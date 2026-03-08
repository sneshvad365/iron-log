package ironlog.model

import scalasql.Table
import scalasql.Column
import java.time.LocalDateTime

case class Report[T[_]](
  id:        T[String],
  userId:    T[String],
  title:     T[String],
  content:   T[String],
  createdAt: T[LocalDateTime],
)

object Report extends Table[Report]:
  override def tableName = "reports"
