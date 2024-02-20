package tlang.tmpl.style

import dev.tlang.tlang.tmpl.style.ast.{StyleBlock, StyleStruct}
import org.scalatest.funsuite.AnyFunSuiteLike
import tlang.core
import tlang.core.Null
import tlang.internal.TmplStringId

class StyleUtilsTest extends AnyFunSuiteLike {

  test("Find style") {
    val struct = StyleStruct(Null.empty(), Some(new TmplStringId(None, "h1")), None, Some(List()))
    val block = StyleBlock(Null.empty(), "styles", List(), None, List())
    StyleUtils.findStyles(block.toEntity, new core.String("h1"))
    assert(true)
  }

}
