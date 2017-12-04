package nickel

import nickel.JsonImplicits._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}
import java.time.YearMonth

import io.vertx.scala.ext.web.Router

class TransactionController(
  router: Router,
  transactionRepository: TransactionRepository
)(implicit ec: ExecutionContext) {

  Endpoint.get(
    router,
    path = "/api/transactions",
    produceInput = { req =>
      for {
        month <- req.getParam("month")
          .toRight("Missing parameter: month")
          .flatMap { str => Try(YearMonth.parse(str)).toOption.toRight("Invalid month") }
        account <- req.getParam("account")
          .map { str => Try(str.toLong) } match {
          case Some(Success(id)) => Right(Some(Id[Account](id)))
          case Some(Failure(_)) => Left("Invalid account id")
          case None => Right(None)
        }
      } yield (month, account)
    },
    produceOutput = (transactionRepository.inMonth _).tupled
  )

  Endpoint.post(
    router,
    path = "/api/transactions",
    produceInput = (_, transaction: Transaction) => transaction.validate,
    produceOutput = transactionRepository.create
  )

  Endpoint.get(
    router,
    path = "/api/transactions/months",
    produceInput = Function.const(Right(Unit)),
    produceOutput = (_: Unit.type) => transactionRepository.months
  )

}
