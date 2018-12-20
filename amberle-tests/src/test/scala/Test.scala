import amberle.Maybe

object Test extends App {

  locally {
    val value = 1
    val maybe = Maybe.from(1)
    val f: java.util.function.Function[Int, Maybe[Int]] = x => Maybe.from(x * 2)
    println(maybe.flatMap(f) == f(value))
  }

  ///
  locally {
    val maybe = Maybe.from(1)
    println(maybe.flatMap(value => Maybe.from(value)) == maybe)
  }

  ///
  locally {
    val maybe = Maybe.from(1)
    val f: java.util.function.Function[Int, Maybe[Int]] = x => Maybe.from(x * 2)
    val g: java.util.function.Function[Int, Maybe[Int]] = x => Maybe.from(x + 2)
    println(maybe.flatMap(f).flatMap(g) == maybe.flatMap(f).flatMap(g))
  }
}
