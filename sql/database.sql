CREATE TABLE forum
(
  slug     TEXT              NOT NULL
    CONSTRAINT forum_slug_pk
    PRIMARY KEY,
  title    TEXT,
  nickname TEXT              NOT NULL,
  posts    INTEGER DEFAULT 0 NOT NULL,
  threads  INTEGER DEFAULT 0 NOT NULL
);

CREATE UNIQUE INDEX forums_slug_uindex
  ON forum (slug);

CREATE TABLE "user"
(
  about    TEXT,
  email    TEXT NOT NULL,
  fullname TEXT NOT NULL,
  nickname TEXT NOT NULL
    CONSTRAINT user_nickname_pk_prim
    PRIMARY KEY
    CONSTRAINT user_nickname_pk
    UNIQUE
);

CREATE UNIQUE INDEX users_nickname_uindex
  ON "user" (nickname);

CREATE INDEX user_email_index
  ON "user" (email);

ALTER TABLE forum
  ADD CONSTRAINT forum_user_nickname_fk
FOREIGN KEY (nickname) REFERENCES "user";

CREATE TABLE thread
(
  authornick    TEXT   NOT NULL
    CONSTRAINT thread_user_nickname_fk
    REFERENCES "user",
  id            SERIAL NOT NULL
    CONSTRAINT thread_pkey
    PRIMARY KEY,
  created       TIMESTAMP,
  forumnickname TEXT   NOT NULL
    CONSTRAINT thread_forum_slug_fk
    REFERENCES forum,
  messagetext   TEXT   NOT NULL,
  slug          CHAR(50),
  title         TEXT
);

CREATE UNIQUE INDEX thread_id_uindex
  ON thread (id);

CREATE UNIQUE INDEX thread_slug_uindex
  ON thread (slug);

CREATE TABLE messages
(
  authornick    TEXT                  NOT NULL
    CONSTRAINT messages_user_nickname_fk
    REFERENCES "user",
  created       TIMESTAMP,
  forumnickname TEXT
    CONSTRAINT messages_forum_slug_fk
    REFERENCES forum,
  id            SERIAL                NOT NULL
    CONSTRAINT messages_pkey
    PRIMARY KEY,
  isedited      BOOLEAN DEFAULT FALSE NOT NULL,
  message       TEXT,
  parentid      INTEGER DEFAULT 0,
  threadid      INTEGER               NOT NULL
    CONSTRAINT messages_thread_id_fk
    REFERENCES thread
);

CREATE UNIQUE INDEX messages_id_uindex
  ON messages (id);

CREATE TABLE votes
(
  nickname TEXT    NOT NULL
    CONSTRAINT votes_user_nickname_fk
    REFERENCES "user",
  voice    INTEGER NOT NULL,
  threadid INTEGER NOT NULL
    CONSTRAINT votes_thread_id_fk
    REFERENCES thread
);
