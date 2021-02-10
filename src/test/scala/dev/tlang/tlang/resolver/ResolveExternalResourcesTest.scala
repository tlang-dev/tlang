package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.helper.HelperBlock
import dev.tlang.tlang.loader.{BuildModuleTree, FileResourceLoader, ResourceLoader, TBagManager}
import dev.tlang.tlang.loader.remote.RemoteLoader

import java.nio.file.Paths
import io.sorne.tlang.ast.helper.HelperBlock
import io.sorne.tlang.loader.remote.RemoteLoader
import io.sorne.tlang.loader.{BuildModuleTree, FileResourceLoader, LoaderError, ResourceLoader, TBagManager}
import org.scalatest.funsuite.AnyFunSuite

class ResolveExternalResourcesTest extends AnyFunSuite {

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
      |dependencies:
      |  - TLang/Generator/Generator 1.0.0:alpha:1 gen
      |""".stripMargin

  test("Resolve from external resources") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
      if (name == "Main.tlang") {
        Right(
          """
            |use gen.Generator
            |helper {
            |func myFunc() {
            |Generator.generate(block)
            |}
            |}""".stripMargin)
      } else if (name == "manifest.yaml") {
        Right(defaultManifest)
      }else {
        Left(LoaderError("Not Found"))
      }
    }

    val module = BuildModuleTree.build(Paths.get("Root"), None).toOption.get
    ResolveContext.resolveContext(module)

    val scope = module.resources(module.mainFile).ast.body.head.asInstanceOf[HelperBlock].funcs.get.head.scope
    assert("Generator/generate" == scope.functions.head._1)
    assert("generate" == scope.functions.head._2.name)
  }

}
