/*
rule = CheckScaladoc
CheckScaladoc.files = ["input/src/main/scala/fix/"]
CheckScaladoc.requireDocOnInherited = true
*/
package fix

/** Object
  */
object ObjectRequireDocOnInherited extends TraitRequireDocOnInherited {

  override def defn(x: Int): Int = x // assert: CheckScaladoc
}

/** Trait
  */
trait TraitRequireDocOnInherited {
  /** Dummy
    */
  def defn(x: Int): Int = x
}
