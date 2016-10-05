
DROP EXTENSION IF EXISTS "uuid-ossp";

CREATE EXTENSION "uuid-ossp";


DROP TYPE IF EXISTS LMS;

CREATE TYPE LMS AS ENUM ('quizzes', 'gooru', 'its-learning');


CREATE TABLE profile
(
    id              UUID        PRIMARY KEY,
    external_id     VARCHAR(36),
    profile_body    JSONB,
    created_at      TIMESTAMP   DEFAULT current_timestamp
);

CREATE INDEX profile_external_id_md5_idx ON profile (DECODE(MD5(external_id), 'HEX') NULLS LAST);


CREATE TABLE collection
(
    id                  UUID        PRIMARY KEY,
    external_id         VARCHAR(36),
    is_collection       BOOLEAN     NOT NULL DEFAULT TRUE,
    owner_profile_id    UUID        REFERENCES profile(id),
    lms_id              LMS         NOT NULL DEFAULT 'quizzes',
    collection_body     JSONB,
    is_deleted          BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP   DEFAULT current_timestamp
);

CREATE INDEX collection_external_id_md5_idx ON collection (DECODE(MD5(external_id), 'HEX') NULLS LAST);

-- To use this index you have to do something like this
-- SELECT * From collection
-- WHERE DECODE(MD5(external_id), 'HEX') = DECODE(MD5('given-external-id'), 'HEX')
-- AND external_id = given-external-id -- who knows when do we get a collision?

CREATE TABLE resource
(
    id                  UUID        PRIMARY KEY,
    external_id         VARCHAR(36) NOT NULL,
    collection_id       UUID        REFERENCES collection(id),
    is_resource         BOOLEAN     NOT NULL DEFAULT TRUE,
    owner_profile_id    UUID        REFERENCES profile(id),
    lms_id              LMS         NOT NULL DEFAULT 'quizzes',
    resource_body       JSONB,
    is_deleted          BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP   DEFAULT current_timestamp
);

CREATE INDEX resource_external_id_md5_idx ON resource (DECODE(MD5(external_id), 'HEX') NULLS LAST);

CREATE TABLE context
(
    id                  UUID        PRIMARY KEY,
    collection_id       UUID        REFERENCES collection(id),
    context_body        JSONB,
    created_at          TIMESTAMP   DEFAULT current_timestamp
);


-- These table are deprecated.
CREATE TABLE event_index
(
    id                      uuid        primary key,
    collection_unique_id    character varying(255),
    is_complete             boolean NOT NULL,
    user_id character varying(255)
);

CREATE TABLE event
(
    id          uuid        primary key,
    event_index uuid        references event_index(id),
    created_at  timestamp   default current_timestamp,
    event_body  jsonb
--    CONSTRAINT validate_description CHECK(length(event_body->>'description')>0 AND (event_body->>'description') IS NOT NULL),
);

CREATE TABLE collection_on_air
(
    id uuid NOT NULL,
    class_id character varying(255),
    collection_id character varying(255),
    CONSTRAINT collection_on_air_pkey PRIMARY KEY (id)
);


-- Filling temporal data
Insert Into profile (id) values(uuid_generate_v1mc());
Insert Into collection (id, owner_profile_id) values(uuid_generate_v1mc(), (Select id From profile Limit 1));
Insert Into collection (id, owner_profile_id) values(uuid_generate_v1mc(), (Select id From profile Limit 1));
Insert Into collection (id, owner_profile_id) values(uuid_generate_v1mc(), (Select id From profile Limit 1));
