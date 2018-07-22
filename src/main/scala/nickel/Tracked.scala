package nickel

import play.api.libs.json.{Json, OWrites}

import java.time.Instant

trait Tracked[T] {
  val id: Id[T]
  val createdAt: Instant
}

object Tracked {
  def writes[T](w: OWrites[T]): OWrites[T with Tracked[T]] = OWrites { x =>
    Json.obj(
      "id" -> x.id,
      "createdAt" -> x.createdAt
    ) ++ w.writes(x)
  }
}

