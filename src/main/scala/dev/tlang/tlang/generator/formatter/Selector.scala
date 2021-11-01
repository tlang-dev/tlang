package dev.tlang.tlang.generator.formatter

import scala.collection.mutable.ListBuffer

case class Selector(token: String, children: ListBuffer[Selector] = ListBuffer.empty, rule: Option[Rule] = None, var level: Int = 0) {

  def +=(selector: Selector): Selector = {
    selector.level = level + 1
    children.addOne(selector)
    selector
  }
}
