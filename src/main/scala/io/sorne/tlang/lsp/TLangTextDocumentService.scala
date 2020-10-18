package io.sorne.tlang.lsp

import io.sorne.tlang.lsp.context.{Context, CurrentFile}

object TLangTextDocumentService {

  def completion(request: Request): Either[LSPError, Option[Response]] = {
    Right(Some(Response(request.jsonrpc, request.id, Map("isIncomplete" -> false,
      "items" -> List(
        CompletionItem("helper"),
        CompletionItem("model"),
        CompletionItem("tmpl"),
      )))))
  }

  def formatting(request: Request): Either[LSPError, Option[Response]] = {
    Right(Some(Response(request.jsonrpc, request.id, List(
      TextEdit(Range(Position(1, 1), Position(1, 12)), "My new data"),
    ))))
  }

  def color(request: Request): Either[LSPError, Option[Response]] = {
    Right(Some(Response(request.jsonrpc, request.id, List(
      ColorInformation(Range(Position(0, 0), Position(0, 6)), Color(0.11f, 0.678f, 0.192f, 1)))
    )))
  }

  def open(request: Request): Either[LSPError, Option[Response]] = {
    request.params.get("textDocument").foreach(doc => {
      val asMap = doc.asInstanceOf[Map[String, Any]]
      Context.openFile(CurrentFile(asMap.getOrElse("uri", "").toString, new StringBuilder(asMap.getOrElse("text", "").toString)))
    })
    Right(None)
  }

  def change(request: Request): Either[LSPError, Option[Response]] = {
    request.params.get("contentChanges").foreach(changes => {
      changes.asInstanceOf[List[Map[String, Any]]].foreach(asMap => {
        val text = asMap.getOrElse("text", "").toString
        val rangeLength: Int = asMap.getOrElse("rangeLength", "0").toString.toInt
        asMap.get("range").foreach(range => {
          val rangeAsMap = range.asInstanceOf[Map[String, Any]]
          rangeAsMap.get("start").foreach(x => {
            val start = x.asInstanceOf[Map[String, BigInt]]
            rangeAsMap.get("end").foreach(y => {
              val end = y.asInstanceOf[Map[String, BigInt]]
              val finalRange = Range(Position(start.getOrElse("line", BigInt(0)).intValue, start.getOrElse("character", BigInt(0)).intValue),
                Position(end.getOrElse("line", BigInt(0)).intValue, end.getOrElse("character", BigInt(0)).intValue))
              Context.change(finalRange, rangeLength, text)
            })
          })
        })
      })
      //      Context.change()

    })
    Right(None)
  }

  case class TextEdit(range: Range, newText: String)

  case class CompletionItem(label: String)

  case class Range(start: Position, end: Position)

  case class Position(line: Int, character: Int)

  case class ColorInformation(range: Range, color: Color)

  case class Color(red: Float, blue: Float, green: Float, alpha: Float)

}
