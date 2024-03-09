package dev.tlang.tlang.interpreter

import dev.tlang.tlang.astbuilder.BuildAst
import dev.tlang.tlang.interpreter.recipe.{BuildProgram, BuilderContext, Parameter}
import dev.tlang.tlang.loader.{Module, Resource, manifest}
import dev.tlang.tlang.resolver.{BuildCallObjectLink, BuildLinkTree, PathContext, ResolverContext}
import dev.tlang.tlang.{CommonLexer, TLang}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuiteLike
import tlang.core
import tlang.internal.ContextResource

class RunnerTest extends AnyFunSuiteLike {

  val fakeContext: ContextResource = new ContextResource(new core.String(""), new core.String(""), new core.String("TLang/Runner"), new core.String("RunnerTest"))

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
    val context = BuilderContext(module = Module("test", fakeManifest, Map(), None, ""), resource = Resource("", "TLang", "Runner", "RunnerTest", domain))
    val resContext = ResolverContext(module = Module("test", fakeManifest, Map(), None, ""), resource = Resource("", "TLang", "Runner", "RunnerTest", domain))
    val callables = BuildLinkTree.buildLinkTree(resContext).toOption.get
    val pathContext = PathContext(resContext.resource, callables, resContext.getFullPkg, resContext.resource.name, "")
    BuildCallObjectLink.buildCallObjectLink(pathContext)
    context.callables ++= callables
    BuildProgram.buildProgram(context)
    val logger = new TestLogger
    val parameter = Parameter(0, 7, logger)
    new Runner().run(context.program, parameter)
    val logs = logger.getLogs
    assert(logs(19) == "Jumping to: 0:2")
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
    BuildProgram.buildProgram(context)
    val logger = new TestLogger
    val parameter = Parameter(0, 0, logger)
    new Runner().run(context.program, parameter)
    val logs = logger.getLogs
    assert(logs(5) == "Jumping to: 0:6")
    assert(logs(9) == "SetLazyStatic: [TLang/Runner/RunnerTest] replaced at 0")
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
    BuildProgram.buildProgram(context)
    val logger = new TestLogger
    val parameter = Parameter(0, 0, logger)
    new Runner().run(context.program, parameter)
    val logs = logger.getLogs
    assert(logs(5) == "Jumping to: 0:6")
    assert(logs(15) == "Jumping to: 0:12")
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
    val context = BuilderContext(module = Module("test", fakeManifest, Map(), None, ""), resource = Resource("", "TLang", "Runner", "RunnerTest", domain))
    val resContext = ResolverContext(module = Module("test", fakeManifest, Map(), None, ""), resource = Resource("", "TLang", "Runner", "RunnerTest", domain))
    val callables = BuildLinkTree.buildLinkTree(resContext).toOption.get
    val pathContext = PathContext(resContext.resource, callables, resContext.getFullPkg, resContext.resource.name, "")
    BuildCallObjectLink.buildCallObjectLink(pathContext)
    context.callables ++= callables
    BuildProgram.buildProgram(context)
    val logger = new TestLogger
    val parameter = Parameter(0, 0, logger)
    new Runner().run(context.program, parameter)
    val logs = logger.getLogs
    assert(logs(20) == "Jumping to: 0:20")
  }

  test("test call func with return") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |
        |func myFunc {
        |  myFunc2()
        |}
        |
        |func myFunc2: String {
        |"This is the answer"
        |}
        |
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val domain = BuildAst.build(fakeContext, parser.domainModel())
    val context = BuilderContext(module = Module("test", fakeManifest, Map(), None, ""), resource = Resource("", "TLang", "Runner", "RunnerTest", domain))
    val resContext = ResolverContext(module = Module("test", fakeManifest, Map(), None, ""), resource = Resource("", "TLang", "Runner", "RunnerTest", domain))
    val callables = BuildLinkTree.buildLinkTree(resContext).toOption.get
    val pathContext = PathContext(resContext.resource, callables, resContext.getFullPkg, resContext.resource.name, "")
    BuildCallObjectLink.buildCallObjectLink(pathContext)
    context.callables ++= callables
    BuildProgram.buildProgram(context)
    val logger = new TestLogger
    val parameter = Parameter(0, 0, logger)
    new Runner().run(context.program, parameter)
    val logs = logger.getLogs
    assert(logs(15) == "[RefFuncSet] Section n°: 0, Instruction n°: 15")
    assert(logs(20) == "Jumping back to: 0:6")
    assert(logs(21) == "[RefFuncGet] Section n°: 0, Instruction n°: 6")
  }

  test("test call Terminal") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """
        |use IO.Terminal
        |
        |helper {
        |
        |func myFunc {
        |  Terminal.println("This is a test! If it's working, you should see this in terminal")
        |}
        |
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val domain = BuildAst.build(fakeContext, parser.domainModel())
    val context = BuilderContext(module = Module("test", fakeManifest, Map(), None, ""), resource = Resource("", "TLang", "Runner", "RunnerTest", domain))
    val resContext = ResolverContext(module = Module("test", fakeManifest, Map(), None, ""), resource = Resource("", "TLang", "Runner", "RunnerTest", domain))
    val callables = BuildLinkTree.buildLinkTree(resContext).toOption.get
    val pathContext = PathContext(resContext.resource, callables, resContext.getFullPkg, resContext.resource.name, "")
    BuildCallObjectLink.buildCallObjectLink(pathContext)
    context.callables ++= callables
    BuildProgram.buildProgram(context)
    val logger = new TestLogger
    val parameter = Parameter(0, 0, logger)
    new Runner().run(context.program, parameter)
    val logs = logger.getLogs
    assert(logs(8) == "[CallJVM] Section n°: 0, Instruction n°: 6")
  }

  test("test call attr in entity") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """
        |use IO.Terminal
        |
        |helper {
        |func myFunc {
        |  Terminal.println(myEntity.attr1)
        |}
        |
        |}
        |
        |model {
        |
        |let myEntity = {
        |  attr1 = "If you see this in terminal, it's working"
        |}
        |
        |}
        |
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val domain = BuildAst.build(fakeContext, parser.domainModel())
    val context = BuilderContext(module = Module("test", fakeManifest, Map(), None, ""), resource = Resource("", "TLang", "Runner", "RunnerTest", domain))
    val resContext = ResolverContext(module = Module("test", fakeManifest, Map(), None, ""), resource = Resource("", "TLang", "Runner", "RunnerTest", domain))
    val callables = BuildLinkTree.buildLinkTree(resContext).toOption.get
    val pathContext = PathContext(resContext.resource, callables, resContext.getFullPkg, resContext.resource.name, "")
    BuildCallObjectLink.buildCallObjectLink(pathContext)
    context.callables ++= callables
    BuildProgram.buildProgram(context)
    val logger = new TestLogger
    val parameter = Parameter(0, 2, logger)
    new Runner().run(context.program, parameter)
    val logs = logger.getLogs
    //assert(logs(9) == "[Label] New label: tlang.tmpl.lang.LangBlock.name")
    //assert(logs(logs.length-2) == "End of label:tlang.tmpl.lang.LangBlock")
  }


}
