package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{AssignVar, ComplexAttribute, EntityValue}
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.ast.model.set.{ModelSetEntity, ModelSetRef}
import dev.tlang.tlang.ast.{DomainModel, DomainUse}
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils, Scope}
import dev.tlang.tlang.loader._
import dev.tlang.tlang.loader.remote.RemoteLoader
import org.scalatest.funsuite.AnyFunSuite
import tlang.core.Null

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
            |use MyPackage.MyFile
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      } else {
        Right(
          """
            |expose MyEntityType
            |helper {
             func theFunc() {}
            |}
            |model {
            |set MyEntityType {myFunc: &theFunc()}
            |}""".stripMargin)
      }
    }
    val module = BuildModuleTree.build(Paths.get("Root"), "").toOption.get
    val entity = EntityValue(Null.empty(), Some(ObjType(Null.empty(), Some("MyFile"), ManualType("", "MyEntityType"))), Some(List(ComplexAttribute(Null.empty(), Some("attr1"), value = Operation(Null.empty(), None, Right(CallObject(Null.empty(), List(CallVarObject(Null.empty(), "MyEntityType"), CallFuncObject(Null.empty(), Some("theFunc"), None)))))))))
    val block = ModelBlock(Null.empty(), Some(List(AssignVar(Null.empty(), "myVar", None, Operation(Null.empty(), None, Right(entity)), Scope()))))
    ResolveModel.resolveModel(block, module, List(DomainUse(Null.empty(), List("MyPackage", "MyFile"))), Resource("Root", "", "", "", DomainModel(Null.empty(), None, List())))
    assert("theFunc" == ContextUtils.findModel(Context(List(entity.scope)), "MyEntityType").get.asInstanceOf[ModelSetEntity].attrs.get.head.value.asInstanceOf[ModelSetRef].refs.head)
    //    assert("myString" == ContextUtils.findVar(Context(List(entity.scope)), "param1").get.asInstanceOf[TLangString].getElement)
  }

}
