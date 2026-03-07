package ironlog

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

object Auth:
  private val algorithm = Algorithm.HMAC256(Config.jwtSecret)
  private val verifier  = JWT.require(algorithm).build()

  def hashPassword(plain: String): String =
    BCrypt.withDefaults().hashToString(12, plain.toCharArray)

  def checkPassword(plain: String, hashed: String): Boolean =
    BCrypt.verifyer().verify(plain.toCharArray, hashed).verified

  def issueToken(userId: String, email: String): String =
    val exp = Instant.now().plus(Config.jwtExpiryHours, ChronoUnit.HOURS)
    JWT.create()
      .withClaim("userId", userId)
      .withClaim("email", email)
      .withExpiresAt(Date.from(exp))
      .sign(algorithm)

  def requireUser(request: cask.Request): String =
    val header = request.headers.get("authorization")
      .flatMap(_.headOption)
      .getOrElse(throw Unauthorized("Missing Authorization header"))
    if !header.startsWith("Bearer ") then throw Unauthorized("Invalid Authorization header")
    val token = header.drop(7)
    try
      val decoded = verifier.verify(token)
      decoded.getClaim("userId").asString()
    catch case _: Exception => throw Unauthorized("Invalid or expired token")

case class Unauthorized(msg: String) extends Exception(msg)
