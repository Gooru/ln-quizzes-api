-- the constraint adding below implicitly creates an index with owner_profile_id, so we drop this one
DROP INDEX IF EXISTS collection_owner_profile_id_idx;

-- this index means that only one "owner - external parent" combination is possible
CREATE UNIQUE INDEX collection_owner_profile_id_external_parent_id_idx ON collection (owner_profile_id, DECODE(MD5(external_parent_id), 'HEX'));