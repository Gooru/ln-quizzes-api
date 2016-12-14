CREATE INDEX IF NOT EXISTS collection_external_parent_id_md5_idx ON collection (DECODE(MD5(external_parent_id), 'HEX'));
