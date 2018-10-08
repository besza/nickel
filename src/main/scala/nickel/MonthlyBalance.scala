package nickel

import java.time.YearMonth

import nickel.common.Id
import play.api.libs.json.{Json, Writes}

case class MonthlyBalance(
  month: YearMonth,
  in: Money,
  out: Money,
  accountBalances: Seq[(Id[Account], Money)],
  balance: Money
)

object MonthlyBalance {
  implicit val writes: Writes[MonthlyBalance] = Json.writes[MonthlyBalance]
}
