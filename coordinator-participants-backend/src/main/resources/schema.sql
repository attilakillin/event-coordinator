CREATE TABLE IF NOT EXISTS participants (
    id           INTEGER                   NOT NULL PRIMARY KEY AUTO_INCREMENT,

    last_name    VARCHAR(255)              NOT NULL,
    first_name   VARCHAR(255)              NOT NULL,

    email        VARCHAR(255)              NOT NULL,
    address      VARCHAR(255),
    phone_number VARCHAR(100),

    notes        TEXT
);
