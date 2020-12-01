package io.sorne.tlang.resolver

import java.nio.file.Paths

import io.sorne.tlang.ast.helper.HelperBlock
import io.sorne.tlang.loader.{BuildModuleTree, LoaderError, ResourceLoader}
import org.scalatest.funsuite.AnyFunSuite

class ResolveExternalResourcesTest extends AnyFunSuite {

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
      if (name == "Main") {
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
