package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.common.value.{AssignVar, EntityValue}
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.loader.remote.RemoteLoader
import dev.tlang.tlang.loader.{BuildModuleTree, FileResourceLoader, ResourceLoader, TBagManager}
import org.scalatest.funsuite.AnyFunSuite

import java.nio.file.Paths

class BrowseNewEntityTest extends AnyFunSuite {

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

  test("Attr in entity") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use MyPackage.MyFile
            |model {
            |let entity1: MyEntity = {
            |  attr1 MyFile.myEntity
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
    val entity = module.resources("Main").ast.body.head.asInstanceOf[ModelBlock].content.get.head.asInstanceOf[AssignVar].value.content.toOption.get.asInstanceOf[EntityValue]
    val resource = module.resources("Main")
    BrowseNewEntity.browseEntity(entity, module, resource.ast.header.get.uses.get, resource)
    assert(entity.scope.variables.contains("MyFile/myEntity"))
  }

  test("Attr in impl") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use MyPackage.MyFile
            |model {
            |let entity1: MyEntity = {
            |  attr1 impl {
            |    attr2 MyFile.myEntity
            |  }
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
    val entity = module.resources("Main").ast.body.head.asInstanceOf[ModelBlock].content.get.head.asInstanceOf[AssignVar].value.content.toOption.get.asInstanceOf[EntityValue]
    val resource = module.resources("Main")
    BrowseNewEntity.browseEntity(entity, module, resource.ast.header.get.uses.get, resource)
    assert(entity.scope.variables.contains("MyFile/myEntity"))
  }
}
