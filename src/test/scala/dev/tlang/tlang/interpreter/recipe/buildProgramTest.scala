package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.astbuilder.{BuildAst, BuildHelperBlock}
import dev.tlang.tlang.loader.{Module, Resource, manifest}
import dev.tlang.tlang.{CommonLexer, TLang}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuiteLike
import tlang.core
import tlang.internal.ContextResource

class buildProgramTest extends AnyFunSuiteLike {

  val fakeContext: ContextResource = new ContextResource(new core.String(""), new core.String(""), new core.String(""), new core.String(""))

  val fakeManifest: manifest.Manifest = manifest.Manifest("", "", "", "", None, 0, None, None)

  test("test buildProgram") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(1 == 1) {
        |} else {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val domain = BuildAst.build(fakeContext, parser.domainModel())
    val context = BuilderContext(module = Module("test", fakeManifest, Map(), None, ""), resource = Resource("test", "test", "test", "test", domain))
    BuildProgram.buildProgram(context)
    context
  }

}
