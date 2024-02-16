package dev.tlang.tlang.astbuilder

import org.antlr.v4.runtime.Token
import tlang.core
import tlang.core.Null

object AstBuilderUtils {

  def extraString(withQuotes: String): String = {
    if (withQuotes != null && withQuotes.length >= 2) {
      if (withQuotes.startsWith("\"") && withQuotes.endsWith("\"")) removeEscapedQuotes(withQuotes.substring(1, withQuotes.length - 1))
      else removeEscapedQuotes(withQuotes)
    } else if (withQuotes != null) {
      removeEscapedQuotes(withQuotes)
    } else {
      ""
    }
  }

  def extraText(withQuotes: String): String = {
    if (withQuotes != null && withQuotes.length >= 6) {
      if (withQuotes.startsWith("\"\"\"") && withQuotes.endsWith("\"\"\"")) withQuotes.substring(3, withQuotes.length - 3)
      else withQuotes
    } else if (withQuotes != null) {
      withQuotes
    } else {
      ""
    }
  }

  def getText(token: Token): Null[core.String] = {
    if (token != null) Null.of(new core.String(token.getText)) else Null.empty()
  }

  private def removeEscapedQuotes(str: String): String = {
    str.replaceAll("\\\\\"", "\"")
  }

}
