package scalafix.checkscaladoc

import scalafix.v1.SemanticDocument

import scala.meta.internal.semanticdb.TextDocument

object ScalafixAccess {
  def getTextDocument(ctx: SemanticDocument): TextDocument = ctx.internal.textDocument
}
