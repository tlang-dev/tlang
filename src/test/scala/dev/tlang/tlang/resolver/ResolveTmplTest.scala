package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, TLangLong, TLangString}
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils}
import dev.tlang.tlang.loader.remote.RemoteLoader
import dev.tlang.tlang.loader.{BuildModuleTree, FileResourceLoader, ResourceLoader, TBagManager}
import dev.tlang.tlang.tmpl.lang.ast.LangBlock
import org.scalatest.funsuite.AnyFunSuite
import tlang.core
import tlang.internal.ContextResource

import java.nio.file.Paths

class ResolveTmplTest extends AnyFunSuite {

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

  test("Resolve pkg") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use MyPackage.MyFile
            |tmpl[scala] myTmpl{
            | pkg ${MyFile.myPkg}.${MyFile.myPkg2}
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose myPkg
            |expose myPkg2
            |model {
            |let myPkg :String = "myPackage"
            |let myPkg2 :String = "myPackage2"
            |
            |}""".stripMargin)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
    val block = module.resources("Main").ast.body.head.asInstanceOf[LangBlock]
    val resource = module.resources("Main")
    ResolveTmpl.resolveTmpl(block, module, resource.ast.header.get.uses.get, resource)
    assert("myPackage" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/myPkg").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("myPackage2" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/myPkg2").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
  }

  test("Resolve uses") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use MyPackage.MyFile
            |tmpl[scala] myTmpl{
            | use ${MyFile.use1}.${MyFile.use2}
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose use1
            |expose use2
            |model {
            |let use1 :String = "Use1"
            |let use2 :String = "Use2"
            |
            |}""".stripMargin)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
    val block = module.resources("Main").ast.body.head.asInstanceOf[LangBlock]
    val resource = module.resources("Main")
    ResolveTmpl.resolveTmpl(block, module, resource.ast.header.get.uses.get, resource)
    assert("Use1" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/use1").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("Use2" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/use2").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
  }

  test("Resolve annots") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use MyPackage.MyFile
            |tmpl[scala] myTmpl{
            | @${MyFile.annot}(${MyFile.param} = "Value")
            | func myFunc() {
            | }
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose annot
            |expose param
            |model {
            |let annot :String = "Annot"
            |let param :String = "Param"
            |}""".stripMargin)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
    val block = module.resources("Main").ast.body.head.asInstanceOf[LangBlock]
    val resource = module.resources("Main")
    ResolveTmpl.resolveTmpl(block, module, resource.ast.header.get.uses.get, resource)
    assert("Annot" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/annot").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("Param" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/param").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
  }

  test("Resolve props") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use MyPackage.MyFile
            |tmpl[scala] myTmpl{
            | func[${MyFile.prop1} ${MyFile.prop2}] myFunc() {
            | }
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose prop1
            |expose prop2
            |model {
            |let prop1 :String = "Prop1"
            |let prop2 :String = "Prop2"
            |}""".stripMargin)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
    val block = module.resources("Main").ast.body.head.asInstanceOf[LangBlock]
    val resource = module.resources("Main")
    ResolveTmpl.resolveTmpl(block, module, resource.ast.header.get.uses.get, resource)
    assert("Prop1" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/prop1").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("Prop2" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/prop2").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
  }

  test("Resolve currying") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use MyPackage.MyFile
            |tmpl[scala] myTmpl{
            | func myFunc(${MyFile.param}: ${MyFile.type}<${MyFile.type2}>) {
            | }
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose param
            |expose type
            |expose type2
            |model {
            |let param :String = "Param"
            |let type :String = "Type"
            |let type2 :String = "Type2"
            |}""".stripMargin)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
    val block = module.resources("Main").ast.body.head.asInstanceOf[LangBlock]
    val resource = module.resources("Main")
    ResolveTmpl.resolveTmpl(block, module, resource.ast.header.get.uses.get, resource)
    assert("Param" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/param").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("Type" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/type").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("Type2" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/type2").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
  }

  test("Resolve func content") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use MyPackage.MyFile
            |tmpl[scala] myTmpl{
            | func myFunc() {
            |   var [${MyFile.prop}] ${MyFile.myVar} = "Value"
            | }
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose prop
            |expose myVar
            |model {
            |let prop :String = "Prop"
            |let myVar :String = "MyVar"
            |}""".stripMargin)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
    val block = module.resources("Main").ast.body.head.asInstanceOf[LangBlock]
    val resource = module.resources("Main")
    ResolveTmpl.resolveTmpl(block, module, resource.ast.header.get.uses.get, resource)
    assert("Prop" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/prop").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("MyVar" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/myVar").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
  }

  test("Resolve func inside func") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use MyPackage.MyFile
            |tmpl[scala] myTmpl {
            | func myFunc() {
            |   func MyFunc2() {
            |     var [${MyFile.prop}] ${MyFile.myVar} = "Value"
            |   }
            | }
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose prop
            |expose myVar
            |model {
            |let prop :String = "Prop"
            |let myVar :String = "MyVar"
            |}""".stripMargin)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
    val block = module.resources("Main").ast.body.head.asInstanceOf[LangBlock]
    val resource = module.resources("Main")
    ResolveTmpl.resolveTmpl(block, module, resource.ast.header.get.uses.get, resource)
    assert("Prop" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/prop").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("MyVar" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/myVar").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
  }

  test("Resolve built in func") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use MyPackage.MyFile
            |tmpl[scala] myTmpl{
            | impl MyClass {
            |   <[forEach(MyFile.array, MyFile.&doSomething(_))]>
            | }
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose array
            |expose doSomething
            |model {
            |  let array = ["One", "Two", "Three"]
            |}
            |
            |helper {
            |  func doSomething(elem String) {
            |  }
            |}
            |""".stripMargin)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
    val block = module.resources("Main").ast.body.head.asInstanceOf[LangBlock]
    val resource = module.resources("Main")
    ResolveTmpl.resolveTmpl(block, module, resource.ast.header.get.uses.get, resource)
    assert("One" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/array").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[ArrayValue].tbl.get.head.value.content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("Two" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/array").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[ArrayValue].tbl.get(1).value.content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("Three" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/array").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[ArrayValue].tbl.get.last.value.content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("doSomething" == ContextUtils.findFunc(Context(List(block.scope)), "MyFile/doSomething").get.name)
  }

  test("Resolve while") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use MyPackage.MyFile
            |tmpl[scala] myTmpl{
            | while (${MyFile.cond1} == ${MyFile.cond2}) {
            |   var [${MyFile.prop}] ${MyFile.myVar} = "Value"
            | }
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose cond1
            |expose cond2
            |expose prop
            |expose myVar
            |model {
            |let cond1 :Long = 1
            |let cond2 :Long = 2
            |let prop :String = "Prop"
            |let myVar :String = "MyVar"
            |}""".stripMargin)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
    val block = module.resources("Main").ast.body.head.asInstanceOf[LangBlock]
    val resource = module.resources("Main")
    ResolveTmpl.resolveTmpl(block, module, resource.ast.header.get.uses.get, resource)
    assert(1 == ContextUtils.findVar(Context(List(block.scope)), "MyFile/cond1").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangLong].getElement)
    assert(2 == ContextUtils.findVar(Context(List(block.scope)), "MyFile/cond2").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangLong].getElement)
    assert("Prop" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/prop").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("MyVar" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/myVar").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
  }

  test("Resolve do while") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use MyPackage.MyFile
            |tmpl[scala] myTmpl{
            | do {
            |   var [${MyFile.prop}] ${MyFile.myVar} = "Value"
            | } while (${MyFile.cond1} == ${MyFile.cond2})
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose cond1
            |expose cond2
            |expose prop
            |expose myVar
            |model {
            |let cond1 :Long = 1
            |let cond2 :Long = 2
            |let prop :String = "Prop"
            |let myVar :String = "MyVar"
            |}""".stripMargin)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
    val block = module.resources("Main").ast.body.head.asInstanceOf[LangBlock]
    val resource = module.resources("Main")
    ResolveTmpl.resolveTmpl(block, module, resource.ast.header.get.uses.get, resource)
    assert(1 == ContextUtils.findVar(Context(List(block.scope)), "MyFile/cond1").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangLong].getElement)
    assert(2 == ContextUtils.findVar(Context(List(block.scope)), "MyFile/cond2").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangLong].getElement)
    assert("Prop" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/prop").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("MyVar" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/myVar").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
  }

  test("Resolve if else") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use MyPackage.MyFile
            |tmpl[scala] myTmpl{
            | if (${MyFile.cond1} == ${MyFile.cond2}) {
            |   var [${MyFile.prop}] ${MyFile.myVar} = "Value"
            | } else {
            |   var [${MyFile.prop2}] ${MyFile.myVar2} = "Value2"
            | }
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose cond1
            |expose cond2
            |expose prop
            |expose myVar
            |expose prop2
            |expose myVar2
            |model {
            |let cond1 :Long = 1
            |let cond2 :Long = 2
            |let prop :String = "Prop"
            |let myVar :String = "MyVar"
            |let prop2 :String = "Prop2"
            |let myVar2 :String = "MyVar2"
            |}""".stripMargin)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
    val block = module.resources("Main").ast.body.head.asInstanceOf[LangBlock]
    val resource = module.resources("Main")
    ResolveTmpl.resolveTmpl(block, module, resource.ast.header.get.uses.get, resource)
    assert(1 == ContextUtils.findVar(Context(List(block.scope)), "MyFile/cond1").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangLong].getElement)
    assert(2 == ContextUtils.findVar(Context(List(block.scope)), "MyFile/cond2").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangLong].getElement)
    assert("Prop" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/prop").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("MyVar" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/myVar").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("Prop2" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/prop2").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("MyVar2" == ContextUtils.findVar(Context(List(block.scope)), "MyFile/myVar2").get.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
  }

}
