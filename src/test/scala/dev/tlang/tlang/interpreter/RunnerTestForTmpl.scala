package dev.tlang.tlang.interpreter

import dev.tlang.tlang.{CommonLexer, TLang}
import dev.tlang.tlang.astbuilder.BuildAst
import dev.tlang.tlang.interpreter.context.State
import dev.tlang.tlang.interpreter.recipe.{BuildProgram, BuilderContext, Parameter}
import dev.tlang.tlang.loader.{Module, Resource, manifest}
import dev.tlang.tlang.resolver.{BuildCallObjectLink, BuildLinkTree, PathContext, ResolverContext}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuiteLike
import tlang.core
import tlang.core.func.FuncRet
import tlang.internal.ContextResource

class RunnerTestForTmpl extends AnyFunSuiteLike {

  val fakeContext: ContextResource = new ContextResource(new core.String(""), new core.String(""), new core.String("TLang/Runner"), new core.String("RunnerTest"))

  val fakeManifest: manifest.Manifest = manifest.Manifest("RunnerTest", "Runner", "TLang", "0.0.0", None, 0, None, None)



  test("test call lang tmpl") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """
        |helper {
        |
        |func myFunc {
        | myTmpl().content.tPkg
        |}
        |
        |}
        |
        |lang [Kotlin] myTmpl() {
        | use test.test2
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
    val ret = new Runner().initAndRun(State(program = context.program), parameter)
    assert("If you see this in terminal, echo from JVM works!" == ret.toOption.get.get.asInstanceOf[FuncRet].get().getValue.toString)
  }

}
