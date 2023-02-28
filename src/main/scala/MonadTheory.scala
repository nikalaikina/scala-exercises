
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
