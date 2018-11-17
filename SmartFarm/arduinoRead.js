var SerialPort = require('serialport'),
    portName = 'COM3',
    sp = new SerialPort(portName);

sp.on('open', function () {
    console.log('Serial Port OPEN');
    app.set('serialport', sp);
    
    sp.on('data', function (data) {
        var op = data.toString();
        console.log("데이터 읽음 : " + op);

        var pool = req.app.get('database').pool;

        if (pool) {
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
                    date: date,
                    op: op
                }

                // SQL문 실행
                var exec = conn.query("insert into test set ?", DBdata, function (err) {
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
        }
        
    });
});

module.exports=sp;
