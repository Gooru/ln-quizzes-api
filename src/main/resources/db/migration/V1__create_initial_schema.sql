
DROP EXTENSION IF EXISTS "uuid-ossp";
-- CREATE EXTENSION "uuid-ossp";

DROP TYPE IF EXISTS LMS;
CREATE TYPE LMS AS ENUM ('quizzes', 'gooru', 'its_learning');

CREATE TABLE profile
(
    id              UUID        PRIMARY KEY,
    external_id     VARCHAR(50) NOT NULL,
    lms_id          LMS         NOT NULL DEFAULT 'quizzes',
    profile_data    JSONB,
    created_at      TIMESTAMP   NOT NULL DEFAULT current_timestamp,
    CONSTRAINT profile_external_id_lms_id_uc UNIQUE (external_id, lms_id)
);
CREATE INDEX profile_external_id_md5_idx ON profile (DECODE(MD5(external_id), 'HEX'));
-- To use this index you have to do something like this
-- SELECT * From profile
-- WHERE DECODE(MD5(external_id), 'HEX') = DECODE(MD5('given-external-id'), 'HEX')
-- AND external_id = given-external-id -- who knows when do we get a collision?

CREATE TABLE collection
(
    id                  UUID        PRIMARY KEY,
    external_id         VARCHAR(50) NOT NULL,
    external_parent_id  VARCHAR(50) NOT NULL,
    lms_id              LMS         NOT NULL DEFAULT 'quizzes',
    is_collection       BOOLEAN     NOT NULL DEFAULT TRUE,
    owner_profile_id    UUID        NOT NULL REFERENCES profile(id),
    collection_data     JSONB,
    is_locked           BOOLEAN     NOT NULL DEFAULT FALSE,
    is_deleted          BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP   NOT NULL DEFAULT current_timestamp,
    CONSTRAINT collection_external_id_lms_id_uc UNIQUE (external_id, lms_id)
);
CREATE INDEX collection_external_id_md5_idx ON collection (DECODE(MD5(external_id), 'HEX'));
CREATE INDEX collection_owner_profile_id_idx ON collection (owner_profile_id);

CREATE TABLE resource
(
    id                  UUID        PRIMARY KEY,
    external_id         VARCHAR(50) NOT NULL,
    lms_id              LMS         NOT NULL DEFAULT 'quizzes',
    collection_id       UUID        NOT NULL REFERENCES collection(id),
    is_resource         BOOLEAN     NOT NULL DEFAULT TRUE,
    owner_profile_id    UUID        NOT NULL REFERENCES profile(id),
    resource_data       JSONB,
    sequence            SMALLINT    NOT NULL DEFAULT 0,
    is_deleted          BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP   NOT NULL DEFAULT current_timestamp,
    CONSTRAINT resource_external_id_lms_id_uc UNIQUE (external_id, lms_id)
);
CREATE INDEX resource_external_id_md5_idx ON resource (DECODE(MD5(external_id), 'HEX'));
CREATE INDEX resource_collection_id_idx ON resource (collection_id);
CREATE INDEX resource_owner_profile_id_idx ON resource (owner_profile_id);

CREATE TABLE "group"
(
    id                  UUID        PRIMARY KEY,
    owner_profile_id    UUID        NOT NULL REFERENCES profile(id),
    group_data          JSONB,
    created_at          TIMESTAMP   NOT NULL DEFAULT current_timestamp
);
CREATE INDEX group_owner_profile_id_idx ON resource (owner_profile_id);

CREATE TABLE group_profile
(
    id                  UUID        PRIMARY KEY,
    group_id            UUID        NOT NULL REFERENCES "group"(id),
    profile_id          UUID        NOT NULL REFERENCES profile(id),
    created_at          TIMESTAMP   NOT NULL DEFAULT current_timestamp,
    CONSTRAINT group_profile_group_id_profile_id_uc UNIQUE (group_id, profile_id)
);

CREATE TABLE context
(
    id                  UUID        PRIMARY KEY,
    collection_id       UUID        NOT NULL REFERENCES collection(id),
    group_id            UUID        NOT NULL REFERENCES "group"(id),
    context_data        JSONB,
    created_at          TIMESTAMP   NOT NULL DEFAULT current_timestamp,
    CONSTRAINT context_collection_id_group_id_uc UNIQUE (collection_id, group_id)
);

CREATE TABLE context_profile
(
    id                  UUID        PRIMARY KEY,
    context_id          UUID        NOT NULL REFERENCES context(id),
    profile_id          UUID        NOT NULL REFERENCES profile(id),
    current_resource_id UUID        NOT NULL REFERENCES resource(id),
    is_complete         BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP   NOT NULL DEFAULT current_timestamp,
    CONSTRAINT context_profile_context_id_profile_id_uc UNIQUE (context_id, profile_id)
);

CREATE TABLE context_profile_event
(
    id                  UUID        PRIMARY KEY,
    context_profile_id  UUID        NOT NULL REFERENCES context_profile(id),
    resource_id         UUID        NOT NULL REFERENCES resource(id),
    event_data          JSONB,
    created_at          TIMESTAMP   NOT NULL DEFAULT current_timestamp,
    CONSTRAINT context_profile_event_context_profile_id_resource_id_uc UNIQUE (context_profile_id, resource_id)
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
-- Insert Into profile (id, external_id) values(uuid_generate_v1mc(), 'profile-external-id-1');
-- Insert Into collection (id, external_id, owner_profile_id) values(uuid_generate_v1mc(), 'collection-external-id-1', (Select id From profile Limit 1));
-- Insert Into collection (id, external_id, owner_profile_id) values(uuid_generate_v1mc(), 'profile-external-id-1', (Select id From profile Limit 1));
--Insert Into context(id, collection_id, context_data) values(uuid_generate_v1mc(), (Select id From collection Limit 1), '{ "courseId": "abc", "classId": "def", "unitId": "ghi", "lessonId": "jkl" }');
--Insert Into context(id, collection_id, context_data) values(uuid_generate_v1mc(), (Select id From collection Limit 1), '{ "courseId": "a123", "classId": "b123", "unitId": "c123", "lessonId": "d123" }');
