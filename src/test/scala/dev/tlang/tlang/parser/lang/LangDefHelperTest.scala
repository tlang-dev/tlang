package dev.tlang.tlang.parser.lang

import dev.tlang.tlang.parser.{Lexer, LexerRunner, Pos, Seq}
import org.scalatest.funsuite.AnyFunSuite

class LangDefHelperTest extends AnyFunSuite {

  val pos: Pos = Pos(0, 0, 0)

  def defSeq: Seq = Seq("", pos = pos)

  test("Simple empty func") {
    val str =
      """func myFunc{}""".stripMargin
    val seq = defSeq
    val lexer = Lexer(List(LangDefHelper.func()))
    LexerRunner.run(str.toCharArray, pos, seq,lexer )
    assert("func" == seq.children.head.seq)
    assert("myFunc" == seq.children.head.children(1).seq)
    assert("{" == seq.children.head.children(2).seq)
    assert("}" == seq.children.head.children(3).seq)
  }

  test("Simple empty func with params") {
    val str =
      """func myFunc(param:Type, param2:Type2){}
        |""".stripMargin
    val seq = defSeq
    val lexer = Lexer(List(LangDefHelper.func()))
    LexerRunner.run(str.toCharArray, pos, seq,lexer )
    val curry = seq.children.head.children(1).children.head
    val param2 =curry.children(2)
    assert("func" == seq.children.head.seq)
    assert("myFunc" == seq.children.head.children(1).seq)
    assert("param" == curry.children.head.seq)
    assert(":" == curry.children.head.children.head.seq)
    assert("Type" == curry.children.head.children.head.children.head.seq)
    assert("," == curry.children.head.children.head.children.head.children.head.seq)
    assert("param2" == param2.seq)
    assert(":" == param2.children.head.seq)
    assert("Type2" == param2.children.head.children.head.seq)
    assert("{" == seq.children.head.children(2).seq)
    assert("}" == seq.children.head.children(3).seq)
  }

  test("Simple empty func with curry params") {
    val str =
      """func myFunc(param:Type, param2:Type2)(param3:Type3, param4:Type4){}
        |""".stripMargin
    val seq = defSeq
    val lexer = Lexer(List(LangDefHelper.func()))
    LexerRunner.run(str.toCharArray, pos, seq,lexer )
    val curry = seq.children.head.children(1).children.head
    val param2 =curry.children(2)
    val curry2 = seq.children.head.children(2).children.head
    val param4 =curry2.children(2)
    assert("func" == seq.children.head.seq)
    assert("myFunc" == seq.children.head.children(1).seq)
    assert("param" == curry.children.head.seq)
    assert(":" == curry.children.head.children.head.seq)
    assert("Type" == curry.children.head.children.head.children.head.seq)
    assert("," == curry.children.head.children.head.children.head.children.head.seq)
    assert("param2" == param2.seq)
    assert(":" == param2.children.head.seq)
    assert("Type2" == param2.children.head.children.head.seq)

    assert("param3" == curry2.children.head.seq)
    assert(":" == curry2.children.head.children.head.seq)
    assert("Type3" == curry2.children.head.children.head.children.head.seq)
    assert("," == curry2.children.head.children.head.children.head.children.head.seq)
    assert("param4" == param4.seq)
    assert(":" == param4.children.head.seq)
    assert("Type4" == param4.children.head.children.head.seq)

    assert("{" == seq.children.head.children(2).seq)
    assert("}" == seq.children.head.children(3).seq)
  }
}
