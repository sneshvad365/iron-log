CREATE TABLE workout_sets (
  id          TEXT PRIMARY KEY DEFAULT gen_random_uuid(),
  workout_id  TEXT REFERENCES workouts(id) ON DELETE CASCADE NOT NULL,
  exercise_id TEXT REFERENCES exercises(id) NOT NULL,
  set_number  INT  NOT NULL,
  weight_kg   NUMERIC(6,2),
  reps        INT,
  duration_s  INT,
  is_done     BOOLEAN NOT NULL DEFAULT FALSE,
  notes       TEXT,
  logged_at   TIMESTAMP NOT NULL DEFAULT NOW()
);
