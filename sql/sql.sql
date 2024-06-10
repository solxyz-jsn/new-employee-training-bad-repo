CREATE TABLE SPRING_SESSION (
                                PRIMARY_ID CHAR(36) NOT NULL,
                                SESSION_ID CHAR(36) NOT NULL,
                                CREATION_TIME BIGINT NOT NULL,
                                LAST_ACCESS_TIME BIGINT NOT NULL,
                                MAX_INACTIVE_INTERVAL INT NOT NULL,
                                EXPIRY_TIME BIGINT NOT NULL,
                                PRINCIPAL_NAME VARCHAR(100),
                                CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID)
);

CREATE UNIQUE INDEX SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID);
CREATE INDEX SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME);

CREATE TABLE SPRING_SESSION_ATTRIBUTES (
                                           SESSION_PRIMARY_ID CHAR(36) NOT NULL,
                                           ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
                                           ATTRIBUTE_BYTES BYTEA NOT NULL,
                                           CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
                                           CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE
);

CREATE TABLE ACCOUNTS (
                          USER_ID CHAR(36) NOT NULL,
                          IS_ADMIN INTEGER NOT NULL DEFAULT 0, -- 0: false, 1: true
                          EMAIL VARCHAR(255) NOT NULL UNIQUE,
                          USERNAME VARCHAR(100) NOT NULL,
                          PASSWORD CHAR(60) NOT NULL,
                          UPDATED_AT INTEGER NOT NULL,
                          PRIMARY KEY (USER_ID)
);

CREATE TABLE BOOKS (
                          ISBN CHAR(13) NOT NULL,
                          TITLE TEXT NOT NULL,
                          AUTHOR TEXT NOT NULL,
                          PUBLISHER TEXT NOT NULL,
                          DESCRIPTION TEXT,
                          STOCK INTEGER NOT NULL DEFAULT 0,
                          AVAILABLE_STOCK INTEGER NOT NULL DEFAULT 0,
                          CREATED_AT INTEGER NOT NULL,
                          UPDATED_AT INTEGER NOT NULL,
                          PRIMARY KEY (ISBN)
);

CREATE TABLE BOOK_CHECKOUT_HISTORY (
                                 RENTAL_ID CHAR(36) NOT NULL,
                                 ISBN CHAR(13) NOT NULL,
                                 USER_ID CHAR(36) NOT NULL,
                                 RENTAL_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 RETURN_AT TIMESTAMP,
                                 VERSION BIGINT NOT NULL DEFAULT 0,
                                 PRIMARY KEY (RENTAL_ID),
                                 FOREIGN KEY (USER_ID) REFERENCES ACCOUNTS(USER_ID),
                                 FOREIGN KEY (ISBN) REFERENCES BOOKS(ISBN)
);

CREATE UNIQUE INDEX idx_tbl_book_rental
    ON BOOK_CHECKOUT_HISTORY (ISBN, USER_ID)
    WHERE RETURN_AT IS NULL;

insert into ACCOUNTS (USER_ID, IS_ADMIN, EMAIL, USERNAME, PASSWORD, updated_at) VALUES ('f3d6bcdc-6c32-45b6-9aea-1aa6d36b6b17', 0, 'test@solxyz.co.jp', 'テスト 太郎', '$2a$08$uHZP6K8gdWlbog/Nl3.vCu6HJ0Aq6nj5h45wMYfUZLWhW82XDAaYK', strftime('%s', 'now'));
insert into ACCOUNTS (USER_ID, IS_ADMIN, EMAIL, USERNAME, PASSWORD, updated_at) VALUES ('f3d6bcdc-6c32-45b6-9aea-1aa6d36b6b13', 1, 'admin@solxyz.co.jp', '管理 次郎', '$2a$08$uHZP6K8gdWlbog/Nl3.vCu6HJ0Aq6nj5h45wMYfUZLWhW82XDAaYK', strftime('%s', 'now'));

INSERT INTO books (isbn, title, author, publisher, stock, available_stock, description, created_at, updated_at)
VALUES ('9784814400072', '詳解 システム・パフォーマンス 第2版', 'Brendan Gregg', 'オライリージャパン', 3, 2, '本書は、エンタープライズとクラウド環境を対象としたオペレーティングシステムとアプリケーションのパフォーマンス分析と向上について解説します。', strftime('%s', 'now'), strftime('%s', 'now'));
INSERT INTO books (isbn, title, author, publisher, stock, available_stock, description, created_at, updated_at)
VALUES ('9784814400515', '詳解 Rustアトミック操作とロック', 'Mara Bos', 'オライリージャパン', 1, 0, 'Rustでは並行性を持つプログラムを安全に記述することができます。本書はその並行プログラムの基盤となる、アトミック操作とロックの仕組みについての理解を深め、より安全で効率の良いコードを書くための指南書です。難解だと思われがちなアトミック処理、ロック、メモリオーダリングのような低レイヤを詳細に理解し、アーキテクチャやOSによる相違を知ることで、安全で高性能な並行処理プログラムを実装できるようになります。Rustユーザはもちろん非ユーザにとっても低レイヤプログラミングの優れたリソースとなる一冊です。', strftime('%s', 'now'), strftime('%s', 'now'));
INSERT INTO books (isbn, title, author, publisher, stock, available_stock, description, created_at, updated_at)
VALUES ('9784814400690', '入門 継続的デリバリー', 'Christie Wilson', 'オライリージャパン', 1, 1, '継続的デリバリーとは、コード変更を必要に応じて迅速かつ安全に、継続的にリリースできるようにするための開発手法です。本書は、初めて継続的デリバリーに取り組む読者向けに、必要な知識とベストプラクティスをていねいに紹介する入門書です。基本的な概念や技術、アプローチの解説はもとより、章ごとに事例を使用しながら、継続的デリバリーを実践する際に直面するさまざまなシナリオを取り上げ、その全体像・世界観を包括的に理解することができます。',strftime('%s', 'now'), strftime('%s', 'now'));
