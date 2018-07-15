package nickel

import slick.jdbc.HsqldbProfile.api._

import scala.concurrent.{ExecutionContext, Future}

private class AccountTable(tag: Tag) extends Table[Account.Stored](tag, "account") {
  def id = column[Id[Account]]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")

  def * = (id, name) <> ((fromDb _).tupled, toDb)
  def fromDb(id: Id[Account], name: String) = Account(name).stored(id)
  def toDb(a: Account.Stored) = Some((a.id, a.name))
}

class AccountRepository(database: Database)(implicit ec: ExecutionContext) {
  private val table = TableQuery[AccountTable]
  private val insert =
    table returning table.map(_.id) into
    ((inserted, id) => inserted.stored(id))

  def all: Future[Seq[Account.Stored]] =
    database.run { table.result }

  def create(account: Account): Future[Account.Stored] =
    database.run { insert += account.stored(Id(0)) }
}
