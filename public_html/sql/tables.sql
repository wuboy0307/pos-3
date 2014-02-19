DROP TABLE IF EXISTS user;
CREATE TABLE user (
    user_id			integer NOT NULL AUTO_INCREMENT,
    login			varchar(64) NOT NULL,
    pin				varchar(16) NOT NULL,
    is_admin		char(1) NOT NULL DEFAULT 'N',
    is_active        char(1) NOT NULL DEFAULT 'Y',
    updateTimestamp	timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY(user_id)
);
CREATE UNIQUE INDEX login ON user (login);

insert into user (login,pin,is_admin) values ('geoff','5555','N');
insert into user (login,pin,is_admin) values ('binh','1234','Y');
insert into user (login,pin,is_admin) values ('pham','4321','Y');

insert into user (user_id,login,pin,is_admin) values (0, 'admin','1234','Y');
insert into user (user_id,login,pin,is_admin) values (-1, 'DEVICE','1234','N');

DROP TABLE IF EXISTS item;
CREATE TABLE item (
    item_id         varchar(64) NOT NULL,
    description     varchar(128) NOT NULL,
    price           DECIMAL(6,2) NOT NULL,
    user_id         integer NOT NULL,
    updateTimestamp timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY(item_id)
);

CREATE INDEX itemDesc ON item (description);

insert into item values ('001','Item 1',9.99,1,null);
insert into item values ('002','Item 2',5.50,1,null);
insert into item values ('003','Item 3',19.99,1,null);

DROP TABLE IF EXISTS watermark;
CREATE TABLE watermark (
    device_id       varchar(96) NOT NULL,
    lastTimestamp   timestamp,
    PRIMARY KEY(device_id)
);