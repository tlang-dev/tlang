package io.sorne.tlang.lsp.indent

import java.util.regex.Pattern

import io.sorne.tlang.lsp.TLangTextDocumentService.TextEdit
import io.sorne.tlang.lsp.context.ContentUtils
import io.sorne.tlang.lsp.{LSPError, TLangTextDocumentService}

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

object Indent {

  val NON_SPACE_OR_TAB: Pattern = Pattern.compile("[^(\\s |\\t)]")

  def indent(content: StringBuilder): Either[LSPError, List[TextEdit]] = {
    Right(doNextBracket(content, 0, 0, new ListBuffer[TextEdit]).toList)
  }

  @tailrec
  def doNextBracket(content: StringBuilder, startPos: Int, indentLevel: Int, edits: ListBuffer[TextEdit]): ListBuffer[TextEdit] = {
    if (startPos >= content.length() - 1) edits
    else {
      val endPos = findEndingPosition(content, startPos)
      val delta = indentLineByLine(content, startPos, endPos, indentLevel, edits)
      val newLevel = if (content.substring(endPos + delta, endPos + delta + 1).equals("{")) indentLevel + 1 else indentLevel - 1
      doNextBracket(content, endPos + delta + 1, newLevel, edits)
    }
  }

  @tailrec
  def indentLineByLine(content: StringBuilder, startPos: Int, endPos: Int, indentLevel: Int, edits: ListBuffer[TextEdit], delta: Int = 0): Int = {
    if (startPos >= endPos) delta
    else {
      var endLinePos = content.indexOf("\n", startPos)
      if (endLinePos < 0) endLinePos = endPos
      val line = content.substring(startPos, endLinePos)
      var totalDelta = delta
      var currentDelta = 0
      if (!line.matches("^([^\\S\\t]{" + (indentLevel * 4) + "}\\S).*")) {
        val matcher = NON_SPACE_OR_TAB.matcher(line)
        if (matcher.find()) {
          val codePos = matcher.start()
          val spaces = genSpaces(indentLevel * 4)
          if (codePos == 0) content.insert(startPos, spaces)
          else content.replace(startPos, startPos + codePos, spaces)
          currentDelta = spaces.length - codePos
          totalDelta += currentDelta
          edits.addOne(TextEdit(TLangTextDocumentService.Range(ContentUtils.findPositionByCharNumber(content, startPos),
            ContentUtils.findPositionByCharNumber(content, startPos + codePos)), spaces))
        }
      }
      indentLineByLine(content, endLinePos + currentDelta + 1, endPos + currentDelta, indentLevel, edits, totalDelta)
    }
  }

  def findEndingPosition(content: StringBuilder, position: Int): Int = {
    val nextOpeningBracket = content.indexOf("{", position)
    val nextClosingBracket = content.indexOf("}", position)
    if (nextOpeningBracket > -1 && nextOpeningBracket < nextClosingBracket) nextOpeningBracket else nextClosingBracket
  }

  private def genSpaces(total: Int): String = {
    val spaces = new Array[Char](total)
    for (i <- 0 until total) spaces(i) = ' '
    String.valueOf(spaces)
  }

  def indexOfBackward(str: StringBuilder, start: Int): Unit = {

  }

}
