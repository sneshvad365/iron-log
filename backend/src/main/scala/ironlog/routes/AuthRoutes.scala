package ironlog.routes

import ironlog.*
import ironlog.model.*
import ironlog.Middleware.*
import scalasql.PostgresDialect.*

object AuthRoutes extends cask.Routes:

  @cask.post("/api/auth/register")
  def register(request: cask.Request) =
    try
      val body     = ujson.read(request.text())
      val name     = body("name").str
      val email    = body("email").str
      val password = body("password").str
      val existing = Database.run(_.run(User.select.filter(_.email === email)))
      if existing.nonEmpty then
        err("Email already registered", 409)
      else
        val hashed = Auth.hashPassword(password)
        val id     = java.util.UUID.randomUUID().toString
        val now    = java.time.LocalDateTime.now()
        Database.run(_.run(User.insert.columns(
          _.id        := id,
          _.email     := email,
          _.password  := hashed,
          _.name      := name,
          _.createdAt := now,
        )))
        val token = Auth.issueToken(id, email)
        ok(ujson.Obj("token" -> token, "user" -> ujson.Obj("id" -> id, "email" -> email, "name" -> name)))
    catch case e: Exception => err(e.getMessage, 500)

  @cask.post("/api/auth/login")
  def login(request: cask.Request) =
    try
      val body     = ujson.read(request.text())
      val email    = body("email").str
      val password = body("password").str
      val users    = Database.run(_.run(User.select.filter(_.email === email)))
      users.headOption match
        case None => err("Invalid email or password", 401)
        case Some(u) =>
          if !Auth.checkPassword(password, u.password) then
            err("Invalid email or password", 401)
          else
            val token = Auth.issueToken(u.id, u.email)
            ok(ujson.Obj("token" -> token, "user" -> ujson.Obj("id" -> u.id, "email" -> u.email, "name" -> u.name)))
    catch case e: Exception => err(e.getMessage, 500)

  initialize()
