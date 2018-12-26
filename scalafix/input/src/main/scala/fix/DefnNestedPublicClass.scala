/*
rule = CheckScaladoc
CheckScaladoc.files = ["input/src/main/scala/fix/"]
*/
package fix

/** These tests checks that the lint rule checks
  * the public members of public class, object and trait.
  */
object DefnNestedPublicClass {

  /** Test for members of public class.
    */
  class PublicClass(private val x: Int) {
    final val Const = 100 // assert: CheckScaladoc
    private val PrivateConst = 100
    /** scaladoc
      */
    final val DocumentedConst = 100

    case class CaseClass() { // assert: CheckScaladoc
      final val NestedConst = 100 // assert: CheckScaladoc
    }
  }

  /** Test for members of public trait.
    */
  trait PublicTrait {
    final val Const = 100 // assert: CheckScaladoc
    private val PrivateConst = 100
    /** scaladoc
      */
    final val DocumentedConst = 100

    case class CaseClass() { // assert: CheckScaladoc
      final val NestedConst = 100 // assert: CheckScaladoc
    }
  }

  /** Test for members of public object.
    */
  object PublicObject {
    final val Const = 100 // assert: CheckScaladoc
    private val PrivateConst = 100
    /** scaladoc
      */
    final val DocumentedConst = 100

    case class CaseClass() { // assert: CheckScaladoc
      final val NestedConst = 100 // assert: CheckScaladoc
    }
  }
}
