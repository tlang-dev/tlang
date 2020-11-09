package io.sorne.tlang.resolver

import java.nio.file.Paths

import io.sorne.tlang.ast.helper.{HelperCallFuncObject, HelperCallVarObject, HelperContent, HelperFunc}
import io.sorne.tlang.ast.model.ModelContent
import io.sorne.tlang.ast.model.`new`.{ModelNewEntity, ModelNewEntityValue}
import io.sorne.tlang.ast.model.set.ModelSetEntity
import io.sorne.tlang.astbuilder.BuildAst
import io.sorne.tlang.interpreter.context.Scope
import io.sorne.tlang.loader.{BuildModuleTree, Resource, ResourceLoader}
import io.sorne.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class ResolveContextTest extends AnyFunSuite {

  test("") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main") {
        Right(
          """
            |use MyPackage.MyFile
            |model {
            |}""".stripMargin)
      } else {
        Right(
          """
            |model {
            |}""".stripMargin)
      }
    }

    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    ResolveContext.resolveContext(module)
  }

  test("Resolve call object"){

  }

  test("Follow func call") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """
        |expose myFunc
        |helper {
        |func myFunc {}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val block = BuildAst.build(parser.domainModel())
    val resource = Resource("", "", "", "Main", block)
    val calls = List(HelperCallVarObject("first"),HelperCallVarObject("second"), HelperCallFuncObject(Some("myFunc"), None))
    val scope = Scope()

    ResolveContext.followCall(resource, calls, 2, List("first", "second"), scope)
    assert("first/second/myFunc" == scope.functions.head._1)
    assert("myFunc" == scope.functions.head._2.name)
  }

  test("Follow var call") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """
        |expose myEntity
        |model {
        |let myEntity AnyEntity {}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val block = BuildAst.build(parser.domainModel())
    val resource = Resource("", "", "", "Main", block)
    val calls = List(HelperCallVarObject("first"),HelperCallVarObject("second"), HelperCallVarObject("myEntity"))
    val scope = Scope()

    ResolveContext.followCall(resource, calls, 2, List("first", "second"), scope)
    assert("first/second/myEntity" == scope.variables.head._1)
    assert("AnyEntity" == scope.variables.head._2.getType)
  }

  test("Find func in resource") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        |func myFunc {}
        |func MyFunc2 {}
        |func MyFunc3 {}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val block = BuildAst.build(parser.domainModel())
    val resource = Resource("", "", "", "Main", block)

    val func = ResolveContext.findInResource(resource, HelperCallFuncObject(Some("MyFunc2"), None)).toOption.get.get.asInstanceOf[HelperFunc]
    assert("MyFunc2" == func.name)
  }

  test("Find var in resource") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |let myEntity AnyEntity {}
        |let myEntity2 AnyEntity2 {}
        |let myEntity3 AnyEntity3 {}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val block = BuildAst.build(parser.domainModel())
    val resource = Resource("", "", "", "Main", block)

    val entityType = ResolveContext.findInResource(resource, HelperCallVarObject("myEntity2")).toOption.get.get.asInstanceOf[ModelNewEntityValue].`type`.get
    assert("AnyEntity2" == entityType)
  }

  test("Find func") {
    val funcs = List(
      HelperFunc("func1", block = HelperContent(None)),
      HelperFunc("func2", block = HelperContent(None)),
      HelperFunc("func3", block = HelperContent(None)),
    )

    val func = ResolveContext.findInFuncs(funcs, "func2")
    assert("func2" == func.get.name)
  }

  test("Find var") {
    val contents: List[ModelContent] = List(
      ModelSetEntity("myEntity", None, None),
      ModelNewEntity("myEntity", ModelNewEntityValue(None, None, None)),
      ModelNewEntity("myEntity2", ModelNewEntityValue(None, None, None)),
    )

    val myVar = ResolveContext.findInVars(contents, "myEntity")
    assert("myEntity" == myVar.get.name)
  }
  test("Find empty func") {
    val funcs = List(
      HelperFunc("func1", block = HelperContent(None)),
      HelperFunc("func2", block = HelperContent(None)),
      HelperFunc("func3", block = HelperContent(None)),
    )

    val func = ResolveContext.findInFuncs(funcs, "func4")
    assert(func.isEmpty)
  }

  test("Find empty var") {
    val contents: List[ModelContent] = List(
      ModelSetEntity("myEntity", None, None),
      ModelNewEntity("myEntity", ModelNewEntityValue(None, None, None)),
      ModelNewEntity("myEntity2", ModelNewEntityValue(None, None, None)),
    )

    val myVar = ResolveContext.findInVars(contents, "myEntity3")
    assert(myVar.isEmpty)
  }

}
