-- MariaDB 컨테이너를 처음 만들 때 한 번 실행되는 초기 스키마
-- predictdo, predictdoweek 테이블은 애플리케이션(JPA ddl-auto=update)이 생성한다.
-- waterdata, weather는 mldata 뷰가 참조하므로 여기서 먼저 만든다.

CREATE TABLE IF NOT EXISTS waterdata (
    num    BIGINT NOT NULL AUTO_INCREMENT,
    time   DATETIME(6),
    tankid VARCHAR(255),
    wdo    FLOAT,
    wt     FLOAT,
    ph     FLOAT,
    sa     FLOAT,
    PRIMARY KEY (num),
    INDEX idx_waterdata_tankid_time (tankid, time)
);

CREATE TABLE IF NOT EXISTS weather (
    time DATETIME(6) NOT NULL,
    swt  FLOAT,
    wdir SMALLINT,
    ws   FLOAT,
    ssa  FLOAT,
    sat  FLOAT,
    sap  FLOAT,
    swh  FLOAT,
    scd  FLOAT,
    scs  FLOAT,
    PRIMARY KEY (time)
);

-- 예측 모델 입력용 뷰: 수조 데이터에 같은 분(minute)의 기상 데이터를 붙인다.
-- 원래 RDS의 뷰 정의가 남아 있지 않아 엔티티(MldataView) 컬럼과 수집 시각 규칙을 보고 재구성한 것이다.
CREATE OR REPLACE VIEW mldata AS
SELECT w.num,
       w.time,
       w.tankid,
       w.wdo,
       w.wt,
       w.ph,
       w.sa,
       e.swt,
       e.wdir,
       e.ws,
       e.ssa,
       e.sat,
       e.sap,
       e.swh,
       e.scd,
       e.scs
FROM waterdata w
JOIN weather e ON e.time = DATE_FORMAT(w.time, '%Y-%m-%d %H:%i:00');
