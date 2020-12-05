package io.sorne.tlang.resolver

import java.nio.file.Paths

import io.sorne.tlang.ast.common.value.{ArrayValue, TLangLong, TLangString}
import io.sorne.tlang.ast.helper.HelperBlock
import io.sorne.tlang.loader.{BuildModuleTree, ResourceLoader}
import org.scalatest.funsuite.AnyFunSuite

class ResolveForStatementTest extends AnyFunSuite {

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
    assert(1 == scope.variables.head._2.asInstanceOf[TLangLong].getValue)
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
    val array = scope.variables.head._2.asInstanceOf[ArrayValue].tbl.get
    assert(1 == array.head.value.asInstanceOf[TLangLong].getValue)
    assert(2 == array(1).value.asInstanceOf[TLangLong].getValue)
    assert(3 == array(2).value.asInstanceOf[TLangLong].getValue)
    assert(4 == array(3).value.asInstanceOf[TLangLong].getValue)
    assert(5 == array.last.value.asInstanceOf[TLangLong].getValue)
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
    assert("content" == scope.variables.head._2.asInstanceOf[TLangString].getValue)
  }

}
