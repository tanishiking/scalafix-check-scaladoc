/*
rule = CheckScaladoc
*/
package fix

/** These tests checks that the lint rule doesn't
  * check the existence of scaladoc for definitions
  * of which locations are nested in val, def, var, or type alias.
  */
object DefnNested {
  val value: Int = { // assert: CheckScaladoc
    // shouldn't assert lint error even though they don't have scaladoc
    // because it is impossible to access the members defined in this block.
    var x = 1
    def foo(bar: Int) = bar
    x = foo(x)
    val y = x + 1
    y
  }

  var variable: String = { // assert: CheckScaladoc
    type foo = String
    case class Foo(
      x: foo
    )
    Foo("test").toString
  }

  def definition(foo: Int): Int = { // assert: CheckScaladoc
    val x = foo + 1
    def inc(x: Int): Int = x + 1
    var y = inc(x)
    y
  }

  type tpe = { // assert: CheckScaladoc
    type Test = Int
  }
}
