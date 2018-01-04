import scala.util.{Failure, Success, Try}

package object nickel {

  implicit class TryOptOps[A](val opt: Option[A]) extends AnyVal {
     def tryOpt[B](f: A => B, error: String): Either[String, Option[B]] =
       opt.map(x => Try(f(x))) match {
         case Some(Success(x)) => Right(Some(x))
         case Some(Failure(_)) => Left(error)
         case None => Right(None)
       }
  }

}
