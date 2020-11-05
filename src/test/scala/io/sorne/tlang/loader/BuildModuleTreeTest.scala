package io.sorne.tlang.loader

import java.nio.file.Paths

import io.sorne.tlang.ast.model.ModelBlock
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class BuildModuleTreeTest extends AnyFunSuite {

  test("Build resource AST") {
    val content =
      """
        |model {
        |}""".stripMargin
    val resource = BuildModuleTree.buildResourceAST("Root", "Level1", "Package", "Main", content)
    assert("Root".equals(resource.rootDir))
    assert("Level1".equals(resource.fromRoot))
    assert("Package".equals(resource.pkg))
    assert("Main".equals(resource.name))
    assert(resource.ast.body.head.isInstanceOf[ModelBlock])
  }

  test("Browse resource") {
    val resources = mutable.Map.empty[String, Resource]
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name.equals("Main")) {
        Right(
          """
            |use MyFile
            |model {
            |}""".stripMargin)
      } else {
        Right(
          """
            |model {
            |}""".stripMargin)
      }
    }
    BuildModuleTree.browseResources("Root", "FromRoot", "Package", "Main", resources)
    assert(2 == resources.size)
    val resName1 = resources.head._1
    val resName2 = resources.last._1
    val res1 = resources.head._2
    val res2 = resources.last._2
    assert("FromRoot/Package/Main".equals(resName1))
    assert("Root".equals(res1.rootDir))
    assert("FromRoot".equals(res1.fromRoot))
    assert("Package".equals(res1.pkg))
    assert("Main".equals(res1.name))

    assert("FromRoot/Package/MyFile".equals(resName2))
    assert("Root".equals(res2.rootDir))
    assert("FromRoot".equals(res2.fromRoot))
    assert("Package".equals(res2.pkg))
    assert("MyFile".equals(res2.name))
  }

  test("Browse resource in sub package") {
    val resources = mutable.Map.empty[String, Resource]
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name.equals("Main")) {
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
    BuildModuleTree.browseResources("Root", "FromRoot", "Package", "Main", resources)
    assert(2 == resources.size)
    val resName1 = resources.head._1
    val resName2 = resources.last._1
    val res1 = resources.head._2
    val res2 = resources.last._2
    assert("FromRoot/Package/MyPackage/MyFile".equals(resName1))
    assert("Root".equals(res1.rootDir))
    assert("FromRoot/Package".equals(res1.fromRoot))
    assert("MyPackage".equals(res1.pkg))
    assert("MyFile".equals(res1.name))

    assert("FromRoot/Package/Main".equals(resName2))
    assert("Root".equals(res2.rootDir))
    assert("FromRoot".equals(res2.fromRoot))
    assert("Package".equals(res2.pkg))
    assert("Main".equals(res2.name))
  }

  test("Build module tree") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name.equals("Main")) {
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
    assert(module.extResources.isEmpty)
    assert("Root" == module.rootDir)
    assert(2 == module.resources.size)
    val res1 = module.resources("MyPackage/MyFile")
    val res2 = module.resources("Main")
    assert("Root".equals(res1.rootDir))
    assert("".equals(res1.fromRoot))
    assert("MyPackage".equals(res1.pkg))
    assert("MyFile".equals(res1.name))

    assert("Root".equals(res2.rootDir))
    assert("".equals(res2.fromRoot))
    assert("".equals(res2.pkg))
    assert("Main".equals(res2.name))
  }

  test("Build module tree with defined main") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name.equals("MainFile")) {
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

    val module = BuildModuleTree.build(Paths.get("Root"), Some("MainPackage/MainFile")).toOption.get
    assert(module.extResources.isEmpty)
    assert("Root" == module.rootDir)
    assert(2 == module.resources.size)
    val res1 = module.resources("MainPackage/MainFile")
    val res2 = module.resources("MainPackage/MyPackage/MyFile")
    assert("Root".equals(res1.rootDir))
    assert("".equals(res1.fromRoot))
    assert("MainPackage".equals(res1.pkg))
    assert("MainFile".equals(res1.name))

    assert("Root".equals(res2.rootDir))
    assert("MainPackage".equals(res2.fromRoot))
    assert("MyPackage".equals(res2.pkg))
    assert("MyFile".equals(res2.name))
  }

}
