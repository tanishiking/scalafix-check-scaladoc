package fix

import metaconfig.annotation.{Description, ExampleValue}
import metaconfig.{ConfDecoder, ConfError, Configured, generic}
import metaconfig.generic.Surface
import scalafix.internal.config.FilterMatcher

case class CheckScaladocConfig(
  @Description("Access modifiers that allows scalafix-check-scaladoc to lint the API." +
    "For example, if `access=protected`, scalafix-check-scaladoc will lint only on APIs that is `public` or `protected`.")
  @ExampleValue("protected")
  access: Access = Public,
  @Description("Files or directories to lint.")
  @ExampleValue("[src/main/scala/example/dir/, src/main/scala/example/foo]")
  files: List[String] = List.empty
) {
  def matcher: FilterMatcher =
    FilterMatcher.matchNothing.copy(
      includeFilters = FilterMatcher.mkRegexp(files)
    )
}

object CheckScaladocConfig {
  private[fix] val default = CheckScaladocConfig()

  private[fix] implicit val surface: Surface[CheckScaladocConfig] = generic.deriveSurface[CheckScaladocConfig]
  private[fix] implicit val decoder: ConfDecoder[CheckScaladocConfig] = generic.deriveDecoder[CheckScaladocConfig](default).noTypos
}

sealed trait Access
case object Private extends Access
case object Protected extends Access
case object Public extends Access

object Access {
  private[fix] implicit val accessDecoder: ConfDecoder[Access] = ConfDecoder.stringConfDecoder.flatMap {
    case "private" => Configured.Ok(Private)
    case "protected" => Configured.Ok(Protected)
    case "public" => Configured.Ok(Public)
    case other => ConfError.message(s"Invalid access modifier: '$other'").notOk
  }
}
