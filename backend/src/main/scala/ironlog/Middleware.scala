package ironlog

object Middleware:
  val jsonHeaders: Seq[(String, String)] = Seq("Content-Type" -> "application/json")

  def ok(data: ujson.Value): cask.Response[String] =
    cask.Response(ujson.write(ujson.Obj("data" -> data)), 200, headers = jsonHeaders)

  def err(msg: String, status: Int = 400): cask.Response[String] =
    cask.Response(ujson.write(ujson.Obj("error" -> ujson.Str(msg))), status, headers = jsonHeaders)

  def handleAuth(request: cask.Request)(f: String => cask.Response[String]): cask.Response[String] =
    try f(Auth.requireUser(request))
    catch
      case _: Unauthorized => err("Unauthorized", 401)
      case e: Exception    => err(e.getMessage, 500)
