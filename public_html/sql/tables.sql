DROP TABLE IF EXISTS user;
CREATE TABLE user (
    user_id			    integer NOT NULL AUTO_INCREMENT,
    login			    varchar(64) NOT NULL,
    pin				    varchar(16) NOT NULL,
    is_admin		    char(1) NOT NULL DEFAULT 'N',
    is_active           char(1) NOT NULL DEFAULT 'Y',
    update_device_id    varchar(96) NOT NULL DEFAULT 'SYSTEM',
    update_user_id      integer NOT NULL DEFAULT -1,
    updateTimestamp	    timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
    item_id             varchar(64) NOT NULL,
    description         varchar(128) NOT NULL,
    price               varchar(32) NOT NULL,
    update_device_id    varchar(96) NOT NULL DEFAULT 'SYSTEM',
    update_user_id      integer NOT NULL DEFAULT -1,
    updateTimestamp     timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY(item_id)
);

CREATE INDEX itemDesc ON item (description);

insert into item (item_id,description,price) values ('001','Item 1','9.99');
insert into item (item_id,description,price) values ('002','Item 2','5.50');
insert into item (item_id,description,price) values ('003','Item 3','19.99');

DROP TABLE IF EXISTS watermark;
CREATE TABLE watermark (
    device_id       varchar(96) NOT NULL,
    lastTimestamp   timestamp,
    PRIMARY KEY(device_id)
);

DROP TABLE IF EXISTS system_settings;
CREATE TABLE system_settings (
    setting_id      varchar(32) NOT NULL,
    setting_value   varchar(128),
    PRIMARY KEY (setting_id)
);

insert into system_settings values ('simulate_down_nice','0');
insert into system_settings values ('simulate_down_broken','0');

