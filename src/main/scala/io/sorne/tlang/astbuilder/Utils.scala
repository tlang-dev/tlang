package io.sorne.tlang.astbuilder

import org.antlr.v4.runtime.Token

object Utils {

  def extraString(withQuotes: String): String = {
    if (withQuotes != null && withQuotes.length > 2) {
      if (withQuotes.startsWith("\"") && withQuotes.endsWith("\"")) withQuotes.substring(1, withQuotes.length - 1)
      else withQuotes
    } else {
      ""
    }
  }

  def getText(token: Token): Option[String] = {
    if (token != null) Some(token.getText) else None
  }

}
