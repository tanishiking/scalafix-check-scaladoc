/*
rule = CheckScaladoc
CheckScaladoc.files = ["input/src/main/scala/fix/"]
CheckScaladoc.access = "protected"
*/
package fix

object ConfigAccessProtected { // assert: CheckScaladoc
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
  private def privateDefn(): Unit = {}

  // CheckScaladoc should assert lint error
  // it doesn't have scaladoc and it doesn't have private modifier.
  protected def protectedDefn(): Unit = {} // assert: CheckScaladoc
}
