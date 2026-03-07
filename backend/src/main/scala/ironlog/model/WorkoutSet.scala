package ironlog.model

import scalasql.Table

case class WorkoutSet[T[_]](
  id:         T[String],
  workoutId:  T[String],
  exerciseId: T[String],
  setNumber:  T[Int],
  weightKg:   T[Option[Double]],
  reps:       T[Option[Int]],
  durationS:  T[Option[Int]],
  isDone:     T[Boolean],
  notes:      T[Option[String]],
  loggedAt:   T[java.time.LocalDateTime],
)

object WorkoutSet extends Table[WorkoutSet]:
  override def tableName = "workout_sets"
