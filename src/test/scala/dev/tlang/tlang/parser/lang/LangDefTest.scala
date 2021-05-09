package dev.tlang.tlang.parser.lang

import dev.tlang.tlang.parser.{LexerRunner, Pos, Seq}
import org.scalatest.funsuite.AnyFunSuite

class LangDefTest extends AnyFunSuite {

  val pos: Pos = Pos(0, 0, 0)

  def defSeq: Seq = Seq("", pos = pos)

  test("uses") {
    val str =
      """use pkg1.pkg2.pkg3
        |use pkg4.pkg5
        |""".stripMargin
    val seq = defSeq
    LexerRunner.run(str.toCharArray, pos, seq, LangDef.domainModel())
    val use1 = seq.children.head
    val use2 = seq.children.last
    assert("use" == use1.seq)
    assert("pkg1" == use1.children(1).seq)
    assert("." == use1.children(1).children.head.seq)
    assert("pkg2" == use1.children(1).children.head.children.head.seq)
    assert("." == use1.children(1).children.last.seq)
    assert("pkg3" == use1.children(1).children.last.children.head.seq)
    assert("use" == use2.seq)
    assert("pkg4" == use2.children(1).seq)
    assert("." == use2.children(1).children.head.seq)
    assert("pkg5" == use2.children(1).children.head.children.head.seq)
  }

  test("exposes") {
    val str =
      """expose pkg1.pkg2.pkg3
        |expose pkg4.pkg5
        |""".stripMargin
    val seq = defSeq
    LexerRunner.run(str.toCharArray, pos, seq, LangDef.domainModel())
    val use1 = seq.children.head
    val use2 = seq.children.last
    assert("expose" == use1.seq)
    assert("pkg1" == use1.children(1).seq)
    assert("." == use1.children(1).children.head.seq)
    assert("pkg2" == use1.children(1).children.head.children.head.seq)
    assert("." == use1.children(1).children.last.seq)
    assert("pkg3" == use1.children(1).children.last.children.head.seq)
    assert("expose" == use2.seq)
    assert("pkg4" == use2.children(1).seq)
    assert("." == use2.children(1).children.head.seq)
    assert("pkg5" == use2.children(1).children.head.children.head.seq)
  }

  test("helper"){
    val str =
      """helper {}
        |""".stripMargin
    val seq = defSeq
    LexerRunner.run(str.toCharArray, pos, seq, LangDef.domainModel())
    assert("helper" == seq.children.head.seq)
    assert("{" == seq.children.head.children(1).seq)
    assert("}" == seq.children.head.children.last.seq)
  }

  test("model"){
    val str =
      """model {}
        |""".stripMargin
    val seq = defSeq
    LexerRunner.run(str.toCharArray, pos, seq, LangDef.domainModel())
    assert("model" == seq.children.head.seq)
    assert("{" == seq.children.head.children(1).seq)
    assert("}" == seq.children.head.children.last.seq)
  }

  test("All together") {
    val str =
      """use pkg1.pkg2.pkg3
        |expose pkg4.pkg5
        |helper {}
        |model {}
        |""".stripMargin
    val seq = defSeq
    LexerRunner.run(str.toCharArray, pos, seq, LangDef.domainModel())
    val use = seq.children.head
    val expose = seq.children(1)
    val helper = seq.children(2)
    val model = seq.children(4)
    assert("use" == use.seq)
    assert("pkg1" == use.children(1).seq)
    assert("." == use.children(1).children.head.seq)
    assert("pkg2" == use.children(1).children.head.children.head.seq)
    assert("." == use.children(1).children.last.seq)
    assert("pkg3" == use.children(1).children.last.children.head.seq)
    assert("expose" == expose.seq)
    assert("pkg4" == expose.children(1).seq)
    assert("." == expose.children(1).children.head.seq)
    assert("pkg5" == expose.children(1).children.head.children.head.seq)
    assert("helper" == helper.seq)
    assert("{" == helper.children(1).seq)
    assert("}" == helper.children.last.seq)
    assert("model" == model.seq)
    assert("{" == model.children(1).seq)
    assert("}" == model.children.last.seq)
  }

}
