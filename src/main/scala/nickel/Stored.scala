package nickel

import play.api.libs.json.{OWrites, Writes}

trait Stored[T] {
  val id: Id[T]
}

object Stored {
  def writes[T](w: OWrites[T]): OWrites[T with Stored[T]] = OWrites { x =>
    w.writes(x) + ("id", implicitly[Writes[Id[T]]].writes(x.id))
  }
}
