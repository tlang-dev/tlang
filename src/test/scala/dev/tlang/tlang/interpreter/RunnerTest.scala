package dev.tlang.tlang.interpreter

import dev.tlang.tlang.astbuilder.BuildAst
import dev.tlang.tlang.interpreter.recipe.{BuildProgram, BuilderContext, Parameter}
import dev.tlang.tlang.loader.{Module, Resource, manifest}
import dev.tlang.tlang.{CommonLexer, TLang}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuiteLike
import tlang.core
import tlang.internal.ContextResource

class RunnerTest extends AnyFunSuiteLike {

  val fakeContext: ContextResource = new ContextResource(new core.String(""), new core.String(""), new core.String("TLang.Runner"), new core.String("RunnerTest"))

  val fakeManifest: manifest.Manifest = manifest.Manifest("RunnerTest", "Runner", "TLang", "0.0.0", None, 0, None, None)

  test("test helper with if") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |
        |func myFunc2 {
        |}
        |
        |func myFunc {
        | if(1 == 1) {
        |  myFunc2()
        |}
        |}
        |
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val domain = BuildAst.build(fakeContext, parser.domainModel())
    val context = BuilderContext(module = Module("test", fakeManifest, Map(), None, ""), resource = Resource("test", "test", "test", "test", domain))
    BuildProgram.buildProgram(context, domain)
    val logger = new TestLogger
    val parameter = Parameter(0, 7, logger)
    new Runner().run(context.program, parameter)
    val logs = logger.getLogs
    assert(logs(18) == "Jumping to: 0:2")
  }

  test("test run model") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """model {
        |
        |let str = "This is a test"
        |
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val domain = BuildAst.build(fakeContext, parser.domainModel())
    val context = BuilderContext(module = Module("test", fakeManifest, Map(), None, ""), resource = Resource("test", "test", "test", "test", domain))
    BuildProgram.buildProgram(context, domain)
    val logger = new TestLogger
    val parameter = Parameter(0, 0, logger)
    new Runner().run(context.program, parameter)
    val logs = logger.getLogs
    assert(logs(8) == "Jumping to: 0:6")
    assert(logs(12) == "SetLazyStatic: [TLang.Runner.RunnerTest] replaced at 0")
  }

  test("test run model with entity") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """model {
        |
        |let myEntity = {
        |attr1 = 5
        |}
        |
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val domain = BuildAst.build(fakeContext, parser.domainModel())
    val context = BuilderContext(module = Module("test", fakeManifest, Map(), None, ""), resource = Resource("test", "test", "test", "test", domain))
    BuildProgram.buildProgram(context, domain)
    val logger = new TestLogger
    val parameter = Parameter(0, 0, logger)
    new Runner().run(context.program, parameter)
    val logs = logger.getLogs
    assert(logs(8) == "Jumping to: 0:6")
    assert(logs(18) == "Jumping to: 0:12")
  }

  test("test call func below") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |
        |func myFunc {
        | if(1 == 1) {
        |  myFunc2()
        |}
        |}
        |
        |func myFunc2 {
        |}
        |
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val domain = BuildAst.build(fakeContext, parser.domainModel())
    val context = BuilderContext(module = Module("test", fakeManifest, Map(), None, ""), resource = Resource("test", "test", "test", "test", domain))
    BuildProgram.buildProgram(context, domain)
    val logger = new TestLogger
    val parameter = Parameter(0, 0, logger)
    new Runner().run(context.program, parameter)
    val logs = logger.getLogs
    assert(logs(21) == "Jumping to: 0:19")
  }

}