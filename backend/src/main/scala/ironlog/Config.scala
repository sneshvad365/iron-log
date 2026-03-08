package ironlog

object Config:
  val dbUrl: String         = sys.env.getOrElse("DB_URL",      "jdbc:postgresql://localhost:5432/ironlog")
  val dbUser: String        = sys.env.getOrElse("DB_USER",     "postgres")
  val dbPassword: String    = sys.env.getOrElse("DB_PASSWORD", "password")
  val jwtSecret: String     = sys.env.getOrElse("JWT_SECRET",  "change-me-minimum-32-character-secret!")
  val jwtExpiryHours: Long  = sys.env.get("JWT_EXPIRY_HOURS").flatMap(_.toLongOption).getOrElse(24L)
  val port: Int             = sys.env.get("PORT").flatMap(_.toIntOption).getOrElse(8080)
  val anthropicKey: String  = sys.env.getOrElse("ANTHROPIC_API_KEY", "")
  val stripeSecretKey: String = sys.env.getOrElse("STRIPE_SECRET_KEY", "")
  val frontendUrl: String   = sys.env.getOrElse("FRONTEND_URL", "http://localhost:9000")
