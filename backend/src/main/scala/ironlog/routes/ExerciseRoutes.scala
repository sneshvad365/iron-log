package ironlog.routes

import ironlog.*
import ironlog.model.*
import ironlog.Middleware.*
import scalasql.PostgresDialect.*

object ExerciseRoutes extends cask.Routes:

  private def exerciseJson(e: Exercise[scalasql.Sc]): ujson.Obj = ujson.Obj(
    "id"          -> e.id,
    "name"        -> e.name,
    "muscleGroup" -> e.muscleGroup,
    "type"        -> e.`type`,
    "createdBy"   -> e.createdBy.fold[ujson.Value](ujson.Null)(ujson.Str(_)),
    "createdAt"   -> e.createdAt.toString,
  )

  @cask.get("/api/exercises")
  def list(request: cask.Request) = handleAuth(request) { userId =>
    try
      val params      = request.queryParams
      val muscleGroup = params.get("muscle_group").flatMap(_.headOption).filter(_.nonEmpty)
      val typeFilter  = params.get("type").flatMap(_.headOption).filter(_.nonEmpty)
      val search      = params.get("search").flatMap(_.headOption).filter(_.nonEmpty)
      val rows = Database.run(_.run(
        Exercise.select.filter(e => e.createdBy.isEmpty || e.createdBy === Option(userId))
      ))
      val filtered = rows
        .filter(e => muscleGroup.forall(_ == e.muscleGroup))
        .filter(e => typeFilter.forall(_ == e.`type`))
        .filter(e => search.forall(s => e.name.toLowerCase.contains(s.toLowerCase)))
      ok(ujson.Arr(filtered.map(exerciseJson)*))
    catch case e: Exception => err(e.getMessage, 500)
  }

  @cask.post("/api/exercises")
  def create(request: cask.Request) = handleAuth(request) { userId =>
    try
      val body         = ujson.read(request.text())
      val name         = body("name").str
      val muscleGroup  = body("muscle_group").str
      val exerciseType = body("type").str
      val id           = java.util.UUID.randomUUID().toString
      val now          = java.time.LocalDateTime.now()
      Database.run(_.run(Exercise.insert.columns(
        _.id          := id,
        _.name        := name,
        _.muscleGroup := muscleGroup,
        _.`type`      := exerciseType,
        _.createdBy   := Option(userId),
        _.createdAt   := now,
      )))
      ok(ujson.Obj(
        "id"          -> id,
        "name"        -> name,
        "muscleGroup" -> muscleGroup,
        "type"        -> exerciseType,
        "createdBy"   -> userId,
        "createdAt"   -> now.toString,
      ))
    catch case e: Exception => err(e.getMessage, 500)
  }

  @cask.post("/api/exercises/:id/delete")
  def delete(id: String, request: cask.Request) = handleAuth(request) { userId =>
    try
      val rows = Database.run(_.run(Exercise.select.filter(_.id === id)))
      rows.headOption match
        case None => err("Exercise not found", 404)
        case Some(e) =>
          if e.createdBy != Option(userId) then
            err("Cannot delete a global exercise", 403)
          else
            Database.run(_.run(Exercise.delete(_.id === id)))
            ok(ujson.Obj("success" -> true))
    catch case e: Exception => err(e.getMessage, 500)
  }

  @cask.get("/api/exercises/:id/history")
  def history(id: String, request: cask.Request) = handleAuth(request) { userId =>
    try
      val userWorkouts = Database.run(_.run(
        Workout.select.filter(w => w.userId === userId && w.endedAt.isDefined)
      ))
      val workoutIds  = userWorkouts.map(_.id).toSet
      val workoutsMap = userWorkouts.map(w => w.id -> w).toMap

      val sets = Database.run(_.run(
        WorkoutSet.select.filter(s => s.exerciseId === id && s.isDone === true)
      )).filter(s => workoutIds.contains(s.workoutId))

      val byWorkout = sets.groupBy(_.workoutId)

      val oneRmProgression = byWorkout.toSeq.flatMap { (wId, wSets) =>
        workoutsMap.get(wId).toSeq.flatMap { w =>
          val pts = wSets.flatMap { s =>
            for weight <- s.weightKg; reps <- s.reps if reps > 0
            yield (weight, reps, weight * (1 + reps / 30.0))
          }
          pts.maxByOption(_._3).map { best =>
            ujson.Obj(
              "date"           -> w.startedAt.toLocalDate.toString,
              "estimatedOneRm" -> best._3,
              "bestWeightKg"   -> best._1,
              "bestReps"       -> best._2,
            )
          }
        }
      }.sortBy(_("date").str)

      val volumePerSession = byWorkout.toSeq.flatMap { (wId, wSets) =>
        workoutsMap.get(wId).map { w =>
          val vol = wSets.flatMap(s => for wt <- s.weightKg; r <- s.reps if s.isDone yield wt * r).sum
          ujson.Obj("date" -> w.startedAt.toLocalDate.toString, "volumeKg" -> vol)
        }
      }.sortBy(_("date").str)

      val setsJson = sets.map(s => ujson.Obj(
        "id"        -> s.id,
        "workoutId" -> s.workoutId,
        "setNumber" -> s.setNumber,
        "weightKg"  -> s.weightKg.fold[ujson.Value](ujson.Null)(ujson.Num(_)),
        "reps"      -> s.reps.fold[ujson.Value](ujson.Null)(ujson.Num(_)),
        "isDone"    -> s.isDone,
        "loggedAt"  -> s.loggedAt.toString,
      ))

      ok(ujson.Obj(
        "sets"             -> ujson.Arr(setsJson*),
        "oneRmProgression" -> ujson.Arr(oneRmProgression*),
        "volumePerSession" -> ujson.Arr(volumePerSession*),
      ))
    catch case e: Exception => err(e.getMessage, 500)
  }

  initialize()
