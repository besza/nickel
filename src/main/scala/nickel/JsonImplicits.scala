package nickel

import scala.util.{Failure, Success, Try}

import java.time.YearMonth

import play.api.libs.json._

object JsonImplicits {

  implicit val yearMonthFormat: Format[YearMonth] = Format(
    Reads {
      case JsString(str) => Try(YearMonth.parse(str)) match {
        case Success(month) => JsSuccess(month)
        case Failure(_) => JsError("Invalid month")
      }
      case _ => JsError("Invalid month")
    },
    Writes { month => JsString(month.toString) }
  )

}
