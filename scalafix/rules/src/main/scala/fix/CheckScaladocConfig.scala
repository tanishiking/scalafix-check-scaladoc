package fix

import metaconfig.annotation.{Description, ExampleValue}
import metaconfig.{ConfDecoder, ConfError, Configured, generic}
import metaconfig.generic.Surface
import scalafix.internal.config.FilterMatcher

/** Configuration for scalafix-check-scaladoc
  *
  * @param access Access modifier that allows scalafix-check-scaladoc to check the APIs.
  * @param files Files or directories to lint.
  */
private[fix] case class CheckScaladocConfig(
  @Description("Access modifier that allows scalafix-check-scaladoc to lint the API." +
    "For example, if `access=protected`, scalafix-check-scaladoc will lint only on APIs that is `public` or `protected`.")
  @ExampleValue("protected")
  access: Access = Public,
  @Description("Files or directories to lint.")
  @ExampleValue("""["src/main/scala/example/dir/", "src/main/scala/example/foo"]""")
  files: List[String] = List.empty,
  @Description("If requireDocOnInherited=true, scalafix-check-scaladoc check a scaladoc existence on inherited methods.")
  @ExampleValue("false")
  requireDocOnInherited: Boolean = false
) {
  /** matcher returns FilterMatcher that will check if
    * a textDocument should be checked or not.
    */
  def matcher: FilterMatcher =
    FilterMatcher.matchNothing.copy(
      includeFilters = FilterMatcher.mkRegexp(files)
    )
}

private[fix] object CheckScaladocConfig {
  private[fix] val default = CheckScaladocConfig()

  private[fix] implicit val surface: Surface[CheckScaladocConfig] = generic.deriveSurface[CheckScaladocConfig]
  private[fix] implicit val decoder: ConfDecoder[CheckScaladocConfig] = generic.deriveDecoder[CheckScaladocConfig](default).noTypos
}

private[fix] sealed trait Access
private[fix] case object Private extends Access
private[fix] case object Protected extends Access
private[fix] case object Public extends Access

private[fix] object Access {
  private[fix] implicit val accessDecoder: ConfDecoder[Access] = ConfDecoder.stringConfDecoder.flatMap {
    case "private" => Configured.Ok(Private)
    case "protected" => Configured.Ok(Protected)
    case "public" => Configured.Ok(Public)
    case other => ConfError.message(s"Invalid access modifier: '$other'").notOk
  }
}
