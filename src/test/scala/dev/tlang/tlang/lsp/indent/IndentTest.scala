package dev.tlang.tlang.lsp.indent

import dev.tlang.tlang.lsp.TLangTextDocumentService.TextEdit
import dev.tlang.tlang.lsp.TLangTextDocumentService
import dev.tlang.tlang.lsp.TLangTextDocumentService.Position
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable.ListBuffer

class IndentTest extends AnyFunSuite {

  test("Indenting one line") {
    val txt = "This is a text"
    val str = new StringBuilder(txt)
    val changes = new ListBuffer[TextEdit]
    val delta = Indent.indentLineByLine(str, 0, str.length(), 1, changes)
    assert(4 == delta)
    assert(changes.head.newText.equals("    "))
    assert(changes.head.range.equals(TLangTextDocumentService.Range(Position(0, 0), Position(0, 0))))
    assert(("    " + txt).equals(str.toString()))
  }

  test("Indenting one line falsely indented") {
    val txt = "This is a text"
    val str = new StringBuilder("\t \t " + txt)
    val changes = new ListBuffer[TLangTextDocumentService.TextEdit]
    val delta = Indent.indentLineByLine(str, 0, str.length(), 1, changes)
    assert(0 == delta)
    assert(changes.head.newText.equals("    "))
    assert(changes.head.range.equals(TLangTextDocumentService.Range(Position(0, 0), Position(0, 4))))
    assert(("    " + txt).equals(str.toString()))
  }

  test("Indenting one line already indented") {
    val txt = "This is a text"
    val str = new StringBuilder("    " + txt)
    val changes = new ListBuffer[TLangTextDocumentService.TextEdit]
    val delta = Indent.indentLineByLine(str, 0, str.length(), 1, changes)
    assert(0 == delta)
    assert(changes.isEmpty)
    assert(("    " + txt).equals(str.toString()))
  }

  test("Test remove indent") {
    val txt = "This is a text"
    val str = new StringBuilder("    " + txt)
    val changes = new ListBuffer[TLangTextDocumentService.TextEdit]
    val delta = Indent.indentLineByLine(str, 0, str.length(), 0, changes)
    assert(-4 == delta)
    assert(changes.head.newText.equals(""))
    assert(changes.head.range.equals(TLangTextDocumentService.Range(Position(0, 0), Position(0, 4))))
    assert(txt.equals(str.toString()))
  }

  test("Test indent multiple lines") {
    val txt = "This is a text\n" +
      "  with another line\n" +
      "\t \t And a third one\n" +
      "    Even a fourth one\n" +
      "    A fifth one\n" +
      "  \t  A sixth one\n" +
      "     And a seventh one"


    val txtRes = "    This is a text\n" +
      "    with another line\n" +
      "    And a third one\n" +
      "    Even a fourth one\n" +
      "    A fifth one\n" +
      "    A sixth one\n" +
      "    And a seventh one"
    val str = new StringBuilder(txt)
    Indent.indentLineByLine(str, 0, str.length(), 1, new ListBuffer[TLangTextDocumentService.TextEdit])
    assert(txtRes.equals(str.toString()))
  }

  test("Test with one set of {}") {
    val txt =
      """   One first line{
        |The second line
        |The third line
        |}""".stripMargin
    val txtRes =
      """One first line{
        |    The second line
        |    The third line
        |}""".stripMargin
    val str = new StringBuilder(txt)
    Indent.doNextBracket(str, 0, 0, new ListBuffer[TLangTextDocumentService.TextEdit])
    assert(txtRes.equals(str.toString()))
  }

  /*
  This step does not change the bracket positions, just the content
   */
  test("Test with nested set of {}") {
    val txt =
      """   One first line{
        |The second line
        |The third line
        |Nested Bracket{
        |  Nested Line One
        |        Nested Line Two
        |Nested Line Three
        |}}""".stripMargin
    val txtRes =
      """One first line{
        |    The second line
        |    The third line
        |    Nested Bracket{
        |        Nested Line One
        |        Nested Line Two
        |        Nested Line Three
        |}}""".stripMargin
    val str = new StringBuilder(txt)
    val changes = new ListBuffer[TLangTextDocumentService.TextEdit]
    Indent.doNextBracket(str, 0, 0, changes)
    assert(6 == changes.size)
    assert(txtRes.equals(str.toString()))
  }

}
