/*
rule = CheckScaladoc
*/
package fix

/** These tests checks that the lint rule doesn't
  * assert lint error on definitions of which
  * locations are in non-public class, trait, object.
  */
object DefnNestedNonPublicClass {
  protected object protectedObj {
    val foo = 1
    var bar = 2
    def test(a: Int) = a
    case class Test(a: Int)
    object testObj {
      val foo = 1
    }
  }

  private case class privateClass() {
    val foo = 1
    var bar = 2
  }

  private trait privateTrait {
    def test(a: Int) = a
  }
}
