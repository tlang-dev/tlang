package io.sorne.tlang

import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class FirstTest extends AnyFunSuite {

  test("Just a first attempt") {
    val lexer = new TLangLexer(CharStreams.fromString("lang \"5\\\".5:!$é\"file \"MyFile.scala\""))

    val tokens = new CommonTokenStream(lexer)

    val parser = new TLangParser(tokens)
    val interpreter = new TLangInterpreterVisitor

    val lang = interpreter.visitLang(parser.lang())
    println("----------->"+lang)
    assert("\"5\\\".5:!$é\"".equals(lang))

    val file = interpreter.visitFile(parser.file())
    println("----------->"+file)
    assert("\"MyFile.scala\"".equals(file))
  }
}
