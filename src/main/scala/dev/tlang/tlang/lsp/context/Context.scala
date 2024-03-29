package dev.tlang.tlang.lsp.context

import dev.tlang.tlang.lsp.TLangTextDocumentService

class Context(var currentFile: CurrentFile) {

}

object Context {

  val context: Context = new Context(null);

  def openFile(currentFile: CurrentFile): Unit = {
    context.currentFile = currentFile
  }

  def change(range: TLangTextDocumentService.Range, rangeLength: Int, change: String): Unit = {
    ContentUtils.replace(context.currentFile.content, range.start, range.end, change)
  }

}
