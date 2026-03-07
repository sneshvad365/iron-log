CREATE TABLE exercises (
  id           TEXT PRIMARY KEY DEFAULT gen_random_uuid(),
  name         VARCHAR(255) NOT NULL,
  muscle_group VARCHAR(100) NOT NULL,
  type         VARCHAR(50)  NOT NULL,
  created_by   TEXT REFERENCES users(id) ON DELETE CASCADE,
  created_at   TIMESTAMP NOT NULL DEFAULT NOW()
);
-- created_by = NULL means it's a global/seed exercise visible to everyone
