package io.sorne.tlang.loader

import java.nio.file.Paths

import io.sorne.tlang.ast.model.ModelBlock
import io.sorne.tlang.loader.manifest.Stability
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class BuildModuleTreeTest extends AnyFunSuite {

  val defaultManifest: String =
    """name: MyProgram
      |project: MyProject
      |organisation: MyOrganisation
      |version: 1.33.7
      |stability: final
      |releaseNumber: 2
      |""".stripMargin

  test("Build resource AST") {
    val content =
      """
        |model {
        |}""".stripMargin
    val resource = BuildModuleTree.buildResourceAST("Root", "Level1", "Package", "Main", content)
    assert("Root" == resource.rootDir)
    assert("Level1" == resource.fromRoot)
    assert("Package" == resource.pkg)
    assert("Main" == resource.name)
    assert(resource.ast.body.head.isInstanceOf[ModelBlock])
  }

  test("Browse resource") {
    val resources = mutable.Map.empty[String, Resource]
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main") {
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
    assert("FromRoot/Package/Main" == resName1)
    assert("Root" == res1.rootDir)
    assert("FromRoot" == res1.fromRoot)
    assert("Package" == res1.pkg)
    assert("Main" == res1.name)

    assert("FromRoot/Package/MyFile" == resName2)
    assert("Root" == res2.rootDir)
    assert("FromRoot" == res2.fromRoot)
    assert("Package" == res2.pkg)
    assert("MyFile" == res2.name)
  }

  test("Browse resource in sub package") {
    val resources = mutable.Map.empty[String, Resource]
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
    BuildModuleTree.browseResources("Root", "FromRoot", "Package", "Main", resources)
    assert(2 == resources.size)
    val resName1 = resources.head._1
    val resName2 = resources.last._1
    val res1 = resources.head._2
    val res2 = resources.last._2
    assert("FromRoot/Package/MyPackage/MyFile" == resName1)
    assert("Root" == res1.rootDir)
    assert("FromRoot/Package" == res1.fromRoot)
    assert("MyPackage" == res1.pkg)
    assert("MyFile" == res1.name)

    assert("FromRoot/Package/Main" == resName2)
    assert("Root" == res2.rootDir)
    assert("FromRoot" == res2.fromRoot)
    assert("Package" == res2.pkg)
    assert("Main" == res2.name)
  }

  test("Build module tree") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main") {
        Right(
          """
            |use MyPackage.MyFile
            |model {
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |model {
            |}""".stripMargin)
      }
    }

    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    assert("Main" == module.mainFile)
    assert(module.extResources.isEmpty)
    assert("Root" == module.rootDir)
    assert(2 == module.resources.size)
    val res1 = module.resources("MyPackage/MyFile")
    val res2 = module.resources("Main")
    assert("Root" == res1.rootDir)
    assert("" == res1.fromRoot)
    assert("MyPackage" == res1.pkg)
    assert("MyFile" == res1.name)

    assert("Root" == res2.rootDir)
    assert("" == res2.fromRoot)
    assert("" == res2.pkg)
    assert("Main" == res2.name)
  }

  test("Build module tree with defined main") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "MainFile") {
        Right(
          """
            |use MyPackage.MyFile
            |model {
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |model {
            |}""".stripMargin)
      }
    }

    val module = BuildModuleTree.build(Paths.get("Root"), Some("MainPackage/MainFile")).toOption.get
    assert("MainPackage/MainFile" == module.mainFile)
    assert(module.extResources.isEmpty)
    assert("Root" == module.rootDir)
    assert(2 == module.resources.size)
    val res1 = module.resources("MainPackage/MainFile")
    val res2 = module.resources("MainPackage/MyPackage/MyFile")
    assert("Root" == res1.rootDir)
    assert("" == res1.fromRoot)
    assert("MainPackage" == res1.pkg)
    assert("MainFile" == res1.name)

    assert("Root" == res2.rootDir)
    assert("MainPackage" == res2.fromRoot)
    assert("MyPackage" == res2.pkg)
    assert("MyFile" == res2.name)
  }

  test("Create package name") {
    val res = BuildModuleTree.createPkg("", "", "Part1", "Part2", "", "Part3")
    assert("Part1/Part2/Part3" == res)
  }

  test("Build manifest") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, _: String) => {
      Right(
        """
          |name: MyProgram
          |project: MyProject
          |organisation: MyOrganisation
          |version: 1.33.7
          |stability: final
          |releaseNumber: 2
          |""".stripMargin)
    }
    val manifest = BuildModuleTree.buildManifest(Paths.get("Root")).toOption.get
    assert("MyProgram" == manifest.name)
    assert("MyProject" == manifest.project)
    assert("MyOrganisation" == manifest.organisation)
    assert("1.33.7" == manifest.version)
    assert(Stability.FINAL == manifest.stability.get)
    assert(2 == manifest.releaseNumber)
  }

}
