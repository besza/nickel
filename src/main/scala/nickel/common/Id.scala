package nickel.common

import play.api.libs.json._

case class Id[T](value: Long) extends AnyRef

object Id {
  implicit def reads[T](implicit longReads: Reads[Long]): Reads[Id[T]] =
    longReads.map(Id(_))

  implicit def writes[T]: Writes[Id[T]] =
    id => JsNumber(id.value)
}
