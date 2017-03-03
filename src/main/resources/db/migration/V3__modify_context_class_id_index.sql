
DROP INDEX context_class_id_idx;
CREATE INDEX context_class_id_collection_id_idx ON context (class_id, collection_id);
