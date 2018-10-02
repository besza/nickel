package nickel.common

import play.api.libs.json._

case class Id[T](value: Long) extends AnyRef

object Id {
  implicit def reads[T]: Reads[Id[T]] =
    implicitly[Reads[String]].map(str => Id(str.toLong))

  implicit def writes[T]: Writes[Id[T]] =
    id => JsString(id.value.toString)
}
