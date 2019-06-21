DROP TABLE log IF EXISTS;

CREATE TABLE log  (
    log_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    data DATETIME,
    ip VARCHAR(40),
    request VARCHAR(15),
    status INT(3),
    user_agent VARCHAR(200)
);
