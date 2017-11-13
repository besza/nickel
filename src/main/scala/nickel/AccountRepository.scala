package nickel

import scala.concurrent.{ExecutionContext, Future}

import io.vertx.core.json.JsonArray
import io.vertx.scala.ext.sql.{SQLClient, SQLOptions}

class AccountRepository(val sqlClient: SQLClient)(implicit val ec: ExecutionContext) {

  def all: Future[List[StoredAccount]] =
    sqlClient.queryFuture(
      "SELECT id, name FROM account"
    ).map { resultSet =>
      resultSet.getResults
        .map { row =>
          StoredAccount(
            id = Id(row.getLong(0)),
            account = Account(name = row.getString(1))
          )
        }
        .toList
    }

  def create(account: Account): Future[StoredAccount] =
    for {
      conn <- sqlClient.getConnectionFuture
      _ = conn.setOptions(SQLOptions().setAutoGeneratedKeys(true))
      result <- conn.updateWithParamsFuture(
        "INSERT INTO account (name) VALUES (?)",
        new JsonArray().add(account.name)
      )
    } yield StoredAccount(
      id = Id(result.getKeys.getLong(0)),
      account = account
    )
}
