package io.sorne.tlang.astbuilder

object Utils {

  def extraString(withQuotes: String): String = {
    if (withQuotes != null && withQuotes.length > 2) {
      if (withQuotes.startsWith("\"") && withQuotes.endsWith("\"")) withQuotes.substring(1, withQuotes.length - 1)
      else withQuotes
    } else {
      ""
    }
  }

}
