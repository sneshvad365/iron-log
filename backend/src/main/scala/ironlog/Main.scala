package ironlog

import ironlog.routes.*

object Main extends cask.Main:
  val allRoutes: Seq[cask.Routes] = Seq(
    AuthRoutes,
    WorkoutRoutes,
    SetRoutes,
    ExerciseRoutes,
    StatsRoutes,
    ReportRoutes,
    DonateRoutes,
  )

  override def port: Int = Config.port

  override def host: String = "0.0.0.0"

  override def main(args: Array[String]): Unit =
    super.main(args)
    Thread.currentThread().join() // keep the JVM alive when run via `sbt run`
