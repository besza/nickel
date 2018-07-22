package nickel

import play.api.libs.json.{Json, OWrites}

trait Stored[T] {
  val id: Id[T]
}

object Stored {
  def writes[T](w: OWrites[T]): OWrites[T with Stored[T]] = OWrites { x =>
    Json.obj(
      "id" -> x.id
    ) ++ w.writes(x)
  }
}
