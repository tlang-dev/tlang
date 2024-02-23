package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainModel
import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetRef}
import dev.tlang.tlang.loader._
import dev.tlang.tlang.loader.remote.RemoteLoader
import org.scalatest.funsuite.AnyFunSuite
import tlang.core.Null

import java.nio.file.Paths

class ResolveModelTest extends AnyFunSuite {

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

  test("Resolve entity") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |model {
            |let myBool = true
            |let myString = "myString"
            |}""".stripMargin)
      } else {
        Right(defaultManifest)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
    val entity = ModelSetEntity(Null.empty(), ManualType("", "MyEntity"), None, Some(List(ModelSetAttribute(Null.empty(), Some("param1"), ModelSetRef(Null.empty(), List("Main", "myBool"), None)))), Some(List(ModelSetAttribute(Null.empty(), Some("attr1"), ModelSetRef(Null.empty(), List("Main", "myString"), None)))))
    ResolveModel.resolveSetEntity(entity, module, List(), Resource("Root", "", "", "", DomainModel(Null.empty(), None, List())))
    //    assert(ContextUtils.findVar(Context(List(entity.scope)), "attr1").get.asInstanceOf[TLangBool].getElement)
    //    assert("myString" == ContextUtils.findVar(Context(List(entity.scope)), "param1").get.asInstanceOf[TLangString].getElement)
  }

}
