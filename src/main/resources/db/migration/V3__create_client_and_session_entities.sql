
CREATE TYPE content_provider AS ENUM ('quizzes', 'gooru');

CREATE TABLE client
(
  id                  UUID          PRIMARY KEY,
  name                VARCHAR(50)   NOT NULL,
  description         TEXT,
  is_active           BOOLEAN       NOT NULL DEFAULT TRUE,
  api_key             UUID          NOT NULL,
  api_secret          BYTEA         NOT NULL,
  created_at          TIMESTAMP     NOT NULL DEFAULT current_timestamp,
  updated_at          TIMESTAMP     NOT NULL DEFAULT current_timestamp
);
CREATE INDEX client_api_key_idx ON client (api_key);
CREATE TRIGGER client_updated_at_trigger
  BEFORE UPDATE
  ON client
  FOR EACH ROW EXECUTE PROCEDURE _update_updated_at();

CREATE TABLE session
(
  id                  UUID          PRIMARY KEY,
  profile_id          UUID          NOT NULL REFERENCES profile (id),
  created_at          TIMESTAMP     NOT NULL DEFAULT current_timestamp,
  last_access_at      TIMESTAMP     NOT NULL DEFAULT current_timestamp
);

-- Inserts defaults clients (Quizzes, Gooru and ItsLearning)
INSERT INTO client (id, name, description, api_key, api_secret)
  VALUES ('c4b2b608-059e-432a-b358-a0dd0c48a80d', 'Quizzes', 'Quizzes Web Application client',
          'f6985f53-3607-40ae-8404-0a565dfd329c', DECODE(MD5('ypCpfRt=qmnWX&C#Z#Xn*V#2mk_MGjd@'), 'HEX'));
INSERT INTO client (id, name, description, api_key, api_secret)
  VALUES ('8d8068c6-71e3-46f1-a169-2fceb3ed674b', 'Gooru', 'Gooru Web Application client',
          'e1bafe95-dc2c-4c72-9dde-6a0d1bc085fa', DECODE(MD5('GBVYr4rdgcv8Ys@=^?*eRfKgK5j2X@Yj'), 'HEX'));
INSERT INTO client (id, name, description, api_key, api_secret)
  VALUES ('732d2960-60e4-447b-a3d9-ef9402ed227a', 'ItsLearning', 'ItsLearning Web Application client',
          'c4b681dd-e863-42ac-a985-27b726226a6d', DECODE(MD5('tC8k^Ge7H5H9Bz+9-!E5YuKBaL*BaDJ!'), 'HEX'));

-- Adds client_id column to profile table and sets Quizzes client id
ALTER TABLE profile ADD COLUMN client_id UUID REFERENCES client (id);
UPDATE profile SET client_id = 'c4b2b608-059e-432a-b358-a0dd0c48a80d';
ALTER TABLE profile ALTER COLUMN client_id SET NOT NULL;