package fix

import metaconfig.Configured
import scalafix.checkscaladoc.ScalafixAccess
import scalafix.v1._

import scala.collection.mutable.ListBuffer
import scala.meta._
import scala.meta.contrib.AssociatedComments
// import scala.meta.contrib.ScaladocParser

/** CheckScaladoc checks if the public members or public package objects have scaladoc
  * and assert lint error if they don't have it.
  */
class CheckScaladoc(config: CheckScaladocConfig) extends SemanticRule("CheckScaladoc") {
  private[this] sealed trait Violation extends Diagnostic
  private[this] case class NoScaladoc(stat: Stat) extends Violation {
    override def message: String = s"$stat doesn't have scaladoc"
    override def position: scala.meta.Position = stat.pos
  }
  def this() = this(CheckScaladocConfig())

  override def withConfiguration(config: Configuration): Configured[Rule] =
    config.conf
      .getOrElse("CheckScaladoc")(this.config)
      .map { newConfig => new CheckScaladoc(newConfig) }

  override def fix(implicit doc: SemanticDocument): Patch = {
    val uri = ScalafixAccess.getTextDocument(doc).uri
    if (config.matcher.matches(uri)) {
      checkTree(doc.tree).map(Patch.lint).asPatch
    } else {
      List.empty.map(Patch.lint).asPatch
    }
  }

  private[this] def checkTree(code: Tree): List[Violation] = {
    val comments = AssociatedComments(code)
    val violations = ListBuffer[Violation]()

    def checkScaladoc(stat: Stat): List[Violation] = {
      import scala.meta.contrib.implicits.CommentExtensions._
      comments.leading(stat).filter(_.isScaladoc).toList match {
        case Nil => List(NoScaladoc(stat))
        case _ =>
          // Try to format the first sentence of a method as “Returns XXX”,
          // as in “Returns the first element of the List”,
          // as opposed to “this method returns” or “get the first” etc.
          // Methods typically return things.
          List.empty
      }
    }
    val traverser = new Traverser {
      // Scaladoc comments can go before
      // fields, methods, classes, traits, objects and even (especially) package objects.
      // https://docs.scala-lang.org/overviews/scaladoc/for-library-authors.html#where-to-put-scaladoc
      override def apply(tree: Tree): Unit =
        tree match {
          case dfn: Defn => dfn match {
            case node @ Defn.Var(mods, _, _, _) =>
              if (isRuleCandidate(mods)) violations ++= checkScaladoc(node)
            case node @ Defn.Val(mods, _, _, _) =>
              if (isRuleCandidate(mods)) violations ++= checkScaladoc(node)
            case node @ Defn.Def(mods, _, _, _, _, _) =>
              if (isRuleCandidate(mods)) violations ++= checkScaladoc(node)
            case node @ Defn.Macro(mods, _, _, _, _, _) =>
              if (isRuleCandidate(mods)) violations ++= checkScaladoc(node)
            case node @ Defn.Type(mods, _, _, _) =>
              if (isRuleCandidate(mods)) violations ++= checkScaladoc(node)
            case node @ Defn.Trait(mods, _, _, _, _) =>
              if (isRuleCandidate(mods)) {
                violations ++= checkScaladoc(node)
                super.apply(node)
              }
            case node @ Defn.Class(mods, _, _, _, _) =>
              if (isRuleCandidate(mods)) {
                violations ++= checkScaladoc(node)
                super.apply(node)
              }
            case node @ Defn.Object(mods, _, _) =>
              if (isRuleCandidate(mods)) {
                violations ++= checkScaladoc(node)
                super.apply(node)
              }
          }
          case node @ Pkg.Object(mods, _, _) =>
            if (isRuleCandidate(mods)) {
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

  private def isRuleCandidate(mods: List[Mod]): Boolean = {
    val canAccess = config.access match {
      case Private => true
      case Protected => !mods.exists(m => m.is[Mod.Private])
      case Public => !mods.exists(m => m.is[Mod.Private] || m.is[Mod.Protected])
    }
    val overriddenMethod = if (config.requireDocOnInherited) {
      true
    } else {
      !mods.exists(m => m.is[Mod.Override])
    }
    canAccess && overriddenMethod
  }
}
