package io.sorne.tlang

import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class FirstTest extends AnyFunSuite {

  test("Get lang") {
    val lexer = new TLangLexer(CharStreams.fromString("lang \"5\\\".5:!$é\""))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val lang = parser.lang().name.getText;
    assert("\"5\\\".5:!$é\"".equals(lang))
  }

  test("Get file") {
    val lexer = new TLangLexer(CharStreams.fromString("file \"MyFile.scala\""))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    assert("\"MyFile.scala\"".equals(parser.file().name.getText))
  }
}
