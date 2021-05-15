package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.{DomainModel, DomainUse}
import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{AssignVar, ComplexAttribute, EntityValue, TLangBool, TLangString}
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetRef}
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils, Scope}
import dev.tlang.tlang.loader.{BuildModuleTree, FileResourceLoader, Resource, ResourceLoader, TBagManager}
import dev.tlang.tlang.loader.remote.RemoteLoader
import org.scalatest.funsuite.AnyFunSuite

import java.nio.file.Paths

class FollowTypeTest extends AnyFunSuite {

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

  test("Resolve entity type") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |expose myEntity
            |helper {
             func theFunc() {}
            |}
            |model {
            |set MyEntity {myFunc &theFunc()}
            |}""".stripMargin)
      } else {
        Right(defaultManifest)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    val entity = EntityValue(None, Some("MyEntity"), Some(List(ComplexAttribute(None, Some("param1"), value = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "MyEntity"), CallFuncObject(None, Some("theFunc"), None)))))))))
    val block = ModelBlock(None, Some(List(AssignVar(None, "myVar", None, Operation(None, None, Right(entity)), Some(Scope())))))
    ResolveModel.resolveModel(block, module, List(DomainUse(None, List("Main", "MyEntity"))), Resource("Root", "", "", "", DomainModel(None, None, List())))
        assert("myValue"==ContextUtils.findVar(Context(List(entity.scope)), "attr1").get.asInstanceOf[TLangString].getElement)
    //    assert("myString" == ContextUtils.findVar(Context(List(entity.scope)), "param1").get.asInstanceOf[TLangString].getElement)
  }

}
