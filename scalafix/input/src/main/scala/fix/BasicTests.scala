/*
rule = CheckScaladoc
CheckScaladoc.files = ["input/src/main/scala/fix/"]
*/
package fix

// assert LinScaladoc if the leading comment is not scaladoc
object BasicTestsForDefn { // assert: CheckScaladoc
  def defn(x: Int): Int = x // assert: CheckScaladoc

  /** Do nothing
    *
    * CheckScaladoc shouldn't assert lint error
    * if def has scaladoc
    */
  def commentedDefn(): Unit = {}

  // CheckScaladoc shouldn't assert lint error
  // if it has private modifier even though
  // it doesn't have scaladoc.

  // CheckScaladoc should assert lint error
  // it doesn't have scaladoc and it doesn't have protected or private modifier.
  protected def protectedDefn(): Unit = {}
}

object BasicTestsForVar { // assert: CheckScaladoc
  var varWithoutScaladoc = 1 // assert: CheckScaladoc

  /** Dummy scaladoc
    */
  var varWithScaladoc = 1

  
  protected var protectedVar = 1
}

object BasicTestsForVal { // assert: CheckScaladoc
  val valWithoutScaladoc = 1 // assert: CheckScaladoc

  /** Dummy scaladoc
    */
  val valWithScaladoc = 1

  
  protected val protectedVal = 1
}

object BasicTestsForMacro { // assert: CheckScaladoc
  import scala.reflect.macros.blackbox.Context
  import scala.language.experimental.macros

  /** Dummy scaladoc
    */
  def implRef(c: Context)(x: c.Expr[Any]): c.Expr[Any] = x

  def macroWithoutScaladoc(x: Any): Any = macro implRef // assert: CheckScaladoc

  /** Dummy scaladoc
    */
  def macroWithScaladoc(x: Any): Any = macro implRef

  private def privateMacro(x: Any): Any = macro implRef
  protected def protectedMacro(x: Any): Any = macro implRef
}

object BasicTestsForType { // assert: CheckScaladoc
  type typ = Int // assert: CheckScaladoc

  /** Dummy scaladoc
    */
  type typWithScaladoc = Int

  private type privateTyp = Int
  protected type protectedTyp = Int
}

object BasicTestsForTrait { // assert: CheckScaladoc
  trait traitWithoutComment {} // assert: CheckScaladoc

  /** Dummy scaladoc
    */
  trait traitWithComment {}

  private trait privateTrait {}
  protected trait protectedTrait {}
}

object BasicTestsForClass { // assert: CheckScaladoc
  class classWithoutComment {} // assert: CheckScaladoc

  /** Dummy scaladoc
    */
  class classWithComment {}

  private class privateClass {}
  protected class protectedClass {}
}

object BasicTestsForObject { // assert: CheckScaladoc
  object objectWithoutComment {} // assert: CheckScaladoc

  /** Dummy scaladoc
    */
  object objectWithComment {}

  private object privateObject {}
  protected object protectedObject {}
}
