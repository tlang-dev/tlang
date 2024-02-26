package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainUse
import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{AssignVar, EntityValue}
import dev.tlang.tlang.ast.helper.{HelperBlock, HelperContent, HelperFunc}
import dev.tlang.tlang.ast.model.ModelContent
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.BuildAst
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.loader._
import dev.tlang.tlang.loader.remote.RemoteLoader
import dev.tlang.tlang.tmpl.lang.ast.LangBlock
import dev.tlang.tlang.{CommonLexer, TLang}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite
import tlang.core
import tlang.core.Null
import tlang.internal.ContextResource

import java.nio.file.Paths

class ResolveContextTest extends AnyFunSuite {

  val fakeContext: ContextResource = new ContextResource(new core.String(""), new core.String(""), new core.String(""), new core.String(""))


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

    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
    ResolveContext.resolveContext(module)

    val scope = module.resources(module.mainFile).ast.body.head.asInstanceOf[HelperBlock].funcs.get.head.scope
    assert("MyFile/myEntity" == scope.variables.head._1)
    assert("MyEntity" == scope.variables.head._2.asInstanceOf[Operation].content.toOption.get.asInstanceOf[EntityValue].`type`.get.getType.getType.toString)
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
            |tmpl[scala] myTmpl lang {
            |
            |}""".stripMargin)
      }
    }

    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
    ResolveContext.resolveContext(module)

    val scope = module.resources(module.mainFile).ast.body.head.asInstanceOf[HelperBlock].funcs.get.head.scope
    assert("MyFile/myTmpl" == scope.templates.head._1)
    assert("myTmpl" == scope.templates.head._2.asInstanceOf[LangBlock].name)
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
    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
    val uses = List(DomainUse(Null.empty(), List("MyPackage", "MyFile")))
    val caller = CallObject(Null.empty(), List(CallVarObject(Null.empty(), "MyFile"), CallVarObject(Null.empty(), "myEntity")))
    val scope = Scope()
    val func = HelperFunc(Null.empty(), "aFunc", None, None, HelperContent(Null.empty(), Some(List(caller))), scope)
    BrowseFunc.resolveFuncs(List(func), module, uses, module.resources.head._2)
    assert("MyFile/myEntity" == scope.variables.head._1)
    assert("MyEntity" == scope.variables.head._2.asInstanceOf[Operation].content.toOption.get)
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
    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
    val uses = List(DomainUse(Null.empty(), List("MyPackage", "MyFile")))
    val caller = CallObject(Null.empty(), List(CallVarObject(Null.empty(), "MyFile"), CallVarObject(Null.empty(), "myEntity")))
    val scope = Scope()
    BrowseHelperStatement.browseStatements(Some(List(caller)), module, uses, scope, module.resources.head._2)
    assert("MyFile/myEntity" == scope.variables.head._1)
    assert("MyEntity" == scope.variables.head._2.asInstanceOf[Operation].content.toOption.get)
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
    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
    val uses = List(DomainUse(Null.empty(), List("MyPackage", "MyFile")))
    val caller = CallObject(Null.empty(), List(CallVarObject(Null.empty(), "MyFile"), CallVarObject(Null.empty(), "myEntity")))
    val scope = Scope()
    FollowCallObject.followCallObject(caller, module, uses, scope, module.resources.head._2).toOption.get
    assert("MyFile/myEntity" == scope.variables.head._1)
    assert("MyEntity" == scope.variables.head._2.asInstanceOf[Operation].content.toOption.get)
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
    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
    val uses = List(DomainUse(Null.empty(), List("MyFile")))
    val caller = CallObject(Null.empty(), List(CallVarObject(Null.empty(), "MyFile"), CallVarObject(Null.empty(), "myFunc")))
    val scope = Scope()
    FollowCallObject.followCallObject(caller, module, uses, scope, module.resources.head._2).toOption.get
    assert("MyFile/myFunc" == scope.functions.head._1)
    assert("myFunc" == scope.functions.head._2.getValue.asInstanceOf[HelperFunc].name)
  }

  test("Follow func call") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """
        |expose myFunc
        |helper {
        |func myFunc {}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val block = BuildAst.build(fakeContext, parser.domainModel())
    val resource = Resource("", "", "", "Main", block)
    val calls = List(CallVarObject(Null.empty(), "first"), CallVarObject(Null.empty(), "second"), CallFuncObject(Null.empty(), Some("myFunc"), None))
    val scope = Scope()

    ResolveContext.followCall(resource, sameResource = false, calls, 2, List("first", "second"), scope)
    assert("first/second/myFunc" == scope.functions.head._1)
    assert("myFunc" == scope.functions.head._2.name)
  }

  test("Follow var call") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """
        |expose myEntity
        |model {
        |let myEntity :AnyEntity ={}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val block = BuildAst.build(fakeContext, parser.domainModel())
    val resource = Resource("", "", "", "Main", block)
    val calls = List(CallVarObject(Null.empty(), "first"), CallVarObject(Null.empty(), "second"), CallVarObject(Null.empty(), "myEntity"))
    val scope = Scope()

    ResolveContext.followCall(resource, sameResource = false, calls, 2, List("first", "second"), scope)
    assert("first/second/myEntity" == scope.variables.head._1)
    assert("AnyEntity" == scope.variables.head._2.asInstanceOf[Operation].content.toOption.get)
  }

  test("Find func in resource") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc {}
        |func MyFunc2 {}
        |func MyFunc3 {}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val block = BuildAst.build(fakeContext, parser.domainModel())
    val resource = Resource("", "", "", "Main", block)

    val func = ResolveContext.findInResource(resource, CallFuncObject(Null.empty(), Some("MyFunc2"), None)).toOption.get.get.asInstanceOf[HelperFunc]
    assert("MyFunc2" == func.name)
  }

  test("Find var in resource") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """model {
        |let myEntity :AnyEntity = {}
        |let myEntity2 :AnyEntity2 = {}
        |let myEntity3 :AnyEntity3 = {}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val block = BuildAst.build(fakeContext, parser.domainModel())
    val resource = Resource("", "", "", "Main", block)

    val entityType = ResolveContext.findInResource(resource, CallVarObject(Null.empty(), "myEntity2")).toOption.get.get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[EntityValue].`type`.get
    assert("AnyEntity2" == entityType.getType)
  }

  test("Find func") {
    val funcs = List(
      HelperFunc(Null.empty(), "func1", block = HelperContent(Null.empty(), None)),
      HelperFunc(Null.empty(), "func2", block = HelperContent(Null.empty(), None)),
      HelperFunc(Null.empty(), "func3", block = HelperContent(Null.empty(), None)),
    )

    val func = ResolveUtils.findInFuncs(funcs, "func2")
    assert("func2" == func.get.name)
  }

  test("Find var") {
    val contents: List[ModelContent[_]] = List(
      ModelSetEntity(Null.empty(), ManualType("", "myEntity"), None, None, None),
      AssignVar(Null.empty(), "myEntity", None, Operation(Null.empty(), None, Right(EntityValue(Null.empty(), None, None)))),
      AssignVar(Null.empty(), "myEntity2", None, Operation(Null.empty(), None, Right(EntityValue(Null.empty(), None, None)))),
    )

    val myVar = ResolveUtils.findInVars(contents, "myEntity")
    assert("myEntity" == myVar.get.name)
  }

  test("Find empty func") {
    val funcs = List(
      HelperFunc(Null.empty(), "func1", block = HelperContent(Null.empty(), None)),
      HelperFunc(Null.empty(), "func2", block = HelperContent(Null.empty(), None)),
      HelperFunc(Null.empty(), "func3", block = HelperContent(Null.empty(), None)),
    )

    val func = ResolveUtils.findInFuncs(funcs, "func4")
    assert(func.isEmpty)
  }

  test("Find empty var") {
    val contents: List[ModelContent[_]] = List(
      ModelSetEntity(Null.empty(), ManualType("", "myEntity"), None, None, None),
      AssignVar(Null.empty(), "myEntity", None, Operation(Null.empty(), None, Right(EntityValue(Null.empty(), None, None)))),
      AssignVar(Null.empty(), "myEntity2", None, Operation(Null.empty(), None, Right(EntityValue(Null.empty(), None, None)))),
    )

    val myVar = ResolveUtils.findInVars(contents, "myEntity3")
    assert(myVar.isEmpty)
  }

}
