CREATE TABLE forum
(
  slug         CITEXT            NOT NULL,
  title        TEXT,
  userid       INTEGER           NOT NULL,
  posts        INTEGER DEFAULT 0 NOT NULL,
  threads      INTEGER DEFAULT 0 NOT NULL,
  id           SERIAL            NOT NULL
    CONSTRAINT forum_id_pk
    PRIMARY KEY,
  tmp_nickname TEXT
);

CREATE UNIQUE INDEX forums_slug_uindex
  ON forum (slug);

CREATE UNIQUE INDEX forum_id_uindex
  ON forum (id);

CREATE TABLE "user"
(
  about    TEXT,
  email    CITEXT NOT NULL
    CONSTRAINT user_email_pk
    UNIQUE,
  fullname TEXT,
  nickname CITEXT NOT NULL
    CONSTRAINT user_nickname_pk
    UNIQUE,
  id       SERIAL NOT NULL
    CONSTRAINT user_id_pk
    PRIMARY KEY
);

CREATE UNIQUE INDEX user_email_uindex
  ON "user" (email);

CREATE UNIQUE INDEX users_nickname_uindex
  ON "user" (nickname);

CREATE UNIQUE INDEX user_id_uindex
  ON "user" (id);

ALTER TABLE forum
  ADD CONSTRAINT forum_user_id_fk
FOREIGN KEY (userid) REFERENCES "user";

CREATE TABLE thread
(
  userid        INTEGER                                NOT NULL
    CONSTRAINT thread_user_id_fk
    REFERENCES "user",
  id            SERIAL                                 NOT NULL
    CONSTRAINT thread_pkey
    PRIMARY KEY,
  created       TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
  forumid       INTEGER                                NOT NULL
    CONSTRAINT thread_forum_id_fk
    REFERENCES forum,
  messagetext   TEXT                                   NOT NULL,
  slug          CITEXT,
  title         TEXT,
  votes         INTEGER DEFAULT 0                      NOT NULL,
  tmp_forumslug CITEXT,
  tmp_nickname  CITEXT
);

CREATE UNIQUE INDEX thread_id_uindex
  ON thread (id);

CREATE UNIQUE INDEX thread_slug_uindex
  ON thread (slug);

CREATE INDEX thread_created_index
  ON thread (created);

CREATE FUNCTION increment_thread_count()
  RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
  UPDATE forum
  SET threads = threads + 1
  WHERE id = NEW.forumid;
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
  userid         INTEGER               NOT NULL
    CONSTRAINT messages_user_id_fk
    REFERENCES "user",
  created        TIMESTAMP WITH TIME ZONE,
  id             SERIAL                NOT NULL
    CONSTRAINT messages_pkey
    PRIMARY KEY,
  isedited       BOOLEAN DEFAULT FALSE NOT NULL,
  message        TEXT,
  parentid       INTEGER DEFAULT 0,
  threadid       INTEGER               NOT NULL
    CONSTRAINT messages_thread_id_fk
    REFERENCES thread,
  tmp_nickname   CITEXT,
  tmp_threadslug CITEXT,
  tmp_forumslug  CITEXT,
  path           BIGINT []
);

CREATE UNIQUE INDEX messages_id_uindex
  ON messages (id);

CREATE FUNCTION message_path()
  RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE newpath BIGINT [];
BEGIN
  IF (NEW.parentid = 0)
  THEN
    NEW.path = newpath || NEW.id :: BIGINT;
    RETURN NEW;
  END IF;

  newpath = (SELECT path
             FROM messages
             WHERE id = NEW.parentid AND threadid = NEW.threadid);

  IF (cardinality(newpath) > 0)
  THEN
    NEW.path = newpath || NEW.id :: BIGINT;
    RETURN NEW;
  END IF;

  RAISE invalid_foreign_key;
END
$$;

CREATE TRIGGER trigger_message_path_update
  BEFORE INSERT
  ON messages
  FOR EACH ROW
EXECUTE PROCEDURE message_path();

CREATE TABLE votes
(
  usernick CITEXT  NOT NULL
    CONSTRAINT votes_user_nickname_fk
    REFERENCES "user" (nickname),
  voice    INTEGER NOT NULL,
  threadid INTEGER NOT NULL
    CONSTRAINT votes_thread_id_fk
    REFERENCES thread,
  CONSTRAINT votes_usernick_threadid_pk
  UNIQUE (usernick, threadid)
);

CREATE FUNCTION add_vote()
  RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
  UPDATE thread
  SET votes = thread.votes + NEW.voice
  WHERE id = NEW.threadid;
  RETURN NEW;
END
$$;

CREATE TRIGGER trigger_add_vote
  AFTER INSERT
  ON votes
  FOR EACH ROW
EXECUTE PROCEDURE add_vote();

CREATE FUNCTION update_vote()
  RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
  UPDATE thread
  SET votes = thread.votes + (NEW.voice - OLD.voice)
  WHERE id = NEW.threadid;
  RETURN NEW;
END
$$;

CREATE TRIGGER trigger_update_vote
  AFTER UPDATE
  ON votes
  FOR EACH ROW
EXECUTE PROCEDURE update_vote();

-- missing source code for citextin
;

-- missing source code for citextout
;

-- missing source code for citextrecv
;

-- missing source code for citextsend
;

-- missing source code for citext
;

-- missing source code for citext
;

-- missing source code for citext
;

-- missing source code for citext_eq
;

-- missing source code for citext_ne
;

-- missing source code for citext_lt
;

-- missing source code for citext_le
;

-- missing source code for citext_gt
;

-- missing source code for citext_ge
;

-- missing source code for citext_cmp
;

-- missing source code for citext_hash
;

-- missing source code for citext_smaller
;

-- missing source code for citext_larger
;

-- missing source code for min
;

-- missing source code for max
;

-- missing source code for texticlike
;

-- missing source code for texticnlike
;

-- missing source code for texticregexeq
;

-- missing source code for texticregexne
;

-- missing source code for texticlike
;

-- missing source code for texticnlike
;

-- missing source code for texticregexeq
;

-- missing source code for texticregexne
;

CREATE FUNCTION regexp_matches(CITEXT, CITEXT)
  RETURNS SETOF TEXT []
IMMUTABLE
STRICT
ROWS 1
LANGUAGE SQL
AS $$
SELECT pg_catalog.regexp_matches($1 :: pg_catalog.TEXT, $2 :: pg_catalog.TEXT, 'i');
$$;

CREATE FUNCTION regexp_matches(CITEXT, CITEXT, TEXT)
  RETURNS SETOF TEXT []
IMMUTABLE
STRICT
ROWS 10
LANGUAGE SQL
AS $$
SELECT pg_catalog.regexp_matches($1 :: pg_catalog.TEXT, $2 :: pg_catalog.TEXT, CASE WHEN pg_catalog.strpos($3, 'c') = 0
  THEN $3 || 'i'
                                                                               ELSE $3 END);
$$;

CREATE FUNCTION regexp_replace(CITEXT, CITEXT, TEXT)
  RETURNS TEXT
IMMUTABLE
STRICT
LANGUAGE SQL
AS $$
SELECT pg_catalog.regexp_replace($1 :: pg_catalog.TEXT, $2 :: pg_catalog.TEXT, $3, 'i');
$$;

CREATE FUNCTION regexp_replace(CITEXT, CITEXT, TEXT, TEXT)
  RETURNS TEXT
IMMUTABLE
STRICT
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
STRICT
LANGUAGE SQL
AS $$
SELECT pg_catalog.regexp_split_to_array($1 :: pg_catalog.TEXT, $2 :: pg_catalog.TEXT, 'i');
$$;

CREATE FUNCTION regexp_split_to_array(CITEXT, CITEXT, TEXT)
  RETURNS TEXT []
IMMUTABLE
STRICT
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
STRICT
LANGUAGE SQL
AS $$
SELECT pg_catalog.regexp_split_to_table($1 :: pg_catalog.TEXT, $2 :: pg_catalog.TEXT, 'i');
$$;

CREATE FUNCTION regexp_split_to_table(CITEXT, CITEXT, TEXT)
  RETURNS SETOF TEXT
IMMUTABLE
STRICT
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
STRICT
LANGUAGE SQL
AS $$
SELECT pg_catalog.strpos(pg_catalog.lower($1 :: pg_catalog.TEXT), pg_catalog.lower($2 :: pg_catalog.TEXT));
$$;

CREATE FUNCTION replace(CITEXT, CITEXT, CITEXT)
  RETURNS TEXT
IMMUTABLE
STRICT
LANGUAGE SQL
AS $$
SELECT pg_catalog.regexp_replace($1 :: pg_catalog.TEXT,
                                 pg_catalog.regexp_replace($2 :: pg_catalog.TEXT, '([^a-zA-Z_0-9])', E'\\\\\\1', 'g'),
                                 $3 :: pg_catalog.TEXT, 'gi');
$$;

CREATE FUNCTION split_part(CITEXT, CITEXT, INTEGER)
  RETURNS TEXT
IMMUTABLE
STRICT
LANGUAGE SQL
AS $$
SELECT (pg_catalog.regexp_split_to_array($1 :: pg_catalog.TEXT,
                                         pg_catalog.regexp_replace($2 :: pg_catalog.TEXT, '([^a-zA-Z_0-9])', E'\\\\\\1',
                                                                   'g'), 'i')) [$3];
$$;

CREATE FUNCTION translate(CITEXT, CITEXT, TEXT)
  RETURNS TEXT
IMMUTABLE
STRICT
LANGUAGE SQL
AS $$
SELECT pg_catalog.translate(pg_catalog.translate($1 :: pg_catalog.TEXT, pg_catalog.lower($2 :: pg_catalog.TEXT), $3),
                            pg_catalog.upper($2 :: pg_catalog.TEXT), $3);
$$;

CREATE FUNCTION f(x INTEGER)
  RETURNS INTEGER
LANGUAGE plpgsql
AS $$
BEGIN
  RETURN x;
END;
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

