/*
rule = CheckScaladoc
CheckScaladoc.files = ["input/src/main/scala/fix/"]
CheckScaladoc.requireDocOnInherited = false
*/
package fix

/** Object
  */
object ObjectDoNotRequireDocOnInherited extends DefnTrait {

  override def defn(x: Int): Int = x
}

/** Trait
  */
trait DefnTrait {
  /** Dummy
    */
  def defn(x: Int): Int = x
}
