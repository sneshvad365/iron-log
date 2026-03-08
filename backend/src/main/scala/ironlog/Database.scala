package ironlog

import scalasql.PostgresDialect.*

object Database:
  val db: scalasql.core.DbClient.DataSource =
    val ds = org.postgresql.ds.PGSimpleDataSource()
    ds.setUrl(Config.dbUrl)
    ds.setUser(Config.dbUser)
    ds.setPassword(Config.dbPassword)
    scalasql.DbClient.DataSource(ds)

  /** Run a single query using an auto-commit connection, then close it. */
  def run[T](f: scalasql.core.DbApi => T): T =
    val conn = db.getAutoCommitClientConnection
    try f(conn)
    finally conn.close()

  /** Run multiple queries in a single transaction. */
  def transaction[T](f: scalasql.core.DbApi.Txn => T): T =
    db.transaction(f)
