package ironlog.model

import scalasql.Table

case class Workout[T[_]](
  id:        T[String],
  userId:    T[String],
  title:     T[String],
  notes:     T[Option[String]],
  startedAt: T[java.time.LocalDateTime],
  endedAt:   T[Option[java.time.LocalDateTime]],
)

object Workout extends Table[Workout]:
  override def tableName = "workouts"
