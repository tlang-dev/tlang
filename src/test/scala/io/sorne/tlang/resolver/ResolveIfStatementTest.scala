package io.sorne.tlang.resolver

import java.nio.file.Paths
import io.sorne.tlang.ast.common.value.TLangBool
import io.sorne.tlang.ast.helper.HelperBlock
import io.sorne.tlang.loader.remote.RemoteLoader
import io.sorne.tlang.loader.{BuildModuleTree, FileResourceLoader, ResourceLoader, TBagManager}
import org.scalatest.funsuite.AnyFunSuite

class ResolveIfStatementTest extends AnyFunSuite {

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

  test("Resolve condition in if") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use MyPackage.MyFile
            |helper {
            |func myFunc() {
            |if(MyFile.myBool) {
            |}
            |}
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose myBool
            |model {
            |let myBool = true
            |
            |}""".stripMargin)
      }
    }

    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    ResolveContext.resolveContext(module)

    val scope = module.resources(module.mainFile).ast.body.head.asInstanceOf[HelperBlock].funcs.get.head.scope
    assert("MyFile/myBool" == scope.variables.head._1)
    assert(scope.variables.head._2.asInstanceOf[TLangBool].getValue)
  }

  test("Resolve true block in if") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use MyPackage.MyFile
            |helper {
            |func myFunc() {
            |if(1 == 1) {
            |MyFile.myBool
            |}
            |}
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose myBool
            |model {
            |let myBool = true
            |
            |}""".stripMargin)
      }
    }

    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    ResolveContext.resolveContext(module)

    val scope = module.resources(module.mainFile).ast.body.head.asInstanceOf[HelperBlock].funcs.get.head.scope
    assert("MyFile/myBool" == scope.variables.head._1)
    assert(scope.variables.head._2.asInstanceOf[TLangBool].getValue)
  }

  test("Resolve false block in if") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use MyPackage.MyFile
            |helper {
            |func myFunc() {
            |if(1 == 1) {
            |} else {
            |MyFile.myBool
            |}
            |}
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose myBool
            |model {
            |let myBool = true
            |
            |}""".stripMargin)
      }
    }

    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    ResolveContext.resolveContext(module)

    val scope = module.resources(module.mainFile).ast.body.head.asInstanceOf[HelperBlock].funcs.get.head.scope
    assert("MyFile/myBool" == scope.variables.head._1)
    assert(scope.variables.head._2.asInstanceOf[TLangBool].getValue)
  }

}
