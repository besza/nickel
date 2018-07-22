package nickel

import scala.concurrent.{ExecutionContext, Future}

class AccountController(
  accountRepository: AccountRepository,
  database: common.DatabaseProfile.api.Database
)(implicit ec: ExecutionContext) {

  def getAll: Future[ApiResponse] =
    database.run { accountRepository.all }
      .map(Ok(_))

  def create(account: Account): Future[ApiResponse] =
    database.run { accountRepository.create(account) }
      .map(Created(_))
}
