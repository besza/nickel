package nickel

import nickel.common.{ApiResponse, Created, Id, NoContent, NotFound, Ok}

import java.time.YearMonth
import scala.concurrent.{ExecutionContext, Future}

class TransactionController(
  transactionRepository: TransactionRepository,
  database: common.DatabaseProfile.api.Database
)(implicit ec: ExecutionContext) {

  def getAll(month: Option[YearMonth], accountId: Option[Id[Account]]): Future[ApiResponse[Seq[Transaction.Tracked]]] =
    database.run { transactionRepository.filtered(month, accountId) }
      .map(Ok(_))

  def create(transaction: Transaction): Future[ApiResponse[Transaction.Tracked]] =
    database.run { transactionRepository.create(transaction) }
      .map(Created(_))

  def update(id: Id[Transaction], transaction: Transaction): Future[ApiResponse[Transaction.Tracked]] =
    database.run { transactionRepository.update(id, transaction) }
      .map {
        case Some(updated) => Ok(updated)
        case None => NotFound
      }

  def delete(id: Id[Transaction]): Future[ApiResponse[Nothing]] =
    database.run { transactionRepository.delete(id) }
      .map(if (_) NoContent else NotFound)

  def getMonths: Future[ApiResponse[Seq[YearMonth]]] =
    database.run { transactionRepository.months }
      .map(Ok(_))

  def getBalance(refAccountId: Id[Account]): Future[ApiResponse[Seq[MonthlyBalance]]] =
    database.run(transactionRepository.monthlySums).map { monthlySums =>
      val monthlyAccountBalances = monthlySums
        .toList
        .collect {
          case ((month, from, to), amount) if from == refAccountId =>
              (month, to, Money(-amount.cents))
          case ((month, from, to), amount) if to == refAccountId =>
              (month, from, amount)
        }
      val occuringAccountIds = monthlyAccountBalances.map(_._2).distinct.sortBy(_.value)
      val monthlyBalances = monthlyAccountBalances
        .groupBy(_._1)
        .map {
          case (month, allBalances) =>
            val accountBalances = allBalances
              .groupBy(_._2)
              .map { case (accountId, monthsAccountIdsBalances) => (accountId, Money(monthsAccountIdsBalances.map(_._3.cents).sum)) }
            val inBalance = Money(accountBalances.values.map(_.cents).filter(_ > 0).sum)
            val outBalance = Money(-accountBalances.values.map(_.cents).filter(_ < 0).sum)
            MonthlyBalance(
              month = month,
              in = inBalance,
              out = outBalance,
              accountBalances = occuringAccountIds.map(a => (a, accountBalances.getOrElse(a, Money(0)))),
              balance = Money(inBalance.cents - outBalance.cents)
            )
          }
        .toSeq
      Ok(monthlyBalances)
    }
}
