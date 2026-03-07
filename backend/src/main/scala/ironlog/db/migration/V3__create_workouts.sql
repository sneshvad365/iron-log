CREATE TABLE workouts (
  id         TEXT PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id    TEXT REFERENCES users(id) ON DELETE CASCADE NOT NULL,
  title      VARCHAR(255) NOT NULL,
  notes      TEXT,
  started_at TIMESTAMP NOT NULL DEFAULT NOW(),
  ended_at   TIMESTAMP
);
-- ended_at = NULL means session is currently active
