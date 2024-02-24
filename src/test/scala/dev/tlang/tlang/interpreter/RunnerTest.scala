package dev.tlang.tlang.interpreter

import dev.tlang.tlang.astbuilder.BuildAst
import dev.tlang.tlang.interpreter.recipe.{BuildProgram, BuilderContext}
import dev.tlang.tlang.{CommonLexer, TLang}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuiteLike
import tlang.core
import tlang.internal.ContextResource

class RunnerTest extends AnyFunSuiteLike {

  val fakeContext: ContextResource = new ContextResource(new core.String(""), new core.String(""), new core.String(""), new core.String(""))

  test("test buildProgram") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(1 == 2) {
        |} else {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val domain = BuildAst.build(fakeContext, parser.domainModel())
    val context = BuilderContext()
    BuildProgram.buildProgram(context, domain)
    new Runner().run(context.program, 2)
  }

}
