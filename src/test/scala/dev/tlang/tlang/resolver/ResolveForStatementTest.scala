package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, TLangLong, TLangString}
import dev.tlang.tlang.ast.helper.HelperBlock
import dev.tlang.tlang.loader.remote.RemoteLoader
import dev.tlang.tlang.loader.{BuildModuleTree, FileResourceLoader, ResourceLoader, TBagManager}
import org.scalatest.funsuite.AnyFunSuite

import java.nio.file.Paths

class ResolveForStatementTest extends AnyFunSuite {

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

  test("Resolve start in for") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use MyPackage.MyFile
            |helper {
            |func myFunc() {
            |for(i MyFile.start to 10) {
            |}
            |}
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose start
            |model {
            |let start = 1
            |
            |}""".stripMargin)
      }
    }

    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    ResolveContext.resolveContext(module)

    val scope = module.resources(module.mainFile).ast.body.head.asInstanceOf[HelperBlock].funcs.get.head.scope
    assert("MyFile/start" == scope.variables.head._1)
    assert(1 == scope.variables.head._2.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangLong].getElement)
  }

  test("Resolve array in for") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use MyPackage.MyFile
            |helper {
            |func myFunc() {
            |for(i in MyFile.array) {
            |}
            |}
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose array
            |model {
            |let array = [1,2,3,4,5]
            |
            |}""".stripMargin)
      }
    }

    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    ResolveContext.resolveContext(module)

    val scope = module.resources(module.mainFile).ast.body.head.asInstanceOf[HelperBlock].funcs.get.head.scope
    assert("MyFile/array" == scope.variables.head._1)
    val array = scope.variables.head._2.asInstanceOf[Operation].content.toOption.get.asInstanceOf[ArrayValue].tbl.get
    assert(1 == array.head.value.content.toOption.get.asInstanceOf[TLangLong].getElement)
    assert(2 == array(1).value.content.toOption.get.asInstanceOf[TLangLong].getElement)
    assert(3 == array(2).value.content.toOption.get.asInstanceOf[TLangLong].getElement)
    assert(4 == array(3).value.content.toOption.get.asInstanceOf[TLangLong].getElement)
    assert(5 == array.last.value.content.toOption.get.asInstanceOf[TLangLong].getElement)
  }

  test("Resolve content in for") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use MyPackage.MyFile
            |helper {
            |func myFunc() {
            |for(i 1 to 10) {
            |MyFile.content
            |}
            |}
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose content
            |model {
            |let content = "content"
            |
            |}""".stripMargin)
      }
    }

    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    ResolveContext.resolveContext(module)

    val scope = module.resources(module.mainFile).ast.body.head.asInstanceOf[HelperBlock].funcs.get.head.scope
    assert("MyFile/content" == scope.variables.head._1)
    assert("content" == scope.variables.head._2.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
  }

}
