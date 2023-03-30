CREATE TABLE IF NOT EXISTS articles (
    id      INTEGER         NOT NULL PRIMARY KEY AUTO_INCREMENT,
    title   VARCHAR(255)    NOT NULL,
    text    TEXT            NOT NULL,
    content TEXT            NOT NULL,
    FULLTEXT(title, text)
) ENGINE=InnoDB;
