/*
rule = CheckScaladoc
CheckScaladoc.files = ["input/src/main/scala/fix/nothing"]
*/
package fix

// do not assert error because regex doesn't match
object ObjectThatWontBeChecked {
  def defn(x: Int): Int = x

  def commentedDefn(): Unit = {}

  private def privateDefn(): Unit = {}

  protected def protectedDefn(): Unit = {}
}
