package dev.tlang.tlang.resolver

import dev.tlang.tlang.astbuilder.BuildAst
import dev.tlang.tlang.interpreter.value.{InterEntity, InterFunction, InterModel, InterResource}
import dev.tlang.tlang.loader.{Module, Resource, manifest}
import dev.tlang.tlang.{CommonLexer, TLang}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuiteLike
import tlang.core
import tlang.internal.ContextResource

class BuildLinkTreeTest extends AnyFunSuiteLike {

  val fakeContext: ContextResource = new ContextResource(new core.String(""), new core.String(""), new core.String("TLang.Runner"), new core.String("RunnerTest"))

  val fakeManifest: manifest.Manifest = manifest.Manifest("RunnerTest", "Runner", "TLang", "0.0.0", None, 0, None, None)

  test("Test create links for resource") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |
        |func myFunc2 {
        |}
        |
        |func myFunc {
        |
        |}
        |
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val domain = BuildAst.build(fakeContext, parser.domainModel())
    val context = ResolverContext(module = Module("test", fakeManifest, Map(), None, ""), resource = Resource("RootDir", "FromRoot", "Pkg", "ResName", domain))
    val tree = BuildLinkTree.buildLinkTree(context).toOption.get
    assert(tree("FromRoot/Pkg/ResName").isInstanceOf[InterResource])
    assert(tree("FromRoot/Pkg/ResName/myFunc").isInstanceOf[InterFunction])
    assert(tree("FromRoot/Pkg/ResName/myFunc2").isInstanceOf[InterFunction])
  }


  test("Test create links for models") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """model {
        |
        |let myEntity = {
        |attr1="This is my attr"
        |attr2=5
        |}
        |
        |set myModel {
        |attr1: String,
        |attr2: Long
        |}
        |
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val domain = BuildAst.build(fakeContext, parser.domainModel())
    val context = ResolverContext(module = Module("test", fakeManifest, Map(), None, ""), resource = Resource("RootDir", "FromRoot", "Pkg", "ResName", domain))
    val tree = BuildLinkTree.buildLinkTree(context).toOption.get
    assert(tree("FromRoot/Pkg/ResName").isInstanceOf[InterResource])
    assert(tree("FromRoot/Pkg/ResName/myEntity").isInstanceOf[InterEntity])
    assert(tree("FromRoot/Pkg/ResName/myModel").isInstanceOf[InterModel])
  }

}
