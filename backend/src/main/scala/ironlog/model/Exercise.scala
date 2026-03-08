package ironlog.model

import scalasql.Table

case class Exercise[T[_]](
  id:          T[String],
  name:        T[String],
  muscleGroup: T[String],
  `type`:      T[String],
  createdBy:   T[Option[String]],
  createdAt:   T[java.time.LocalDateTime],
  gifUrl:      T[Option[String]],
  gifUrl2:     T[Option[String]],
)

object Exercise extends Table[Exercise]:
  override def tableName = "exercises"
