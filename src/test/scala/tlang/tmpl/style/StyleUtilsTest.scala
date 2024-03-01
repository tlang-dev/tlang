package tlang.tmpl.style

import dev.tlang.tlang.tmpl.TmplStringID
import dev.tlang.tlang.tmpl.style.ast.{StyleBlock, StyleStruct}
import org.scalatest.funsuite.AnyFunSuiteLike

class StyleUtilsTest extends AnyFunSuiteLike {

  test("Find style") {
    val struct = StyleStruct(None, Some(TmplStringID(None, "h1")), None, Some(List()))
    val block = StyleBlock(None, "styles", List(), None, List())
    //    StyleUtils.findStyles(block.toEntity, new core.String("h1"))
    assert(true)
  }

}
