package dev.tlang.tlang.parser

import org.scalatest.funsuite.AnyFunSuite

class LexerRunnerTest extends AnyFunSuite {

  test("Find use") {
    val src = "use".toCharArray
    val seq = Seq("", pos = Pos(0, 0, 0))
    val lexer = Lexer(List((Token("use", None), Lexer(List(), None))), None)
    LexerRunner.run(src, Pos(0, 0, 0), seq, lexer)
    assert("use" == seq.children.head.seq)
    assert(seq.children.head.children.isEmpty)
  }

  test("Find use with anything") {
    val src = "use anything".toCharArray
    val seq = Seq("", pos = Pos(0, 0, 0))
    val lexer = Lexer(List((Token("use", None), Lexer(List(), None))), None)
    LexerRunner.run(src, Pos(0, 0, 0), seq, lexer)
    assert("use" == seq.children.head.seq)
    assertSeq(seq.children.head.children.head, " anything")
  }

  test("Find use with end + something") {
    val src = "use anything; something".toCharArray
    val seq = Seq("", pos = Pos(0, 0, 0))
    val lexer = Lexer(List((Token("use", None), Lexer(List(), Some(Token(";", None))))), None)
    LexerRunner.run(src, Pos(0, 0, 0), seq, lexer)
    assert("use" == seq.children.head.seq)
    assertSeq(seq.children.head.children.head, " anything;")
    assertSeq(seq.children.last, " something")
  }

  test("Find use with end + another use") {
    val src = "use anything; use something".toCharArray
    val seq = Seq("", pos = Pos(0, 0, 0))
    val lexer = Lexer(List((Token("use", None), Lexer(List(), Some(Token(";", None))))), None)
    LexerRunner.run(src, Pos(0, 0, 0), seq, lexer)
    assert("use" == seq.children.head.seq)
    assertSeq(seq.children.head.children.head, " anything;")
    assert(" " == seq.children(1).seq)
    assert("use" == seq.children.last.seq)
    assertSeq(seq.children.last.children.head, " something")
  }

  test("Find use with parts") {
    val src = "use pkg.pkg.pkg".toCharArray
    val seq = Seq("", pos = Pos(0, 0, 0))
    val lexer = Lexer(List((Token("use", None), Lexer(List(
      (Token("pkg", None), Lexer(List(), Some(Token("$AFTER_ONE", None)))),
      (Token(".", None), Lexer(List(), Some(Token("pkg", None))))
    ), None))), None)
    LexerRunner.run(src, Pos(0, 0, 0), seq, lexer)
    assert("use" == seq.children.head.seq)
    assert(" " == seq.children.head.children.head.seq)
    assert("pkg" == seq.children.head.children(1).seq)
    assert("." == seq.children.head.children(2).seq)
    assert("pkg" == seq.children.head.children(2).children.head.seq)
    assert("." == seq.children.head.children.last.seq)
    assert("pkg" == seq.children.head.children.last.children.head.seq)
  }

  test("Find use with variable parts") {
    val src = "use pkg1.pkg2.pkg3".toCharArray
    val seq = Seq("", pos = Pos(0, 0, 0))
    val lexer = Lexer(List((Token("use", None), Lexer(List(
      (Token("$ID", None), Lexer(List(), Some(Token("$AFTER_ONE", None)))),
      (Token(".", None), Lexer(List((Token("$ID", None), Lexer(List(), Some(Token("$AFTER_ONE", None))))), Some(Token("$AFTER_ONE", None))))
    ), None))), None)
    LexerRunner.run(src, Pos(0, 0, 0), seq, lexer)
    assert("use" == seq.children.head.seq)
    assert(" " == seq.children.head.children.head.seq)
    assert("pkg1" == seq.children.head.children(1).seq)
    assert("." == seq.children.head.children(2).seq)
    assert("pkg2" == seq.children.head.children(2).children.head.seq)
    assert("." == seq.children.head.children.last.seq)
    assert("pkg3" == seq.children.head.children.last.children.head.seq)
  }

  def assertSeq(seq: Seq, str: String): Unit = {
    var currentSeq = seq
    str.foreach(s => {
      assert(s.toString == currentSeq.seq)
      currentSeq = currentSeq.children.headOption.orNull
    })
  }
}
