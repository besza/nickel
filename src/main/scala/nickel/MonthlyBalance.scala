package nickel

import play.api.libs.json.{Json, Writes}

import java.time.YearMonth

case class MonthlyBalance(
  month: YearMonth,
  in: Money,
  out: Money,
  balance: Money
)

object MonthlyBalance {
  implicit val writes: Writes[MonthlyBalance] = Json.writes[MonthlyBalance]
}
