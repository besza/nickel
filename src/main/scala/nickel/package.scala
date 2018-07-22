import akka.http.scaladsl.server.{RejectionError, ValidationRejection}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, FromStringUnmarshaller, Unmarshaller}
import play.api.libs.json._

import java.time.YearMonth

package object nickel {
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
