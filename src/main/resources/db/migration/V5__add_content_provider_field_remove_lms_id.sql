ALTER TABLE collection ADD COLUMN IF NOT EXISTS content_provider content_provider NOT NULL DEFAULT 'quizzes';
ALTER TABLE resource ADD COLUMN IF NOT EXISTS content_provider content_provider NOT NULL DEFAULT 'quizzes';

UPDATE collection SET content_provider=cast(cast(lms_id as VARCHAR) as content_provider);
UPDATE resource SET content_provider=cast(cast(lms_id as VARCHAR) as content_provider);

ALTER TABLE collection DROP COLUMN IF EXISTS lms_id;
ALTER TABLE resource DROP COLUMN IF EXISTS lms_id;

