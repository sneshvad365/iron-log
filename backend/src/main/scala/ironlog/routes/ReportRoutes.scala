package ironlog.routes

import ironlog.*
import ironlog.model.*
import ironlog.Middleware.*
import scalasql.PostgresDialect.*

object ReportRoutes extends cask.Routes:

  @cask.get("/api/reports")
  def list(request: cask.Request) = handleAuth(request) { userId =>
    try
      val rows = Database.run(_.run(
        Report.select.filter(_.userId === userId)
      )).sortBy(_.createdAt.toString).reverse
      ok(ujson.Arr(rows.map(r => ujson.Obj(
        "id"        -> r.id,
        "title"     -> r.title,
        "createdAt" -> r.createdAt.toString,
      ))*))
    catch case e: Exception => err(e.getMessage, 500)
  }

  @cask.get("/api/reports/:id")
  def get(id: String, request: cask.Request) = handleAuth(request) { userId =>
    try
      val rows = Database.run(_.run(
        Report.select.filter(r => r.id === id && r.userId === userId)
      ))
      rows.headOption match
        case None    => err("Report not found", 404)
        case Some(r) => ok(ujson.Obj(
          "id"        -> r.id,
          "title"     -> r.title,
          "content"   -> r.content,
          "createdAt" -> r.createdAt.toString,
        ))
    catch case e: Exception => err(e.getMessage, 500)
  }

  @cask.post("/api/reports/:id/delete")
  def delete(id: String, request: cask.Request) = handleAuth(request) { userId =>
    try
      val rows = Database.run(_.run(Report.select.filter(r => r.id === id && r.userId === userId)))
      if rows.isEmpty then err("Report not found", 404)
      else
        Database.run(_.run(Report.delete(_.id === id)))
        ok(ujson.Obj("success" -> true))
    catch case e: Exception => err(e.getMessage, 500)
  }

  initialize()
