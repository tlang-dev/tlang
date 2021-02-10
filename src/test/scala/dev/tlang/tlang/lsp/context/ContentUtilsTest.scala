package dev.tlang.tlang.lsp.context

import dev.tlang.tlang.lsp.TLangTextDocumentService.Position
import org.scalatest.funsuite.AnyFunSuite

class ContentUtilsTest extends AnyFunSuite {
  val longText = new StringBuilder("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum lobortis quam nec fringilla congue.\n" +
    "Cras a leo id libero pretium suscipit. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.\n" +
    "Morbi dictum a nibh in venenatis. Nullam pellentesque risus tincidunt rhoncus tristique.\n" +
    "Sed at auctor risus.\n" +
    "hasellus pulvinar cursus odio, in ornare ligula lacinia a.\n" +
    "Phasellus maximus auctor dolor in tristique. Proin sed nibh libero.\n" +
    "Maecenas leo eros, ultricies vel tellus id, dapibus tincidunt dui.\n" +
    "Aenean nulla ante, ornare sed velit eu, pulvinar aliquam lorem.\n\n" +
    "Donec quis aliquam velit, eu elementum ante.\n" +
    "hasellus urna elit, congue varius urna ut, blandit maximus mi.\n" +
    "Phasellus quis dignissim turpis, ac dictum orci.\n" +
    "Vivamus maximus purus vitae tortor viverra, non consequat metus semper.\n" +
    "Vestibulum id turpis rhoncus, efficitur velit et, tristique augue.\n" +
    "Maecenas sagittis nisi ac leo ullamcorper, id egestas felis auctor.\n" +
    "Nam posuere id nunc lobortis sollicitudin.\n" +
    "Duis eu orci sit amet mi luctus pulvinar.\n" +
    "Aenean sagittis dapibus varius.\n" +
    "Etiam ultricies id orci ac lobortis.\n" +
    "Fusce ut magna aliquet, scelerisque nisi nec, ultricies elit.\n" +
    "Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos.\n" +
    "Duis et eleifend risus.")

  val shortText = new StringBuilder("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum lobortis quam nec fringilla congue.\n" +
    "Cras a leo id libero pretium suscipit. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.\n" +
    "Morbi dictum a nibh in venenatis. Nullam pellentesque risus tincidunt rhoncus tristique.\n" +
    "Sed at auctor risus.")

  test("Find char number by position") {
    assert(340 == ContentUtils.findCharNumberByPosition(longText, Position(4, 0)))
    assert(1019 == ContentUtils.findCharNumberByPosition(longText, Position(16, 13)))
  }

  test("Find position by char number") {
    assert(Position(4, 0) == ContentUtils.findPositionByCharNumber(longText, 340))
    assert(Position(16, 13) == ContentUtils.findPositionByCharNumber(longText, 1019))
  }

  test("Replace text in StringBuilder") {
    val expected = new StringBuilder("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum lobortis quam nec fringilla congue.\n" +
      "Cras a leo id libero pretium suscipit. Orci varius natoque penatibus et Etiam ultricies id orci ac lobortis.\n" +
      "Fusce ut magna aliquet, scelerisque nisi nec, ultricies elit. Nullam pellentesque risus tincidunt rhoncus tristique.\n" +
      "Sed at auctor risus.")
    val newText = "Etiam ultricies id orci ac lobortis.\n" +
      "Fusce ut magna aliquet, scelerisque nisi nec, ultricies elit"
    assert(expected.equals(ContentUtils.replace(shortText, Position(1, 72), Position(2, 32), newText)))
  }

}
