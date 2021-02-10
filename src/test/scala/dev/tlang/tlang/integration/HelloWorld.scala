package dev.tlang.tlang.integration

import dev.tlang.tlang.loader.ResourceLoader
import dev.tlang.tlang.loader.ResourceLoader
import org.scalatest.funsuite.AnyFunSuite

class HelloWorld extends AnyFunSuite {

  test("Hello world") {
    implicit val loader: ResourceLoader = (_: String, _: String, _: String, name: String) => {
        Right(
          """
            |use MyPackage.MyFile
            |helper {
            |func Main() {
            |MyFile.myEntity
            |}
            |}""".stripMargin)
    }

  }

}
