package dev.tlang.tlang.loader

import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.loader.manifest.{Dependency, Manifest, Stability}
import dev.tlang.tlang.loader.remote.RemoteLoader
import org.scalatest.funsuite.AnyFunSuite

import java.nio.file.Paths
import java.util.UUID.randomUUID
import scala.collection.mutable

class BuildModuleTreeTest extends AnyFunSuite {

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
      if (name == "Main.tlang") {
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
      if (name == "Main.tlang") {
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
      if (name == "Main.tlang") {
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

    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
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
      if (name == "MainFile.tlang") {
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

    val module = BuildModuleTree.build(Paths.get("Root", "MainPackage/MainFile"), "").toOption.get
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

  test("Browse external resources with TLang modules") {
    val manifest = Manifest("MyName", "MyProject", "MyOrg", "1.0.0", None, 1, None, Some(List(
      Dependency("TLang", "IO", "Terminal", "1.0.0", Stability.ALPHA, 1, Some("terminal")),
      Dependency("TLang", "Generator", "Generator", "1.0.0", Stability.ALPHA, 1, Some("generator")),
    )))
    val res = BuildModuleTree.browseExternalResources(manifest, randomUUID().toString).toOption.get
    assert(2 == res.size)
    assert("Terminal" == res("terminal").manifest.name)
    assert("Generator" == res("generator").manifest.name)
  }

}
