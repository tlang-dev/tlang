package io.sorne.tlang.resolver

import java.nio.file.Paths

import io.sorne.tlang.ast.DomainUse
import io.sorne.tlang.ast.common.call.{CallFuncObject, CallObject, CallVarObject}
import io.sorne.tlang.ast.common.value.EntityValue
import io.sorne.tlang.ast.helper.{HelperBlock, HelperContent, HelperFunc}
import io.sorne.tlang.ast.model.ModelContent
import io.sorne.tlang.ast.model.set.ModelSetEntity
import io.sorne.tlang.astbuilder.BuildAst
import io.sorne.tlang.interpreter.context.Scope
import io.sorne.tlang.loader.{BuildModuleTree, Resource, ResourceLoader}
import io.sorne.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class ResolveContextTest extends AnyFunSuite {

  val defaultManifest: String =
    """name: MyProgram
      |project: MyProject
      |organisation: MyOrganisation
      |version: 1.33.7
      |stability: final
      |releaseNumber: 2
      |""".stripMargin

  test("Resolve context with entity") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main") {
        Right(
          """
            |use MyPackage.MyFile
            |helper {
            |func myFunc() {
            |MyFile.myEntity
            |}
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose myEntity
            |model {
            |let myEntity MyEntity{}
            |
            |}""".stripMargin)
      }
    }

    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    ResolveContext.resolveContext(module)

    val scope = module.resources(module.mainFile).ast.body.head.asInstanceOf[HelperBlock].funcs.get.head.scope
    assert("MyFile/myEntity" == scope.variables.head._1)
    assert("MyEntity" == scope.variables.head._2.getType)
  }

  test("Resolve context with template") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main") {
        Right(
          """
            |use MyPackage.MyFile
            |helper {
            |func myFunc() {
            |MyFile.myTmpl
            |}
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose myTmpl
            |tmpl[scala] myTmpl {
            |
            |}""".stripMargin)
      }
    }

    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    ResolveContext.resolveContext(module)

    val scope = module.resources(module.mainFile).ast.body.head.asInstanceOf[HelperBlock].funcs.get.head.scope
    assert("MyFile/myTmpl" == scope.templates.head._1)
    assert("myTmpl" == scope.templates.head._2.name)
  }

  test("Resolve funcs") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main") {
        Right(
          """
            |use MyPackage.MyFile
            |helper {
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose myEntity
            |model {
            |let myEntity MyEntity{}
            |
            |}""".stripMargin)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    val uses = List(DomainUse(List("MyPackage", "MyFile")))
    val caller = CallObject(List(CallVarObject("MyFile"), CallVarObject("myEntity")))
    val scope = Scope()
    val func = HelperFunc("aFunc", None, None, HelperContent(Some(List(caller))), scope)
    ResolveContext.resolveFuncs(List(func), module, uses)
    assert("MyFile/myEntity" == scope.variables.head._1)
    assert("MyEntity" == scope.variables.head._2.getType)
  }

  test("Resolve statement") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main") {
        Right(
          """
            |use MyPackage.MyFile
            |helper {
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose myEntity
            |model {
            |let myEntity MyEntity{}
            |
            |}""".stripMargin)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    val uses = List(DomainUse(List("MyPackage", "MyFile")))
    val caller = CallObject(List(CallVarObject("MyFile"), CallVarObject("myEntity")))
    val scope = Scope()
    ResolveContext.resolveStatements(Some(List(caller)), module, uses, scope)
    assert("MyFile/myEntity" == scope.variables.head._1)
    assert("MyEntity" == scope.variables.head._2.getType)
  }

  test("Resolve call object for entity in another package") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main") {
        Right(
          """
            |use MyPackage.MyFile
            |helper {
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose myEntity
            |model {
            |let myEntity MyEntity{}
            |
            |}""".stripMargin)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    val uses = List(DomainUse(List("MyPackage", "MyFile")))
    val caller = CallObject(List(CallVarObject("MyFile"), CallVarObject("myEntity")))
    val scope = Scope()
    ResolveContext.resolveCallObject(caller, module, uses, scope).toOption.get
    assert("MyFile/myEntity" == scope.variables.head._1)
    assert("MyEntity" == scope.variables.head._2.getType)
  }

  test("Resolve call object for func in same package") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main") {
        Right(
          """
            |use MyFile
            |model {
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose myFunc
            |helper {
            |func myFunc() {
            |MyFile.myEntity
            |}
            |}""".stripMargin)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    val uses = List(DomainUse(List("MyFile")))
    val caller = CallObject(List(CallVarObject("MyFile"), CallVarObject("myFunc")))
    val scope = Scope()
    ResolveContext.resolveCallObject(caller, module, uses, scope).toOption.get
    assert("MyFile/myFunc" == scope.functions.head._1)
    assert("myFunc" == scope.functions.head._2.getValue.name)
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
    val calls = List(CallVarObject("first"), CallVarObject("second"), CallFuncObject(Some("myFunc"), None))
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
    val calls = List(CallVarObject("first"), CallVarObject("second"), CallVarObject("myEntity"))
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

    val func = ResolveContext.findInResource(resource, CallFuncObject(Some("MyFunc2"), None)).toOption.get.get.asInstanceOf[HelperFunc]
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

    val entityType = ResolveContext.findInResource(resource, CallVarObject("myEntity2")).toOption.get.get.asInstanceOf[EntityValue].`type`.get
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
      ModelNewEntity("myEntity", EntityValue(None, None, None)),
      ModelNewEntity("myEntity2", EntityValue(None, None, None)),
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
      ModelNewEntity("myEntity", EntityValue(None, None, None)),
      ModelNewEntity("myEntity2", EntityValue(None, None, None)),
    )

    val myVar = ResolveContext.findInVars(contents, "myEntity3")
    assert(myVar.isEmpty)
  }

}
