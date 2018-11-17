// MySQL 데이터베이스를 사용할 수 있는 mysql 모듈 불러오기
var mysql=require('mysql');

var database={};

// 초기화를 위해 호출하는 함수
database.init = function(app, config) {
	console.log('init() 호출됨.');
	
	connect(app, config);
}

//데이터베이스에 연결하고 응답 객체의 속성으로 pool 객체 추가
function connect(app, config) {
	console.log('connect() 호출됨.');
	
	// 데이터베이스 연결 : config의 설정 사용
    // MySQL 데이터베이스 연결 설정
    database.pool=mysql.createPool(config.pool);
    
    app.set('database', database);
}

module.exports= database;
