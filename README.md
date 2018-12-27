# scalafix-check-scaladoc
`scalafix-check-scaladoc` is a custom scalafix lint rule that examines scaladoc comments on classes, methods, values, type definitions, and so on.
It complains if they are visible from `scalafix-check-scaladoc` (it examines only **public** APIs by default) and are missing scaladoc comments.

## Installation
- [Install scalafix](https://scalacenter.github.io/scalafix/)

To permanently install the rule for a build, users can add the dependency to build.sbt by updating scalafixDependencies in ThisBuild.

```sh
// build.sbt
scalafixDependencies in ThisBuild +=
  "com.github.tanishiking" %% "scalafix-check-scaladoc" % "0.0.2"
```

Now `CheckScaladoc` is available.

```
// sbt shell
> scalafix CheckScaladoc
```

or configure in `.scalafix.conf`

```
rules = [
  CheckScaladoc
]
CheckScaladoc.files = ["src/main/scala/path/to/target/dir"]
```

and run `> scalafix` (on sbt shell).

## Examples
```scala
MyCode.scala:45:1: error: [CheckScaladoc] case object Test doesn't have scaladoc
[error] case object Test
[error] ^^^^^^^^^^^^^^^^
```

```scala
/** This object won't be complained because it has a scaladoc comment
  */
object Test {
  val value = 1 // this will be complained because it doesn't have scaladoc comment

  /** This won't be complained
    */
  var valueWithScaladoc = 1

  // this won't be complained even though it doesn't have scaladoc comment
  // because privateVal is private and scalafix-check-scaladoc examines
  // only on public APIs.
  private val privateVal = 1
}
```

## Configuration
|Name  |Description  |Default  |
|------|-------------|---|
|`access`|Access modifier that allow scalafix-check-scaladoc to lint the API. For example, if `access=protected`, scalafix-check-scladoc examines on public or protected APIs. |`public`  |
|`files`  |Files or dictionaries to lint |`[]`  |
|`requireDocOnInherited`  | If `requireDocOnInherited=true`, scalafix-check-scaladoc will examines on inherited methods.  |`false` |
