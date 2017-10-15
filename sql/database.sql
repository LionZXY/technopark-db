CREATE TABLE forum
(
  slug     CITEXT            NOT NULL
    CONSTRAINT forum_slug_pk
    PRIMARY KEY,
  title    TEXT,
  nickname CITEXT            NOT NULL,
  posts    INTEGER DEFAULT 0 NOT NULL,
  threads  INTEGER DEFAULT 0 NOT NULL
);

CREATE UNIQUE INDEX forums_slug_uindex
  ON forum (slug);

CREATE TABLE "user"
(
  about    TEXT,
  email    CITEXT NOT NULL
    CONSTRAINT user_email_pk
    UNIQUE,
  fullname TEXT,
  nickname CITEXT NOT NULL
    CONSTRAINT user_nickname_pk_prim
    PRIMARY KEY
    CONSTRAINT user_nickname_pk
    UNIQUE
);

CREATE UNIQUE INDEX user_email_uindex
  ON "user" (email);

CREATE UNIQUE INDEX users_nickname_uindex
  ON "user" (nickname);

ALTER TABLE forum
  ADD CONSTRAINT forum_user_nickname_fk
FOREIGN KEY (nickname) REFERENCES "user";

CREATE TABLE thread
(
  authornick  CITEXT                  NOT NULL
    CONSTRAINT thread_user_nickname_fk
    REFERENCES "user",
  id          SERIAL                  NOT NULL
    CONSTRAINT thread_pkey
    PRIMARY KEY,
  created     TIMESTAMP DEFAULT now() NOT NULL,
  forumslug   CITEXT                  NOT NULL
    CONSTRAINT thread_forum_slug_fk
    REFERENCES forum,
  messagetext TEXT                    NOT NULL,
  slug        TEXT,
  title       TEXT,
  votes       INTEGER DEFAULT 0       NOT NULL
);

CREATE UNIQUE INDEX thread_id_uindex
  ON thread (id);

CREATE UNIQUE INDEX thread_slug_uindex
  ON thread (slug);

CREATE FUNCTION increment_thread_count()
  RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
  UPDATE forum
  SET threads = threads + 1
  WHERE slug = NEW.forumslug;
  RETURN NEW;
END
$$;

CREATE TRIGGER trigger_incr_thread
AFTER INSERT
  ON thread
FOR EACH ROW
EXECUTE PROCEDURE increment_thread_count();

CREATE TABLE messages
(
  authornick CITEXT                NOT NULL
    CONSTRAINT messages_user_nickname_fk
    REFERENCES "user",
  created    TIMESTAMP,
  forumslug  CITEXT
    CONSTRAINT messages_forum_slug_fk
    REFERENCES forum,
  id         SERIAL                NOT NULL
    CONSTRAINT messages_pkey
    PRIMARY KEY,
  isedited   BOOLEAN DEFAULT FALSE NOT NULL,
  message    TEXT,
  parentid   INTEGER DEFAULT 0,
  threadid   INTEGER               NOT NULL
    CONSTRAINT messages_thread_id_fk
    REFERENCES thread
);

CREATE UNIQUE INDEX messages_id_uindex
  ON messages (id);

CREATE TABLE votes
(
  nickname CITEXT  NOT NULL
    CONSTRAINT votes_user_nickname_fk
    REFERENCES "user",
  voice    INTEGER NOT NULL,
  threadid INTEGER NOT NULL
    CONSTRAINT votes_thread_id_fk
    REFERENCES thread
);

CREATE FUNCTION regexp_matches(CITEXT, CITEXT)
  RETURNS SETOF TEXT []
IMMUTABLE
LANGUAGE SQL
AS $$
SELECT pg_catalog.regexp_matches($1 :: pg_catalog.TEXT, $2 :: pg_catalog.TEXT, 'i');
$$;

CREATE FUNCTION regexp_matches(CITEXT, CITEXT, TEXT)
  RETURNS SETOF TEXT []
IMMUTABLE
LANGUAGE SQL
AS $$
SELECT pg_catalog.regexp_matches($1 :: pg_catalog.TEXT, $2 :: pg_catalog.TEXT, CASE WHEN pg_catalog.strpos($3, 'c') = 0
  THEN $3 || 'i'
                                                                               ELSE $3 END);
$$;

CREATE FUNCTION regexp_replace(CITEXT, CITEXT, TEXT)
  RETURNS TEXT
IMMUTABLE
LANGUAGE SQL
AS $$
SELECT pg_catalog.regexp_replace($1 :: pg_catalog.TEXT, $2 :: pg_catalog.TEXT, $3, 'i');
$$;

CREATE FUNCTION regexp_replace(CITEXT, CITEXT, TEXT, TEXT)
  RETURNS TEXT
IMMUTABLE
LANGUAGE SQL
AS $$
SELECT
  pg_catalog.regexp_replace($1 :: pg_catalog.TEXT, $2 :: pg_catalog.TEXT, $3, CASE WHEN pg_catalog.strpos($4, 'c') = 0
    THEN $4 || 'i'
                                                                              ELSE $4 END);
$$;

CREATE FUNCTION regexp_split_to_array(CITEXT, CITEXT)
  RETURNS TEXT []
IMMUTABLE
LANGUAGE SQL
AS $$
SELECT pg_catalog.regexp_split_to_array($1 :: pg_catalog.TEXT, $2 :: pg_catalog.TEXT, 'i');
$$;

CREATE FUNCTION regexp_split_to_array(CITEXT, CITEXT, TEXT)
  RETURNS TEXT []
IMMUTABLE
LANGUAGE SQL
AS $$
SELECT pg_catalog.regexp_split_to_array($1 :: pg_catalog.TEXT, $2 :: pg_catalog.TEXT,
                                        CASE WHEN pg_catalog.strpos($3, 'c') = 0
                                          THEN $3 || 'i'
                                        ELSE $3 END);
$$;

CREATE FUNCTION regexp_split_to_table(CITEXT, CITEXT)
  RETURNS SETOF TEXT
IMMUTABLE
LANGUAGE SQL
AS $$
SELECT pg_catalog.regexp_split_to_table($1 :: pg_catalog.TEXT, $2 :: pg_catalog.TEXT, 'i');
$$;

CREATE FUNCTION regexp_split_to_table(CITEXT, CITEXT, TEXT)
  RETURNS SETOF TEXT
IMMUTABLE
LANGUAGE SQL
AS $$
SELECT pg_catalog.regexp_split_to_table($1 :: pg_catalog.TEXT, $2 :: pg_catalog.TEXT,
                                        CASE WHEN pg_catalog.strpos($3, 'c') = 0
                                          THEN $3 || 'i'
                                        ELSE $3 END);
$$;

CREATE FUNCTION strpos(CITEXT, CITEXT)
  RETURNS INTEGER
IMMUTABLE
LANGUAGE SQL
AS $$
SELECT pg_catalog.strpos(pg_catalog.lower($1 :: pg_catalog.TEXT), pg_catalog.lower($2 :: pg_catalog.TEXT));
$$;

CREATE FUNCTION replace(CITEXT, CITEXT, CITEXT)
  RETURNS TEXT
IMMUTABLE
LANGUAGE SQL
AS $$
SELECT pg_catalog.regexp_replace($1 :: pg_catalog.TEXT,
                                 pg_catalog.regexp_replace($2 :: pg_catalog.TEXT, '([^a-zA-Z_0-9])', E'\\\\\\1', 'g'),
                                 $3 :: pg_catalog.TEXT, 'gi');
$$;

CREATE FUNCTION split_part(CITEXT, CITEXT, INTEGER)
  RETURNS TEXT
IMMUTABLE
LANGUAGE SQL
AS $$
SELECT (pg_catalog.regexp_split_to_array($1 :: pg_catalog.TEXT,
                                         pg_catalog.regexp_replace($2 :: pg_catalog.TEXT, '([^a-zA-Z_0-9])', E'\\\\\\1',
                                                                   'g'), 'i')) [$3];
$$;

CREATE FUNCTION translate(CITEXT, CITEXT, TEXT)
  RETURNS TEXT
IMMUTABLE
LANGUAGE SQL
AS $$
SELECT pg_catalog.translate(pg_catalog.translate($1 :: pg_catalog.TEXT, pg_catalog.lower($2 :: pg_catalog.TEXT), $3),
                            pg_catalog.upper($2 :: pg_catalog.TEXT), $3);
$$;

CREATE OPERATOR <> ( PROCEDURE = citext_ne, LEFTARG = CITEXT, RIGHTARG = CITEXT );

CREATE OPERATOR = ( PROCEDURE = citext_eq, LEFTARG = CITEXT, RIGHTARG = CITEXT );

CREATE OPERATOR > ( PROCEDURE = citext_gt, LEFTARG = CITEXT, RIGHTARG = CITEXT );

CREATE OPERATOR >= ( PROCEDURE = citext_ge, LEFTARG = CITEXT, RIGHTARG = CITEXT );

CREATE OPERATOR < ( PROCEDURE = citext_lt, LEFTARG = CITEXT, RIGHTARG = CITEXT );

CREATE OPERATOR <= ( PROCEDURE = citext_le, LEFTARG = CITEXT, RIGHTARG = CITEXT );

CREATE OPERATOR !~ ( PROCEDURE = "public.texticregexne", LEFTARG = CITEXT, RIGHTARG = CITEXT );

CREATE OPERATOR ~ ( PROCEDURE = "public.texticregexeq", LEFTARG = CITEXT, RIGHTARG = CITEXT );

CREATE OPERATOR !~* ( PROCEDURE = "public.texticregexne", LEFTARG = CITEXT, RIGHTARG = CITEXT );

CREATE OPERATOR ~* ( PROCEDURE = "public.texticregexeq", LEFTARG = CITEXT, RIGHTARG = CITEXT );

CREATE OPERATOR !~~ ( PROCEDURE = "public.texticnlike", LEFTARG = CITEXT, RIGHTARG = CITEXT );

CREATE OPERATOR ~~ ( PROCEDURE = "public.texticlike", LEFTARG = CITEXT, RIGHTARG = CITEXT );

CREATE OPERATOR !~~* ( PROCEDURE = "public.texticnlike", LEFTARG = CITEXT, RIGHTARG = CITEXT );

CREATE OPERATOR ~~* ( PROCEDURE = "public.texticlike", LEFTARG = CITEXT, RIGHTARG = CITEXT );

CREATE OPERATOR !~ ( PROCEDURE = "public.texticregexne", LEFTARG = CITEXT, RIGHTARG = TEXT );

CREATE OPERATOR ~ ( PROCEDURE = "public.texticregexeq", LEFTARG = CITEXT, RIGHTARG = TEXT );

CREATE OPERATOR !~* ( PROCEDURE = "public.texticregexne", LEFTARG = CITEXT, RIGHTARG = TEXT );

CREATE OPERATOR ~* ( PROCEDURE = "public.texticregexeq", LEFTARG = CITEXT, RIGHTARG = TEXT );

CREATE OPERATOR !~~ ( PROCEDURE = "public.texticnlike", LEFTARG = CITEXT, RIGHTARG = TEXT );

CREATE OPERATOR ~~ ( PROCEDURE = "public.texticlike", LEFTARG = CITEXT, RIGHTARG = TEXT );

CREATE OPERATOR !~~* ( PROCEDURE = "public.texticnlike", LEFTARG = CITEXT, RIGHTARG = TEXT );

CREATE OPERATOR ~~* ( PROCEDURE = "public.texticlike", LEFTARG = CITEXT, RIGHTARG = TEXT );

