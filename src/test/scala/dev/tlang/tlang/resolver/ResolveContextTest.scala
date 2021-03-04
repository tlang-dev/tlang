package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainUse
import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.value.{AssignVar, EntityValue}
import dev.tlang.tlang.ast.helper.{HelperBlock, HelperContent, HelperFunc}
import dev.tlang.tlang.ast.model.ModelContent
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.BuildAst
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.loader.remote.RemoteLoader
import dev.tlang.tlang.loader._
import dev.tlang.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

import java.nio.file.Paths

class ResolveContextTest extends AnyFunSuite {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

  implicit val loader: FileResourceLoader.type = FileResourceLoader
  implicit val remoteLoader: RemoteLoader.type = RemoteLoader
  implicit val tBagManager: TBagManager.type = TBagManager

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
      if (name == "Main.tlang") {
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
            |let myEntity :MyEntity = {}
            |
            |}""".stripMargin)
      }
    }

    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    ResolveContext.resolveContext(module)

    val scope = module.resources(module.mainFile).ast.body.head.asInstanceOf[HelperBlock].funcs.get.head.scope
    assert("MyFile/myEntity" == scope.variables.head._1)
    assert("MyEntity" == scope.variables.head._2.asInstanceOf[EntityValue].`type`.get)
  }

  test("Resolve context with template") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
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
      if (name == "Main.tlang") {
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
            |let myEntity :MyEntity = {}
            |
            |}""".stripMargin)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    val uses = List(DomainUse(None, List("MyPackage", "MyFile")))
    val caller = CallObject(None, List(CallVarObject(None, "MyFile"), CallVarObject(None, "myEntity")))
    val scope = Scope()
    val func = HelperFunc(None, "aFunc", None, None, HelperContent(None, Some(List(caller))), scope)
    BrowseFunc.resolveFuncs(List(func), module, uses, module.resources.head._2)
    assert("MyFile/myEntity" == scope.variables.head._1)
    assert("MyEntity" == scope.variables.head._2.getType)
  }

  test("Resolve statement") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
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
            |let myEntity :MyEntity = {}
            |
            |}""".stripMargin)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    val uses = List(DomainUse(None, List("MyPackage", "MyFile")))
    val caller = CallObject(None, List(CallVarObject(None, "MyFile"), CallVarObject(None, "myEntity")))
    val scope = Scope()
    BrowseHelperStatement.browseStatements(Some(List(caller)), module, uses, scope, module.resources.head._2)
    assert("MyFile/myEntity" == scope.variables.head._1)
    assert("MyEntity" == scope.variables.head._2.getType)
  }

  test("Resolve call object for entity in another package") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
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
            |let myEntity :MyEntity = {}
            |
            |}""".stripMargin)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    val uses = List(DomainUse(None, List("MyPackage", "MyFile")))
    val caller = CallObject(None, List(CallVarObject(None, "MyFile"), CallVarObject(None, "myEntity")))
    val scope = Scope()
    FollowCallObject.followCallObject(caller, module, uses, scope, module.resources.head._2).toOption.get
    assert("MyFile/myEntity" == scope.variables.head._1)
    assert("MyEntity" == scope.variables.head._2.getType)
  }

  test("Resolve call object for func in same package") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
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
    val uses = List(DomainUse(None, List("MyFile")))
    val caller = CallObject(None, List(CallVarObject(None, "MyFile"), CallVarObject(None, "myFunc")))
    val scope = Scope()
    FollowCallObject.followCallObject(caller, module, uses, scope, module.resources.head._2).toOption.get
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
    val block = BuildAst.build(fakeContext, parser.domainModel())
    val resource = Resource("", "", "", "Main", block)
    val calls = List(CallVarObject(None, "first"), CallVarObject(None, "second"), CallFuncObject(None, Some("myFunc"), None))
    val scope = Scope()

    ResolveContext.followCall(resource, sameResource = false, calls, 2, List("first", "second"), scope)
    assert("first/second/myFunc" == scope.functions.head._1)
    assert("myFunc" == scope.functions.head._2.name)
  }

  test("Follow var call") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """
        |expose myEntity
        |model {
        |let myEntity :AnyEntity ={}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val block = BuildAst.build(fakeContext, parser.domainModel())
    val resource = Resource("", "", "", "Main", block)
    val calls = List(CallVarObject(None, "first"), CallVarObject(None, "second"), CallVarObject(None, "myEntity"))
    val scope = Scope()

    ResolveContext.followCall(resource, sameResource = false, calls, 2, List("first", "second"), scope)
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
    val block = BuildAst.build(fakeContext, parser.domainModel())
    val resource = Resource("", "", "", "Main", block)

    val func = ResolveContext.findInResource(resource, CallFuncObject(None, Some("MyFunc2"), None)).toOption.get.get.asInstanceOf[HelperFunc]
    assert("MyFunc2" == func.name)
  }

  test("Find var in resource") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |let myEntity :AnyEntity = {}
        |let myEntity2 :AnyEntity2 = {}
        |let myEntity3 :AnyEntity3 = {}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val block = BuildAst.build(fakeContext, parser.domainModel())
    val resource = Resource("", "", "", "Main", block)

    val entityType = ResolveContext.findInResource(resource, CallVarObject(None,"myEntity2")).toOption.get.get.asInstanceOf[EntityValue].`type`.get
    assert("AnyEntity2" == entityType)
  }

  test("Find func") {
    val funcs = List(
      HelperFunc(None, "func1", block = HelperContent(None, None)),
      HelperFunc(None, "func2", block = HelperContent(None, None)),
      HelperFunc(None, "func3", block = HelperContent(None, None)),
    )

    val func = ResolveUtils.findInFuncs(funcs, "func2")
    assert("func2" == func.get.name)
  }

  test("Find var") {
    val contents: List[ModelContent] = List(
      ModelSetEntity(None, "myEntity", None, None),
      AssignVar(None, "myEntity", None, EntityValue(None, None, None)),
      AssignVar(None, "myEntity2", None, EntityValue(None, None, None)),
    )

    val myVar = ResolveUtils.findInVars(contents, "myEntity")
    assert("myEntity" == myVar.get.name)
  }

  test("Find empty func") {
    val funcs = List(
      HelperFunc(None, "func1", block = HelperContent(None, None)),
      HelperFunc(None, "func2", block = HelperContent(None, None)),
      HelperFunc(None, "func3", block = HelperContent(None, None)),
    )

    val func = ResolveUtils.findInFuncs(funcs, "func4")
    assert(func.isEmpty)
  }

  test("Find empty var") {
    val contents: List[ModelContent] = List(
      ModelSetEntity(None, "myEntity", None, None),
      AssignVar(None, "myEntity", None, EntityValue(None, None, None)),
      AssignVar(None, "myEntity2", None, EntityValue(None, None, None)),
    )

    val myVar = ResolveUtils.findInVars(contents, "myEntity3")
    assert(myVar.isEmpty)
  }

}
