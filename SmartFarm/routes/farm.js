var farmdata = function (req, res) {
    console.log('farm 모듈 안에 있는 farmdata 호출됨.');

    // database 객체 참조
    var pool = req.app.get('database').pool;

    if (pool) {
        getData(pool, function (err, rows) {
            // 오류가 발생했을 때 클라이언트로 오류 전송
            if (err) {
                console.error('데이터 조회 중 오류 발생 : ' + err.stack);
                res.writeHead('200', {
                    'Content-Type': 'text/html;charset=utf8'
                });
                res.write('<h2>데이터 조회 중 오류 발생</h2>');
                res.write('<p>' + err.stack + '</p>');
                res.end();

                return;
            }

            if (rows) {
                console.dir(rows);

                res.writeHead('200', {
                    'Content-Type': 'text/html;charset=utf8'
                });

                res.write(JSON.stringify(rows));
                res.end();

            } else {
                res.writeHead('200', {
                    'Content-Type': 'text/html;charset=utf8'
                });
                res.write('<h2>데이터 조회 실패</h2>');
                res.end();
            }
        });
    }
}

var controlArduino = function (req, res) {
    console.log('Farm 모듈 안에 있는 controlArduino 호출됨.');

    var paramControl = req.body.op || req.query.op;

    console.log('op : ', paramControl);

    var serialport = req.app.get('serialport');

    serialport.write(paramControl, function (err) {
        if (err) {
            return console.log('Error on write: ', err.message);
        }
        console.log('message written');
    });


    res.writeHead('200', {
        'Content-Type': 'application/json;charset=utf8'
    });
    res.write("{code:'200', 'message':'데이터를 성공적으로 받음'}");
    res.end();

}

var register = function (req, res) {
    console.log('register 모듈 안에 있는 register 호출됨.');

    var token = require('../ClientToken');

    var paramMobile = req.body.mobile || req.query.mobile;
    var paramRegisterationId = req.body.registrationId || req.query.registrationId;

    console.log('요청 파라미터 : ' + paramMobile + ', ' + paramRegisterationId);

    token.setToken(paramRegisterationId);

    // database 객체 참조
    var pool = req.app.get('database').pool;

    // 커넥션 풀에서 연결 객체를 가져옵니다.
    pool.getConnection(function (err, conn) {
        if (err) {
            if (conn) {
                conn.release(); // 반드시 해제해줘야 한다.
            }
            return;
        }

        console.log('데이터베이스 연결 스레드 아이디 : ' + conn.threadId);

        var params = [paramMobile, paramRegisterationId];
        params = params.concat(params);


        var queryStr = "insert into devices (mobile, registrationId) values (?, ?) on duplicate key update mobile=?, registrationId=?"

        // SQL문 실행
        var exec = conn.query(queryStr, params, function (err, rows) {
            conn.release(); // 반드시 해제해준다.
            console.log('실행 대상 SQL : ' + exec.sql);

            if (err) {
                console.error('register 중 오류 발생 : ' + err.stack);
                res.writeHead('200', {
                    'Content-Type': 'text/html;charset=utf8'
                });
                res.write('<h2>register 중 오류 발생</h2>');
                res.write('<p>' + err.stack + '</p>');
                res.end();

                return;
            }

        });
    });

    res.writeHead('200', {
        'Content-Type': 'application/json;charset=utf8'
    });
    res.write("{code:'200', 'message':'데이터를 성공적으로 받음'}");
    res.end();
}

var getRecentDate = function (req, res) {
    // database 객체 참조
    var pool = req.app.get('database').pool;

    pool.getConnection(function (err, conn) {
        if (err) {
            if (conn) {
                conn.release(); // 반드시 해제해줘야 한다.
            }
            callback(err, null);
            return;
        }

        console.log('데이터베이스 연결 스레드 아이디 : ' + conn.threadId);

        sqlStr = "select * from data order by date desc limit 1"

        // SQL문 실행
        var exec = conn.query(sqlStr, function (err, rows) {
            conn.release(); // 반드시 해제해준다.
            console.log('실행 대상 SQL : ' + exec.sql);

            if (rows) {
                console.dir(rows);

                res.writeHead('200', {
                    'Content-Type': 'text/html;charset=utf8'
                });

                res.write(JSON.stringify(rows));
                res.end();
            } else {
                res.writeHead('200', {
                    'Content-Type': 'text/html;charset=utf8'
                });
                res.write('<h2>데이터 조회 실패</h2>');
                res.end();
            }

        });

    });

}

var getDataHistory = function (req, res) {

    var paramYear = req.body.year || req.query.year;
    var paramMonth = req.body.month || req.query.month;
    var paramDay=req.body.day||req.query.day;

    console.log('요청 파라미터 : ' + paramYear + ', ' + paramMonth+', '+paramDay);
    var dateStr=paramYear+"-"+paramMonth+"-"+paramDay;

    
     // database 객체 참조
    var pool = req.app.get('database').pool;

    pool.getConnection(function (err, conn) {
        if (err) {
            if (conn) {
                conn.release(); // 반드시 해제해줘야 한다.
            }
            callback(err, null);
            return;
        }

        console.log('데이터베이스 연결 스레드 아이디 : ' + conn.threadId);

        sqlStr = "select * from data where date(date)='"+dateStr+"'";

        // SQL문 실행
        var exec = conn.query(sqlStr, function (err, rows) {
            conn.release(); // 반드시 해제해준다.
            console.log('실행 대상 SQL : ' + exec.sql);

            if (rows) {
                res.writeHead('200', {
                    'Content-Type': 'text/html;charset=utf8'
                });

                res.write(JSON.stringify(rows));
                res.end();
            } else {
                res.writeHead('200', {
                    'Content-Type': 'text/html;charset=utf8'
                });
                res.write('<h2>데이터 조회 실패</h2>');
                res.end();
            }

        });

    });
    
}

var getData = function (pool, callback) {
    // 커넥션 풀에서 연결 객체를 가져옵니다.
    pool.getConnection(function (err, conn) {
        if (err) {
            if (conn) {
                conn.release(); // 반드시 해제해줘야 한다.
            }
            callback(err, null);
            return;
        }

        console.log('데이터베이스 연결 스레드 아이디 : ' + conn.threadId);

        // SQL문 실행
        var exec = conn.query("select * from data", function (err, rows) {
            conn.release(); // 반드시 해제해준다.
            console.log('실행 대상 SQL : ' + exec.sql);

            if (rows.length > 0) {
                console.log('데이터 찾음.');
                callback(null, rows);
            } else {
                console.log("데이터 찾지 못함.");
                callback(null, null);
            }

        });

    });
}

module.exports.farmdata = farmdata;
module.exports.controlArduino = controlArduino;
module.exports.register = register;
module.exports.getRecentDate = getRecentDate;
module.exports.getDataHistory = getDataHistory;
