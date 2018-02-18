package nickel

final case class Validator[T](f: T => Either[String, T]) extends AnyVal

object Validator {
  def validate[T : Validator](t: T): Either[String, T] = implicitly[Validator[T]].f(t)
}


