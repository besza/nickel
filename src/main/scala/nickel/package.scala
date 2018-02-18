import akka.http.scaladsl.server.{RejectionError, ValidationRejection}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, FromStringUnmarshaller, Unmarshaller}
import play.api.libs.json._
import slick.jdbc.HsqldbProfile.api._

import java.sql.{Date, Timestamp}
import java.time.{Instant, LocalDate, YearMonth}

package object nickel {
  implicit def idColumnType[T] = MappedColumnType.base[Id[T], Long](_.value, Id(_))
  implicit val moneyColumnType = MappedColumnType.base[Money, Int](_.cents, Money(_))
  implicit val instantColumnType = MappedColumnType.base[Instant, Timestamp](Timestamp.from, _.toInstant)
  implicit val localDateColumnType = MappedColumnType.base[LocalDate, Date](Date.valueOf, _.toLocalDate)

  object DbFun {
    val year = SimpleFunction.unary[LocalDate, Int]("YEAR")
    val month = SimpleFunction.unary[LocalDate, Int]("MONTH")
  }

  implicit def readsFromEntityUnmarshaller[T : Reads : Validator]: FromEntityUnmarshaller[T] =
    Unmarshaller.stringUnmarshaller andThen
    Unmarshaller.strict { str =>
      Json.parse(str).validate[T] match {
        case JsSuccess(value, _) =>
          Validator.validate(value) match {
            case Left(error) => throw RejectionError(ValidationRejection(error, None))
            case Right(_) => value
          }
        case e @ JsError(_) => throw RejectionError(ValidationRejection(e.toString, None))
      }
    }

  implicit def idFromStringUnmarshaller[T]: FromStringUnmarshaller[Id[T]] =
    Unmarshaller.strict { str => Id(str.toLong) }

  implicit val yearMonthFromStringUnmarshaller: FromStringUnmarshaller[YearMonth] =
    Unmarshaller.strict(YearMonth.parse)

  implicit val yearMonthWrites: Writes[YearMonth] = Writes { month => JsString(month.toString) }
}
