# IRON LOG — Workout Logger App

## Project Overview
A full-stack workout logging application where users can track gym sessions, log exercises and sets,
monitor personal records, and visualize strength progress over time.

The app has five main areas:
- **Dashboard** — snapshot of the current week: volume, streak, recent sessions, PRs
- **Log Workout** — start a live session, add exercises, log sets in real time with a running timer
- **History** — calendar + list view of all past workouts, expandable to see full detail
- **Exercise Library** — browse, search, and filter all exercises; view per-exercise history and graphs
- **Stats** — full analytics: volume trends, strength progression per lift, workout frequency, PRs

---

## Tech Stack

### Frontend
- **Framework:** Quasar 2 (Vue 3 Composition API)
- **Language:** TypeScript
- **State:** Pinia
- **HTTP:** Axios
- **Charts:** Apache ECharts (via vue-echarts)
- **Routing:** Vue Router (built into Quasar)

### Backend
- **Language:** Scala 3
- **HTTP Framework:** Cask (Li Haoyi)
- **Database ORM:** ScalaSql (Li Haoyi) with PostgreSQL dialect
- **JSON:** uPickle / uJson (Li Haoyi)
- **Password Hashing:** BCrypt (at.favre.lib bcrypt)
- **JWT:** java-jwt (Auth0 Java library — simple, no FP overhead)
- **Build Tool:** sbt

### Database
- **Engine:** PostgreSQL 15+
- **Migrations:** SQL files in db/migration/, run via Flyway or manually

### Infrastructure
- **Local dev tunnel:** ngrok (free static domain)
- **Target deployment:** Railway (Docker)

---

## Project Structure

```
iron-log/
├── CLAUDE.md
├── docker-compose.yml              # Local Postgres
├── backend/
│   ├── build.sbt
│   ├── project/plugins.sbt
│   └── src/main/scala/ironlog/
│       ├── Main.scala              # Cask server entry point
│       ├── Config.scala            # DB url, JWT secret from env vars
│       ├── Database.scala          # ScalaSql connection pool setup
│       ├── Auth.scala              # BCrypt + JWT helpers
│       ├── Middleware.scala        # JWT auth check helper
│       ├── model/
│       │   ├── User.scala          # Case class + ScalaSql table def
│       │   ├── Workout.scala
│       │   ├── Exercise.scala
│       │   ├── WorkoutSet.scala
│       │   └── Json.scala          # uPickle ReadWriter instances
│       ├── routes/
│       │   ├── AuthRoutes.scala
│       │   ├── WorkoutRoutes.scala
│       │   ├── ExerciseRoutes.scala
│       │   ├── SetRoutes.scala
│       │   └── StatsRoutes.scala
│       └── db/migration/
│           ├── V1__create_users.sql
│           ├── V2__create_exercises.sql
│           ├── V3__create_workouts.sql
│           └── V4__create_sets.sql
└── frontend/
    ├── quasar.config.ts
    ├── src/
    │   ├── boot/axios.ts
    │   ├── stores/
    │   │   ├── auth.ts
    │   │   ├── workout.ts
    │   │   ├── exercise.ts
    │   │   └── stats.ts
    │   ├── pages/
    │   │   ├── LoginPage.vue
    │   │   ├── RegisterPage.vue
    │   │   ├── DashboardPage.vue
    │   │   ├── LogWorkoutPage.vue
    │   │   ├── HistoryPage.vue
    │   │   ├── ExercisesPage.vue
    │   │   └── StatsPage.vue
    │   ├── components/
    │   │   ├── WorkoutCard.vue
    │   │   ├── SetRow.vue
    │   │   ├── ExerciseCard.vue
    │   │   ├── ExerciseHistoryChart.vue
    │   │   ├── VolumeChart.vue
    │   │   ├── FrequencyHeatmap.vue
    │   │   ├── MuscleBreakdownChart.vue
    │   │   ├── PRCard.vue
    │   │   └── ActiveSessionTimer.vue
    │   ├── layouts/
    │   │   ├── AuthLayout.vue
    │   │   └── MainLayout.vue
    │   ├── router/index.ts
    │   └── api/
    │       ├── client.ts
    │       ├── auth.ts
    │       ├── workouts.ts
    │       ├── exercises.ts
    │       ├── sets.ts
    │       └── stats.ts
    └── public/
```

---

## Full App Specification

### Page: Dashboard
The first thing a user sees after login. Shows:
- **Weekly summary bar** — workouts done this week, total volume (kg), total time trained
- **Streak counter** — consecutive days with at least one completed workout
- **Weekly volume area chart** — volume per day for the past 7 days (ECharts AreaChart)
- **Recent workouts list** — last 5 sessions as cards: title, date, duration, muscles hit, total volume
- **Personal records panel** — top 4 lifts (Squat, Bench, Deadlift, OHP) with current PR weight
- **Quick start button** — "+ NEW SESSION" opens LogWorkoutPage with a fresh empty session

### Page: Log Workout
Active session page. Only one active session at a time per user.
- **Session header** — editable session title, live running timer (counts up), Finish button
- **Exercise list** — each exercise shows as a card with:
  - Exercise name + muscle group badge
  - Table of sets: SET | WEIGHT (KG) | REPS | DONE
  - Each set row is editable inline (click weight or reps to edit)
  - Checkbox marks a set as done (turns orange when ticked)
  - "+ ADD SET" copies the previous set's weight/reps as default
  - Previous session's data shown as faint hint text per set for reference
- **"+ ADD EXERCISE" button** — opens a searchable modal over the exercise library
- **Finish button** — sets ended_at, calculates duration, redirects to workout summary page

### Page: History
Full workout history with two views:
- **Calendar view** (top) — monthly calendar, days with workouts highlighted in orange.
  Click a day to jump to that workout in the list below.
- **List view** (below) — reverse-chronological expandable cards:
  - Collapsed: title, date, duration, volume kg, muscle group tags
  - Expanded: full set-by-set breakdown per exercise with weights and reps
- **Filter bar** — filter by muscle group chips, date range picker, or title search
- **Delete button** on each card (with confirmation)

### Page: Exercise Library
Browse all exercises (global seed exercises + user's own custom ones).
- **Search bar** + **muscle group filter** chips: All | Chest | Back | Legs | Shoulders | Arms | Core | Cardio
- **Exercise cards grid** — each card shows:
  - Exercise name + muscle group badge + type badge (Strength / Cardio / Bodyweight)
  - User's PR for this exercise (e.g. "100 kg × 1")
  - Date last performed
- **Click an exercise** → Exercise Detail drawer/page:
  - **Estimated 1RM progression chart** — line chart over time using Epley formula
  - **Volume per session** bar chart — kg lifted per workout session
  - **Session history table** — every session this exercise appeared in, with all sets listed
- **"+ CREATE EXERCISE" button** — form with name, muscle group, type

### Page: Stats
Full analytics dashboard.
- **Lifetime stats row** — total workouts, total volume (kg), total hours trained, favourite muscle group
- **Volume over time** — area chart, toggle weekly/monthly, shows total kg per period
- **Workout frequency heatmap** — GitHub contribution-style grid, one cell per day for past 365 days,
  colour intensity based on volume. Click a cell to open that day's workout.
- **Strength progress section**:
  - Dropdown to pick any exercise from the user's history
  - Estimated 1RM progression line chart over time
  - Table of top 5 all-time sets: date, weight, reps, estimated 1RM
- **Muscle group breakdown** — donut chart, % of total volume per muscle group, toggleable by period
- **All-time PRs table** — every exercise the user has logged, sorted by muscle group,
  showing heaviest set and best estimated 1RM

---

## Database Schema

```sql
-- V1
CREATE TABLE users (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email      VARCHAR(255) UNIQUE NOT NULL,
  password   VARCHAR(255) NOT NULL,
  name       VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- V2
CREATE TABLE exercises (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name         VARCHAR(255) NOT NULL,
  muscle_group VARCHAR(100) NOT NULL,
  type         VARCHAR(50)  NOT NULL,
  created_by   UUID REFERENCES users(id) ON DELETE CASCADE,
  created_at   TIMESTAMP NOT NULL DEFAULT NOW()
);
-- created_by = NULL means it's a global/seed exercise visible to everyone

-- V3
CREATE TABLE workouts (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id    UUID REFERENCES users(id) ON DELETE CASCADE NOT NULL,
  title      VARCHAR(255) NOT NULL,
  notes      TEXT,
  started_at TIMESTAMP NOT NULL DEFAULT NOW(),
  ended_at   TIMESTAMP
);
-- ended_at = NULL means session is currently active

-- V4
CREATE TABLE workout_sets (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  workout_id  UUID REFERENCES workouts(id) ON DELETE CASCADE NOT NULL,
  exercise_id UUID REFERENCES exercises(id) NOT NULL,
  set_number  INT  NOT NULL,
  weight_kg   NUMERIC(6,2),
  reps        INT,
  duration_s  INT,
  is_done     BOOLEAN NOT NULL DEFAULT FALSE,
  notes       TEXT,
  logged_at   TIMESTAMP NOT NULL DEFAULT NOW()
);
```

Seed ~50 global exercises across all muscle groups on first run so users have a full library immediately.

---

## API Routes

All routes return `{ "data": ... }` on success or `{ "error": "message" }` on failure.
Protected routes require header: `Authorization: Bearer <token>`

### Auth (public)
```
POST /api/auth/register   { name, email, password }  → { token, user }
POST /api/auth/login      { email, password }         → { token, user }
```

### Workouts (protected)
```
GET    /api/workouts           → [ WorkoutSummary ]
POST   /api/workouts           { title }  → Workout
GET    /api/workouts/active    → WorkoutDetail | null
GET    /api/workouts/:id       → WorkoutDetail (with all sets + exercise names)
PUT    /api/workouts/:id       { title?, notes?, ended_at? }  → Workout
DELETE /api/workouts/:id       → { success: true }
```

### Sets (protected)
```
POST   /api/workouts/:id/sets  { exercise_id, set_number, weight_kg?, reps?, duration_s? }  → WorkoutSet
PUT    /api/sets/:id           { weight_kg?, reps?, is_done?, notes? }  → WorkoutSet
DELETE /api/sets/:id           → { success: true }
```

### Exercises (protected)
```
GET    /api/exercises              ?muscle_group=&type=&search=  → [ Exercise ]
POST   /api/exercises              { name, muscle_group, type }  → Exercise
DELETE /api/exercises/:id          → { success: true }
GET    /api/exercises/:id/history  → { sets, oneRmProgression, volumePerSession }
```

### Stats (protected)
```
GET /api/stats/summary
  → { weeklyVolume, weeklyWorkouts, weeklyMinutes, streak, totalWorkouts }

GET /api/stats/volume?period=weekly|monthly&count=12
  → [ { label: string, volume: number } ]

GET /api/stats/prs
  → [ { exerciseName, weightKg, reps, estimatedOneRm, date } ]

GET /api/stats/progress/:exerciseId
  → [ { date, estimatedOneRm, bestWeightKg, bestReps } ]

GET /api/stats/frequency?days=365
  → [ { date, volume, workoutCount } ]

GET /api/stats/muscle-breakdown?weeks=12
  → [ { muscleGroup, volumeKg, percentage } ]
```

---

## Auth Flow

1. Register → BCrypt hash password (cost 12) → insert user → issue JWT → return token
2. Login → look up user by email → BCrypt verify → issue JWT → return token
3. JWT payload: `{ userId: String, email: String }` + exp (24h)
4. JWT signed with HS256 using JWT_SECRET env var
5. Quasar stores token in **Pinia auth store (memory only)** — NOT localStorage
6. Axios request interceptor adds `Authorization: Bearer <token>` automatically
7. Page refresh = back to login (intentional, keeps it simple)
8. Backend: `Auth.requireUser(request)` validates JWT header, returns userId or throws 401

---

## Backend Conventions (Cask + ScalaSql + uPickle)

### Route structure
```scala
object WorkoutRoutes extends cask.Routes:
  @cask.get("/api/workouts")
  def list(request: cask.Request) =
    val userId = Auth.requireUser(request)
    val workouts = db.run(WorkoutTable.select.filter(_.userId === userId))
    ok(workouts)

  @cask.post("/api/workouts")
  def create(request: cask.Request) =
    val userId = Auth.requireUser(request)
    val body   = ujson.read(request.text())
    val title  = body("title").str
    // insert and return
```

### Response helpers
```scala
val jsonHeaders = Seq("Content-Type" -> "application/json")

def ok[T: upickle.default.Writer](data: T) =
  cask.Response(upickle.default.write(Map("data" -> data)), 200, headers = jsonHeaders)

def err(msg: String, status: Int = 400) =
  cask.Response(upickle.default.write(Map("error" -> msg)), status, headers = jsonHeaders)
```

### uPickle ReadWriters
```scala
// In model/Json.scala — define for every domain model
given ReadWriter[Workout]    = macroRW
given ReadWriter[Exercise]   = macroRW
given ReadWriter[WorkoutSet] = macroRW
given ReadWriter[User]       = macroRW
```

### DB query rules
- Always scope by userId: `.filter(_.userId === userId)`
- Always check ownership before update/delete: fetch first, verify userId matches, then mutate
- Wrap in try/catch, return `err(e.getMessage, 500)` on DB errors

---

## Frontend Conventions

### Composition API only
```vue
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useWorkoutStore } from 'stores/workout'
const store = useWorkoutStore()
onMounted(() => store.fetchWorkouts())
</script>
```

### API layer
```typescript
// api/workouts.ts
export const listWorkouts   = ()      => client.get('/api/workouts')
export const createWorkout  = (t: string) => client.post('/api/workouts', { title: t })
export const finishWorkout  = (id: string) => client.put(`/api/workouts/${id}`, { ended_at: new Date() })
// Components never call axios directly — always go through api/ functions
```

### Axios JWT interceptor
```typescript
// api/client.ts
client.interceptors.request.use(config => {
  const token = useAuthStore().token
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})
```

---

## Business Logic

### Estimated 1RM (Epley formula)
```
estimatedOneRM = weight × (1 + reps / 30)
```
Calculate server-side in stats endpoints. Use for PR comparisons across rep ranges.

### Session volume
```
sessionVolume = SUM(weight_kg × reps) for all is_done=true sets in the workout
```

### Streak
Count consecutive calendar days ending today where at least one workout has `ended_at IS NOT NULL`.
A day with no completed workout resets the streak to 0.

### Active session guard
On `POST /api/workouts`, query for any existing workout where `user_id = userId AND ended_at IS NULL`.
If found, return 409 Conflict with message "You already have an active session."

---

## Development Setup

```bash
# 1. Start Postgres
docker-compose up -d

# 2. Run backend (port 8080)
cd backend && sbt run

# 3. Run frontend (port 9000, proxies /api/* to :8080)
cd frontend && quasar dev

# 4. Optional: expose backend publicly
ngrok http --domain=your-domain.ngrok-free.app 8080
```

## Environment Variables

### Backend
```
DB_URL=jdbc:postgresql://localhost:5432/ironlog
DB_USER=postgres
DB_PASSWORD=password
JWT_SECRET=minimum-32-character-secret-key-here
JWT_EXPIRY_HOURS=24
PORT=8080
```

### Frontend (.env)
```
VITE_API_BASE_URL=http://localhost:8080
```

---

## build.sbt

```scala
scalaVersion := "3.3.1"

libraryDependencies ++= Seq(
  "com.lihaoyi"  %% "cask"       % "0.9.2",
  "com.lihaoyi"  %% "scalasql"   % "0.1.4",
  "com.lihaoyi"  %% "upickle"    % "3.3.1",
  "com.lihaoyi"  %% "requests"   % "0.8.3",
  "org.postgresql" % "postgresql" % "42.7.3",
  "at.favre.lib"   % "bcrypt"    % "0.10.2",
  "com.auth0"      % "java-jwt"  % "4.4.0",
)
```

---

## What NOT To Do
- Do NOT use Cats Effect, ZIO, or Future — Cask is synchronous, keep it simple
- Do NOT store JWT in localStorage — Pinia memory only
- Do NOT put DB queries inside route handlers — extract to a function or repository object
- Do NOT skip userId scoping on queries — every query must filter by authenticated user
- Do NOT use Options API in Vue — script setup + Composition API only
- Do NOT hardcode secrets — always read from environment variables
- Do NOT return raw exception messages to client in production — catch and wrap in err()
- Do NOT allow deleting or editing another user's data — always verify ownership first
