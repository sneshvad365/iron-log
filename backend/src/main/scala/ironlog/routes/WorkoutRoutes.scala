package ironlog.routes

import ironlog.*
import ironlog.model.*
import ironlog.Middleware.*
import scalasql.PostgresDialect.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object WorkoutRoutes extends cask.Routes:

  private def workoutJson(w: Workout[scalasql.Sc]): ujson.Obj = ujson.Obj(
    "id"        -> w.id,
    "userId"    -> w.userId,
    "title"     -> w.title,
    "notes"     -> w.notes.fold[ujson.Value](ujson.Null)(ujson.Str(_)),
    "startedAt" -> w.startedAt.toString,
    "endedAt"   -> w.endedAt.fold[ujson.Value](ujson.Null)(d => ujson.Str(d.toString)),
  )

  @cask.get("/api/workouts")
  def list(request: cask.Request) = handleAuth(request) { userId =>
    try
      val rows   = Database.run(_.run(Workout.select.filter(_.userId === userId)))
      val sorted = rows.sortBy(_.startedAt.toString).reverse
      ok(ujson.Arr(sorted.map(workoutJson)*))
    catch case e: Exception => err(e.getMessage, 500)
  }

  @cask.post("/api/workouts")
  def create(request: cask.Request) = handleAuth(request) { userId =>
    try
      val body   = ujson.read(request.text())
      val title  = body("title").str
      val active = Database.run(_.run(Workout.select.filter(w => w.userId === userId && w.endedAt.isEmpty)))
      if active.nonEmpty then
        err("You already have an active session.", 409)
      else
        val id  = java.util.UUID.randomUUID().toString
        val now = LocalDateTime.now()
        Database.run(_.run(Workout.insert.columns(
          _.id        := id,
          _.userId    := userId,
          _.title     := title,
          _.notes     := (None: Option[String]),
          _.startedAt := now,
          _.endedAt   := (None: Option[LocalDateTime]),
        )))
        ok(ujson.Obj(
          "id"        -> id,
          "userId"    -> userId,
          "title"     -> title,
          "notes"     -> ujson.Null,
          "startedAt" -> now.toString,
          "endedAt"   -> ujson.Null,
        ))
    catch case e: Exception => err(e.getMessage, 500)
  }

  @cask.get("/api/workouts/:id")
  def get(id: String, request: cask.Request) = handleAuth(request) { userId =>
    try
      if id == "active" then
        val rows = Database.run(_.run(Workout.select.filter(w => w.userId === userId && w.endedAt.isEmpty)))
        rows.headOption match
          case None    => ok(ujson.Null)
          case Some(w) => ok(workoutJson(w))
      else
        val rows = Database.run(_.run(Workout.select.filter(w => w.id === id && w.userId === userId)))
        rows.headOption match
          case None    => err("Workout not found", 404)
          case Some(w) => ok(workoutJson(w))
    catch case e: Exception => err(e.getMessage, 500)
  }

  @cask.post("/api/workouts/:id/update")
  def update(id: String, request: cask.Request) = handleAuth(request) { userId =>
    try
      val rows = Database.run(_.run(Workout.select.filter(w => w.id === id && w.userId === userId)))
      rows.headOption match
        case None => err("Workout not found", 404)
        case Some(w) =>
          val body    = ujson.read(request.text())
          val title   = body.obj.get("title").map(_.str).getOrElse(w.title)
          val notes   = body.obj.get("notes").map(v => Option(v.str)).getOrElse(w.notes)
          val endedAt = body.obj.get("ended_at").flatMap { v =>
            if v.isNull then None
            else Option(LocalDateTime.parse(v.str, DateTimeFormatter.ISO_DATE_TIME))
          }.orElse(w.endedAt)
          Database.run(_.run(
            Workout.update(_.id === id)
              .set(_.title   := title)
              .set(_.notes   := notes)
              .set(_.endedAt := endedAt)
          ))
          ok(ujson.Obj(
            "id"        -> id,
            "userId"    -> userId,
            "title"     -> title,
            "notes"     -> notes.fold[ujson.Value](ujson.Null)(ujson.Str(_)),
            "startedAt" -> w.startedAt.toString,
            "endedAt"   -> endedAt.fold[ujson.Value](ujson.Null)(d => ujson.Str(d.toString)),
          ))
    catch case e: Exception => err(e.getMessage, 500)
  }

  @cask.post("/api/workouts/:id/delete")
  def delete(id: String, request: cask.Request) = handleAuth(request) { userId =>
    try
      val rows = Database.run(_.run(Workout.select.filter(w => w.id === id && w.userId === userId)))
      if rows.isEmpty then
        err("Workout not found", 404)
      else
        Database.run(_.run(Workout.delete(_.id === id)))
        ok(ujson.Obj("success" -> true))
    catch case e: Exception => err(e.getMessage, 500)
  }

  initialize()
