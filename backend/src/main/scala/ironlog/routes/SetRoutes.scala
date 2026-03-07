package ironlog.routes

import ironlog.*
import ironlog.model.*
import ironlog.Middleware.*
import scalasql.PostgresDialect.*
import java.time.LocalDateTime

object SetRoutes extends cask.Routes:

  @cask.post("/api/workouts/:id/sets")
  def create(id: String, request: cask.Request) = handleAuth(request) { userId =>
    try
      val wRows = Database.run(_.run(Workout.select.filter(w => w.id === id && w.userId === userId)))
      if wRows.isEmpty then
        err("Workout not found", 404)
      else
        val body       = ujson.read(request.text())
        val exerciseId = body("exercise_id").str
        val setNumber  = body("set_number").num.toInt
        val weightKg   = body.obj.get("weight_kg").filterNot(_.isNull).map(_.num)
        val reps       = body.obj.get("reps").filterNot(_.isNull).map(_.num.toInt)
        val durationS  = body.obj.get("duration_s").filterNot(_.isNull).map(_.num.toInt)
        val setId      = java.util.UUID.randomUUID().toString
        val now        = LocalDateTime.now()
        Database.run(_.run(WorkoutSet.insert.columns(
          _.id         := setId,
          _.workoutId  := id,
          _.exerciseId := exerciseId,
          _.setNumber  := setNumber,
          _.weightKg   := weightKg,
          _.reps       := reps,
          _.durationS  := durationS,
          _.isDone     := false,
          _.notes      := (None: Option[String]),
          _.loggedAt   := now,
        )))
        ok(ujson.Obj(
          "id"         -> setId,
          "workoutId"  -> id,
          "exerciseId" -> exerciseId,
          "setNumber"  -> setNumber,
          "weightKg"   -> weightKg.fold[ujson.Value](ujson.Null)(ujson.Num(_)),
          "reps"       -> reps.fold[ujson.Value](ujson.Null)(ujson.Num(_)),
          "durationS"  -> durationS.fold[ujson.Value](ujson.Null)(ujson.Num(_)),
          "isDone"     -> false,
          "notes"      -> ujson.Null,
          "loggedAt"   -> now.toString,
        ))
    catch case e: Exception => err(e.getMessage, 500)
  }

  @cask.post("/api/sets/:id/update")
  def update(id: String, request: cask.Request) = handleAuth(request) { userId =>
    try
      val setRows = Database.run(_.run(WorkoutSet.select.filter(_.id === id)))
      setRows.headOption match
        case None => err("Set not found", 404)
        case Some(s) =>
          val wRows = Database.run(_.run(Workout.select.filter(w => w.id === s.workoutId && w.userId === userId)))
          if wRows.isEmpty then
            err("Set not found", 404)
          else
            val body     = ujson.read(request.text())
            val weightKg = body.obj.get("weight_kg").filterNot(_.isNull).map(_.num).orElse(s.weightKg)
            val reps     = body.obj.get("reps").filterNot(_.isNull).map(_.num.toInt).orElse(s.reps)
            val isDone   = body.obj.get("is_done").map(_.bool).getOrElse(s.isDone)
            val notes    = body.obj.get("notes").filterNot(_.isNull).map(v => Option(v.str)).getOrElse(s.notes)
            Database.run(_.run(
              WorkoutSet.update(_.id === id)
                .set(_.weightKg := weightKg)
                .set(_.reps     := reps)
                .set(_.isDone   := isDone)
                .set(_.notes    := notes)
            ))
            ok(ujson.Obj(
              "id"         -> id,
              "workoutId"  -> s.workoutId,
              "exerciseId" -> s.exerciseId,
              "setNumber"  -> s.setNumber,
              "weightKg"   -> weightKg.fold[ujson.Value](ujson.Null)(ujson.Num(_)),
              "reps"       -> reps.fold[ujson.Value](ujson.Null)(ujson.Num(_)),
              "durationS"  -> s.durationS.fold[ujson.Value](ujson.Null)(ujson.Num(_)),
              "isDone"     -> isDone,
              "notes"      -> notes.fold[ujson.Value](ujson.Null)(ujson.Str(_)),
              "loggedAt"   -> s.loggedAt.toString,
            ))
    catch case e: Exception => err(e.getMessage, 500)
  }

  @cask.post("/api/sets/:id/delete")
  def delete(id: String, request: cask.Request) = handleAuth(request) { userId =>
    try
      val setRows = Database.run(_.run(WorkoutSet.select.filter(_.id === id)))
      setRows.headOption match
        case None => err("Set not found", 404)
        case Some(s) =>
          val wRows = Database.run(_.run(Workout.select.filter(w => w.id === s.workoutId && w.userId === userId)))
          if wRows.isEmpty then
            err("Set not found", 404)
          else
            Database.run(_.run(WorkoutSet.delete(_.id === id)))
            ok(ujson.Obj("success" -> true))
    catch case e: Exception => err(e.getMessage, 500)
  }

  initialize()
