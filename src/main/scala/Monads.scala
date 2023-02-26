
import cats.Monad
import cats.implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import Domain._


object Domain {
  class UserId
  class User
  class Address
}

/* The task */
object NoMonads {
  // Service API
  def findLastSubscriber(): UserId = ???
  def findUser(id: UserId): User = ???
  def findAddress(user: User): Address = ???

  def findAddressOfLastSubscriber(): Address = {
    findAddress(findUser(findLastSubscriber()))
  }
}

/* The reality */
object OptionMonad {
  // Service API
  def findLastSubscriber(): Option[UserId] = ???
  def findUser(id: UserId): Option[User] = ???
  def findAddress(user: User): Option[Address] = ???

  /* Not a solution */
  def findAddressOfLastSubscriber(): Option[Address] = {
    val id = findLastSubscriber()
    if (id.nonEmpty) {
      val user = findUser(id.get)
      if (user.nonEmpty) {
        findAddress(user.get)
      } else {
        None
      }
    } else {
      None
    }
  }

  /* Monadic solution */
  def findAddressOfLastSubscriberMonadic(): Option[Address] = {
    findLastSubscriber()
      .flatMap(id => findUser(id))
      .flatMap(user => findAddress(user))
  }

  /* Syntax sugar */
  def findAddressOfLastSubscriberFor(): Option[Address] = {
    for {
      id <- findLastSubscriber()
      user <- findUser(id)
      address <- findAddress(user)
    } yield address
  }
}

/* Another monad (*) */
object FutureMonad {
  // Service API
  def findLastSubscriber(): Future[UserId] = ???
  def findUser(id: UserId): Future[User] = ???
  def findAddress(user: User): Future[Address] = ???

  /* Monadic code is the same */
  def findAddressOfLastSubscriberFor(): Future[Address] = {
    for {
      id <- findLastSubscriber()
      user <- findUser(id)
      address <- findAddress(user)
    } yield address
  }
}


/**
 * We can abstract over Monad
 *
 * F can be Option/Either/Future/Future of Either etc
 */

class AnyMonad[F[_]: Monad] {
  // Service API
  def findLastSubscriber(): F[UserId] = ???
  def findUser(id: UserId): F[User] = ???
  def findAddress(user: User): F[Address] = ???

  def findAddressOfLastSubscriberFor(): F[Address] = {
    for {
      id <- findLastSubscriber()
      user <- findUser(id)
      address <- findAddress(user)
    } yield address
  }
}


trait MonadExample[A] {
  def flatMap[B](f: A => MonadExample[B]): MonadExample[B] = ???

  def map[B](f: A => B): MonadExample[B] = {
    flatMap(a => MonadExample.pure(f(a)))
  }
}

object MonadExample {
  def pure[A](a: A): MonadExample[A] = ???
}


object MonadLaws {
  type A
  type B
  type C

  val x: A = ???
  val monad: MonadExample[A] = ???

  val f: A => MonadExample[B] = ???
  val g: B => MonadExample[C] = ???

  MonadExample.pure(x).flatMap(f)          == f(x)  // Left identity
  monad.flatMap(a => MonadExample.pure(a)) == monad // Right identity

  monad.flatMap(f).flatMap(g) == monad.flatMap(x => f(x).flatMap(g)) // Associativity
}
