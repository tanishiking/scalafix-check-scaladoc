package fix

import scalafix.v1._

import scala.collection.mutable.ListBuffer
import scala.meta.Mod.Protected
import scala.meta._
import scala.meta.contrib.AssociatedComments
// import scala.meta.contrib.ScaladocParser

/** Check if the public members or public package objects have scaladoc
  * and assert lint error if they don't have it.
  */
class CheckScaladoc extends SyntacticRule("CheckScaladoc") {
  private[this] sealed trait Violation extends Diagnostic
  private[this] case class NoScaladoc(stat: Stat) extends Violation {
    override def message: String = s"$stat doesn't have scaladoc"
    override def position: scala.meta.Position = stat.pos
  }

  override def fix(implicit doc: SyntacticDocument): Patch = {
    checkTree(doc.tree).map(Patch.lint).asPatch
  }

  private[this] def checkTree(code: Tree): List[Violation] = {
    val comments = AssociatedComments(code)
    val violations = ListBuffer[Violation]()

    def checkScaladoc(stat: Stat): List[Violation] = {
      import scala.meta.contrib.implicits.CommentExtensions._
      comments.leading(stat).filter(_.isScaladoc).toList match {
        case Nil => List(NoScaladoc(stat))
        case scaladocComment =>
          // Try to format the first sentence of a method as “Returns XXX”,
          // as in “Returns the first element of the List”,
          // as opposed to “this method returns” or “get the first” etc.
          // Methods typically return things.
          List.empty
      }
    }
    val traverser = new Traverser {
      import CheckScaladoc.ModsOps
      // Scaladoc comments can go before
      // fields, methods, classes, traits, objects and even (especially) package objects.
      // https://docs.scala-lang.org/overviews/scaladoc/for-library-authors.html#where-to-put-scaladoc
      override def apply(tree: Tree): Unit =
        tree match {
          case dfn: Defn => dfn match {
            case node @ Defn.Var(mods, _, _, _) =>
              if (mods.isPublic) violations ++= checkScaladoc(node)
            case node @ Defn.Val(mods, _, _, _) =>
              if (mods.isPublic) violations ++= checkScaladoc(node)
            case node @ Defn.Def(mods, _, _, _, _, _) =>
              if (mods.isPublic) violations ++= checkScaladoc(node)
            case node @ Defn.Macro(mods, _, _, _, _, _) =>
              if (mods.isPublic) violations ++= checkScaladoc(node)
            case node @ Defn.Type(mods, _, _, _) =>
              if (mods.isPublic) violations ++= checkScaladoc(node)
            case node @ Defn.Trait(mods, _, _, _, _) =>
              if (mods.isPublic) {
                violations ++= checkScaladoc(node)
                super.apply(node)
              }
            case node @ Defn.Class(mods, _, _, _, _) =>
              if (mods.isPublic) {
                violations ++= checkScaladoc(node)
                super.apply(node)
              }
            case node @ Defn.Object(mods, _, _) =>
              if (mods.isPublic) {
                violations ++= checkScaladoc(node)
                super.apply(node)
              }
          }
          case node @ Pkg.Object(mods, _, _) =>
            if (mods.isPublic) {
              violations ++= checkScaladoc(node)
              super.apply(node)
            }
          case other =>
            super.apply(other)
        }
    }
    traverser(code)
    violations.toList
  }
}

private object CheckScaladoc {
  private implicit class ModsOps(private val mods: List[Mod]) extends AnyVal {
    def isPublic: Boolean = !mods.exists(m => m.is[Mod.Private] || m.is[Protected])
  }
}
