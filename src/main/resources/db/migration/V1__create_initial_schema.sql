CREATE TABLE event_index
(
    id uuid NOT NULL,
    collection_unique_id character varying(255),
    is_complete boolean NOT NULL,
    user_id character varying(255),
    CONSTRAINT event_index_pkey PRIMARY KEY (id)
);

CREATE TABLE collection_on_air
(
    id uuid NOT NULL,
    class_id character varying(255),
    collection_id character varying(255),
    CONSTRAINT collection_on_air_pkey PRIMARY KEY (id)
);

CREATE TABLE event
(
    id uuid NOT NULL,
    event_body jsonb,
    event_index uuid NOT NULL,
    CONSTRAINT event_pkey PRIMARY KEY (id),
--    CONSTRAINT validate_description CHECK(length(event_body->>'description')>0 AND (event_body->>'description') IS NOT NULL),
    CONSTRAINT fk_esgg8c38j1atwo26hlvmurrtn FOREIGN KEY (event_index)
        REFERENCES event_index (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

