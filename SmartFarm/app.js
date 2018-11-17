// Express 기본 모듈 불러오기
var express = require('express'),
    http = require('http'),
    path = require('path');

// Express 미들웨어 불러오기
var bodyParser = require('body-parser'),
    cookieParser = require('cookie-parser'),
    static = require('serve-static'),
    errorHandler = require('errorhandler');

// 오류 핸들러 모듈 사용
var expressErrorHandler = require('express-error-handler');

// Session 미들웨어 불러오기
var expressSession = require('express-session');

// fcm-node
var FCM = require('fcm-node');

var serverKey = 'AAAAkdvIRzw:APA91bF4cnknOty8McdWyv_X1F6DZcgLSKuwuVGZExPT_lqMyk-8dpdys0iT60GGGQTRm3IC_yAo5ACf3mLwYRQr2j42Vl3DGS_b6SFqVqS42ant7a2AxUtD_HFMvgi1fK3dfbMJsFFk';

var farm = require('./routes/farm');
var database = require('./database/database');
var config = require('./config');

// 익스프레스 객체 생성
var app = express();

// 기본 속성 설정
app.set('port', process.env.PORT || config.server_port);

// 뷰 엔진 설정
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');
console.log('뷰 엔진이 ejs로 설정되었습니다.');

// body-parser를 사용해 application/x-www-form-urlencoded 파싱
app.use(bodyParser.urlencoded({
    extended: false
}));

// body-parser를 사용해 application/json 파싱
app.use(bodyParser.json());

// public 폴더를 static으로 오픈
app.use('/public', static(path.join(__dirname, 'public')));

// cookie-parser 설정
app.use(cookieParser());

// 세션 설정
app.use(expressSession({
    secret: 'my key',
    resave: true,
    saveUninitialized: true
}));


var token = require('./ClientToken');

/*
const SerialPort = require('serialport');
const Readline = require('parser-readline');
const sp = new SerialPort('COM3', {
    baudRate: 115200
});

const parser = sp.pipe(new Readline({
    delimiter: "\n"
}));

app.set('serialport', sp);


sp.on('open', function () {
    console.log('Serial Port OPEN');

    parser.on('data', function (data) {

        console.log("데이터 읽음 : " + data);

        var strData = data.toString().trim();
        var regx = /^[a-zA-Z0-9]*\s[0-9]*\s[0-9]*$/;

        if (regx.test(strData)) {
            var strArr = strData.split(" ");

            var pool = database.pool;

            pool.getConnection(function (err, conn) {
                if (err) {
                    if (conn) {
                        conn.release(); // 반드시 해제해줘야 한다.
                    }
                    callback(err, null);
                    return;
                }

                console.log('데이터베이스 연결 스레드 아이디 : ' + conn.threadId);

                require('date-utils');
                var newDate = new Date();
                var date = newDate.toFormat('YYYY-MM-DD HH24:MI:SS');

                // 데이터를 객체로 만든다.
                var DBdata = {
                    boardId: strArr[0],
                    date: date,
                    temperature: strArr[1],
                    humidity: strArr[2]
                }

                // SQL문 실행
                var exec = conn.query("insert into data set ?", DBdata, function (err) {
                    conn.release(); // 반드시 해제해준다.
                    console.log('실행 대상 SQL : ' + exec.sql);

                    if (err) {
                        console.log('SQL 실행 시 오류 발생함.');
                        console.dir(err);

                        callback(err, null);

                        return;
                    }


                });


            });



            var t_tresh = 30;

            if (parseInt(strArr[1]) >= t_tresh) {
                console.log('temperature : ' + strArr[1]);

                var client_token = token.getToken();
                if (client_token != null) {
                    var fcm = new FCM(serverKey);
                    var message = {
                        to: client_token,

                        data: {
                            title: 'Hello',
                            content: '되는가보다'
                        }

                    };

                    fcm.send(message, function (err, response) {
                        if (err) {
                            console.log("Something has gone wrong!");
                        } else {
                            console.log("Successfully sent with response: ", response);
                        }
                    });

                }

            }
        }
    });

});
*/


// 라우터 객체 참조
var router = express.Router();

router.route('/index').get(farm.index);
router.route('/farmdata').get(farm.farmdata);
router.route('/controlArduino').post(farm.controlArduino);
router.route('/register').post(farm.register);
router.route('/getRecentDate').get(farm.getRecentDate);

// 라우터 객체 등록
app.use('/', router);

var errorHandler = expressErrorHandler({
    static: {
        '404': './public/404.html'
    }
});

app.use(expressErrorHandler.httpError(404));
app.use(errorHandler);


// 프로세스 종료 시에 데이터베이스 연결 해제
process.on('SIGTERM', function () {
    console.log("프로세스가 종료됩니다.");
    app.close();
});

app.on('close', function () {
    console.log("Express 서버 객체가 종료됩니다.");
    if (database) {
        database.close();
    }
});


//===== 서버 시작 ====//
http.createServer(app).listen(app.get('port'), function () {
    console.log('서버가 시작되었습니다. 포트 : ' + app.get('port'));

    database.init(app, config);
});
