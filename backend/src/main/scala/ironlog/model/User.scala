package ironlog.model

import scalasql.Table

case class User[T[_]](
  id:        T[String],
  email:     T[String],
  password:  T[String],
  name:      T[String],
  createdAt: T[java.time.LocalDateTime],
)

object User extends Table[User]:
  override def tableName = "users"
