package io.sorne.tlang.lsp

object ParseMessage {

  def parseMessage(request: Request): Either[LSPError, Option[Response]] = {
    request.method match {
      case "initialize" => initialize(request)
      case "workspace/didChangeWatchedFiles" => Right(None)
      case "textDocument/completion" => TLangTextDocumentService.completion(request)
      case "textDocument/formatting" => TLangTextDocumentService.formatting(request)
      case "textDocument/didOpen" => TLangTextDocumentService.open(request)
      case "textDocument/didChange" => TLangTextDocumentService.change(request)
      //      case "textDocument/documentColor" => TLangTextDocumentService.color(request)
      case _ => Right(None)
    }
  }

  def initialize(message: Request): Either[LSPError, Option[Response]] = {
    val capabilities = Map(
      "completionProvider" -> Map(
        "triggerCharacters" -> List(".", ":"),
        "resolveProvider" -> false,
      ),
      "documentHighlightProvider" -> true,
      "documentFormattingProvider" -> true,
      //      "colorProvider" -> true,
      "textDocumentSync" -> 2
    )
    Right(Some(Response(message.jsonrpc, message.id, Map("capabilities" -> capabilities))))
  }

}
