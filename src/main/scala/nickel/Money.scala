package nickel

import scala.util.matching.Regex

import play.api.libs.json._

case class Money(cents: Int) extends AnyVal {
  override def toString: String = {
    val units = cents / 100
    val rest = cents % 100
    "%d.%02d".format(units, rest)
  }
}

object Money {
  val regex: Regex = raw"(0|(?:[1-9]\d*))\.(\d{2})".r

  def parse(str: String): Option[Money] = {
    str match {
      case regex(units, rest) => Some(Money(cents = units.toInt * 100 + rest.toInt))
      case _ => None
    }
  }

  implicit val moneyWrites: Writes[Money] =
    money => JsString(money.toString)

  implicit def moneyReads: Reads[Money] = {
    case JsString(string) => parse(string) match {
      case Some(money) => JsSuccess(money)
      case None => JsError()
    }
    case _ => JsError()
  }

}
