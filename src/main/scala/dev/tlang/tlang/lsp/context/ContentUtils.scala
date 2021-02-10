package dev.tlang.tlang.lsp.context

import dev.tlang.tlang.lsp.TLangTextDocumentService.Position
import io.sorne.tlang.lsp.TLangTextDocumentService.Position

object ContentUtils {

  def findCharNumberByPosition(content: StringBuilder, position: Position): Int = {
    var pos = content.indexOf("\n")
    for (_ <- 2 to position.line) {
      pos = content.indexOf("\n", pos + 1)
    }
    pos + position.character + 1
  }

  def findPositionByCharNumber(content: StringBuilder, pos: Int): Position = {
    var line = -1
    var retPos = -1
    var prevPos = 0
    do {
      line += 1
      prevPos = retPos + 1
      retPos = content.indexOf("\n", retPos + 1)
    } while (retPos != -1 && retPos < pos)
    Position(line, pos - prevPos)
  }

  def replace(content: StringBuilder, start: Position, end: Position, newText: String): StringBuilder = {
    content.replace(findCharNumberByPosition(content, start), findCharNumberByPosition(content, end), newText)
  }

}
