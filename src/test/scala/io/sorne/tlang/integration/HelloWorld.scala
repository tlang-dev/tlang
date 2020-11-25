package io.sorne.tlang.integration

import io.sorne.tlang.loader.ResourceLoader
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
