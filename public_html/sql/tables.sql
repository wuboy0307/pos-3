DROP TABLE IF EXISTS user;
CREATE TABLE user (
    user_id			integer NOT NULL AUTO_INCREMENT,
    login			varchar(64) NOT NULL,
    pin				varchar(16) NOT NULL,
    is_admin		char(1) NOT NULL DEFAULT 'N',
    is_active        char(1) NOT NULL DEFAULT 'Y',
    createTimestamp	timestamp DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(user_id)
);
CREATE UNIQUE INDEX login ON user (login);

insert into user (login,pin,is_admin) values ('geoff','5555','N');

