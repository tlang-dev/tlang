package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainModel
import dev.tlang.tlang.ast.common.value.{TLangBool, TLangString}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetRef}
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils}
import dev.tlang.tlang.loader._
import dev.tlang.tlang.loader.remote.RemoteLoader
import org.scalatest.funsuite.AnyFunSuite

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
    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    val entity = ModelSetEntity(None, "MyEntity", Some(List(ModelSetAttribute(None, Some("param1"), ModelSetRef(None, List("Main", "myBool"), None)))), Some(List(ModelSetAttribute(None, Some("attr1"), ModelSetRef(None, List("Main", "myString"), None)))))
    ResolveModel.resolveSetEntity(entity, module, List(), Resource("Root", "", "", "", DomainModel(None, None, List())))
    assert(ContextUtils.findVar(Context(List(entity.scope)), "attr1").get.asInstanceOf[TLangBool].getElement)
    assert("myString" == ContextUtils.findVar(Context(List(entity.scope)), "param1").get.asInstanceOf[TLangString].getElement)
  }

}
