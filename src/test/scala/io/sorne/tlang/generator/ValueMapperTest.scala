package io.sorne.tlang.generator

import io.sorne.tlang.ast.common.value.TLangString
import io.sorne.tlang.ast.tmpl.{TmplPkg, TmplUse}
import org.scalatest.funsuite.AnyFunSuite

class ValueMapperTest extends AnyFunSuite {

  test("Replace String") {
    val values = Map("one" -> new TLangString("This is the first one"), "two" -> new TLangString("the second one"), "three" -> new TLangString("the last one"))
    val str = "${one} to replace, obviously this is ${two}, and finally, this is ${three}"
    val res = ValueMapper.mapString(str, values)
    assert("This is the first one to replace, obviously this is the second one, and finally, this is the last one" == res)
  }

  test("Replace in package") {
    val values = Map("one" -> new TLangString("Package1"), "two" -> new TLangString("Package2"))
    val res = ValueMapper.mapPkg(Some(new TmplPkg(List("${one}", "${two}"))), values).get
    assert("Package1" == res.parts.head)
    assert("Package2" == res.parts.last)
  }

  test("Replace in uses") {
    val values = Map("one" -> new TLangString("Package1"), "two" -> new TLangString("Package2"))
    val res = ValueMapper.mapUses(Some(List(new TmplUse(List("${one}", "Package2")), new TmplUse(List("Package1", "${two}")))), values).get
    assert("Package1" == res.head.parts.head)
    assert("Package2" == res.head.parts.last)
    assert("Package1" == res.last.parts.head)
    assert("Package2" == res.last.parts.last)
  }

}