package dev.tlang.tlang.parser

import org.scalatest.funsuite.AnyFunSuite

class LexerRunnerTest extends AnyFunSuite {

  test("Find use") {
    val src = "use".toCharArray
    val seq = Seq("", pos = Pos(0, 0, 0))
    val lexer = Lexer(List(Child(Token("use", None), None)), None)
    LexerRunner.run(src, Pos(0, 0, 0), seq, lexer)
    assert("use" == seq.children.head.seq)
    assert(seq.children.head.children.isEmpty)
  }

  test("Find use with anything") {
    val src = "use anything".toCharArray
    val seq = Seq("", pos = Pos(0, 0, 0))
    val lexer = Lexer(List(Child(Token("use", None), Some(Lexer(List(), Some(Token("$EOF", None)))))), None)
    LexerRunner.run(src, Pos(0, 0, 0), seq, lexer)
    assert("use" == seq.children.head.seq)
    assertSeq(seq.children.head, " anything", 0)
  }

  test("Find use with end + something") {
    val src = "use anything; something".toCharArray
    val seq = Seq("", pos = Pos(0, 0, 0))
    val lexer = Lexer(List(Child(Token("use", None), Some(Lexer(List(), Some(Token(";", None)))))), Some(Token("$EOF", None)))
    LexerRunner.run(src, Pos(0, 0, 0), seq, lexer)
    assert("use" == seq.children.head.seq)
    assertSeq(seq.children.head, " anything", 0)
    assertSeq(seq, "; something", 1)
  }

  test("Find use with end + another use") {
    val src = "use anything; use something".toCharArray
    val seq = Seq("", pos = Pos(0, 0, 0))
    val lexer = Lexer(List(Child(Token("use", None), Some(Lexer(List(), Some(Token(";", None)))))), Some(Token("$EOF", None)))
    LexerRunner.run(src, Pos(0, 0, 0), seq, lexer)
    assert("use" == seq.children.head.seq)
    assertSeq(seq.children.head, " anything", 0)
    assert(";" == seq.children(1).seq)
    assert(" " == seq.children(2).seq)
    assert("use" == seq.children.last.seq)
    assertSeq(seq.children.last, " something", 0)
  }

  test("Find use with ID") {
    val src = "use anything".toCharArray
    val seq = Seq("", pos = Pos(0, 0, 0))
    val lexer = Lexer(List(Child(Token("use", None), Some(Lexer(List(
      Child(Token("$ID", None), Some(Lexer(List(), None)))
    ), Some(Token("$EOF", None)))))), None)
    LexerRunner.run(src, Pos(0, 0, 0), seq, lexer)
    assert("use" == seq.children.head.seq)
    assert(" " == seq.children.head.children.head.seq)
    assert("anything" == seq.children.head.children.last.seq)
  }

  test("Find use with variable parts") {
    val src = "use .pkg1.pkg2.pkg3".toCharArray
    val seq = Seq("", pos = Pos(0, 0, 0))
    val lexer = Lexer(List(Child(Token("use", None), Some(Lexer(List(
      Child(Token(".", None), Some(Lexer(List(Child(Token("$ID", None), Some(Lexer(List(), None)))), None)))
    ), Some(Token("$EOF", None)))))), None)
    LexerRunner.run(src, Pos(0, 0, 0), seq, lexer)
    assert("use" == seq.children.head.seq)
    assert(" " == seq.children.head.children.head.seq)
    assert("." == seq.children.head.children(1).seq)
    assert("pkg1" == seq.children.head.children(1).children.head.seq)
    assert("." == seq.children.head.children(2).seq)
    assert("pkg2" == seq.children.head.children(2).children.head.seq)
    assert("." == seq.children.head.children.last.seq)
    assert("pkg3" == seq.children.head.children.last.children.head.seq)
  }

  test("Find use with variable parts without first .") {
    val src = "use pkg1.pkg2.pkg3".toCharArray
    val seq = Seq("", pos = Pos(0, 0, 0))
    val lexer = Lexer(List(Child(Token("use", None), Some(Lexer(List(
      Child(Token("$ID", None), Some(Lexer(List(Child(Token(".", None), Some(Lexer(List(Child(Token("$ID", None), Some(Lexer(List(), None)))), None)))), None))),
    ), Some(Token("$EOF", None)))))), None)
    LexerRunner.run(src, Pos(0, 0, 0), seq, lexer)
    assert("use" == seq.children.head.seq)
    assert(" " == seq.children.head.children.head.seq)
    assert("pkg1" == seq.children.head.children.last.seq)
    assert("." == seq.children.head.children.last.children.head.seq)
    assert("pkg2" == seq.children.head.children.last.children.head.children.head.seq)
    assert("." == seq.children.head.children.last.children.last.seq)
    assert("pkg3" == seq.children.head.children.last.children.last.children.head.seq)
  }

  def assertSeq(seq: Seq, str: String, startPos: Int): Unit = {
    str.toSeq.zipWithIndex.foreach(s => assert(s._1.toString == seq.children(s._2 + startPos).seq))
  }
}
