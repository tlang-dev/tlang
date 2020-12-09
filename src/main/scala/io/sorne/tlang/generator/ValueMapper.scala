package io.sorne.tlang.generator

import io.sorne.tlang.ast.tmpl.func.TmplFunc
import io.sorne.tlang.ast.tmpl._
import io.sorne.tlang.interpreter.Value

object ValueMapper {

  def map(blockAsValue: TmplBlockAsValue): TmplBlockAsValue = {
    val block = blockAsValue.block
    val params = blockAsValue.params
    block.pkg = mapPkg(block.pkg, params)
    block.uses = mapUses(block.uses, params)
    block.content = mapContent(block.content, params)
    blockAsValue
  }

  def mapPkg(pkg: Option[TmplPkg], values: Map[String, Value[_]]): Option[TmplPkg] = {
    pkg.foreach(p => p.parts = p.parts.map(mapString(_, values)))
    pkg
  }

  def mapUses(uses: Option[List[TmplUse]], values: Map[String, Value[_]]): Option[List[TmplUse]] = {
    uses.foreach(_.foreach(use => use.parts = use.parts.map(mapString(_, values))))
    uses
  }

  def mapContent(content: Option[List[TmplContent]], values: Map[String, Value[_]]): Option[List[TmplContent]] = {
    content.foreach(_.foreach {
      case expr: TmplExpression =>
      case func: TmplFunc =>
      case impl: TmplImpl =>
    })
    content
  }

  def mapString(str: String, values: Map[String, Value[_]]): String = {
    var pos = str.indexOf("${")
    val ret = new StringBuilder(str)
    var end = 0
    var search = ""
    while (pos > -1) {
      end = ret.indexOf("}", pos)
      search = ret.substring(pos + 2, end)
      val newVal = values(search).toString
      ret.replace(pos, end + 1, newVal)
      pos = ret.indexOf("${", pos + (newVal.length - (search.length + 3)))
    }
    ret.toString
  }

}
