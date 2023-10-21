CREATE TABLE IF NOT EXISTS events (
    id        INTEGER                    NOT NULL PRIMARY KEY AUTO_INCREMENT,

    created   TIMESTAMP                  NOT NULL,

    title     VARCHAR(255)               NOT NULL
);

CREATE TABLE IF NOT EXISTS participants (
    id        INTEGER                    NOT NULL,
    email     VARCHAR(255)               NOT NULL,
    status    VARCHAR(255)               NOT NULL,
    FOREIGN KEY (id) REFERENCES events (id)
);
