var index=function(req, res){
    // database 객체 참조
	var pool = req.app.get('database').pool;
    
    if(pool){
        getData(pool, function(err, rows){
            // 오류가 발생했을 때 클라이언트로 오류 전송
            if(err){
                console.error('데이터 조회 중 오류 발생 : '+err.stack);
                res.writeHead('200', {'Content-Type':'text/html;charset=utf8'});
                res.write('<h2>데이터 조회 중 오류 발생</h2>');
                res.write('<p>'+err.stack+'</p>');
                res.end();
                
                return;
            }
            
            if(rows){
                console.dir(rows);
            
                
                // 뷰 템플릿을 이용하여 렌더링한 후 전송
                var context={rows:rows};
                req.app.render('datalist', context, function(err, html){
                    if(err){throw err;}
                    res.end(html);
                });
            }
        });
    }  
}

var farmdata=function(req, res){
     console.log('farm 모듈 안에 있는 farmdata 호출됨.');
    
    // database 객체 참조
	var pool = req.app.get('database').pool;
    
    if(pool){
         getData(pool, function(err, rows){
         // 오류가 발생했을 때 클라이언트로 오류 전송
            if(err){
                console.error('데이터 조회 중 오류 발생 : '+err.stack);
                res.writeHead('200', {'Content-Type':'text/html;charset=utf8'});
                res.write('<h2>데이터 조회 중 오류 발생</h2>');
                res.write('<p>'+err.stack+'</p>');
                res.end();
                
                return;
            }
            
            if(rows){
                console.dir(rows);
    
                res.writeHead('200', {'Content-Type':'text/html;charset=utf8'});
                
                res.write(JSON.stringify(rows));
                res.end();
                
            }else{
                res.writeHead('200', {'Content-Type':'text/html;charset=utf8'});
                res.write('<h2>데이터 조회 실패</h2>');
                res.end();
            }
         });
    }
}


var getData=function(pool, callback){
    // 커넥션 풀에서 연결 객체를 가져옵니다.
    pool.getConnection(function(err, conn){
        if(err){
            if(conn){
                conn.release(); // 반드시 해제해줘야 한다.
            }
            callback(err, null);
            return;
        }
        
        console.log('데이터베이스 연결 스레드 아이디 : '+conn.threadId);
        
         // SQL문 실행
        var exec=conn.query("select * from data", function(err, rows){
            conn.release(); // 반드시 해제해준다.
            console.log('실행 대상 SQL : '+exec.sql);
            
            if(rows.length>0){
                console.log('데이터 찾음.');
                callback(null, rows);
            }else{
                console.log("데이터 찾지 못함.");
                callback(null, null);
            }
            
        });
        
});
}

module.exports.index=index;
module.exports.farmdata=farmdata;