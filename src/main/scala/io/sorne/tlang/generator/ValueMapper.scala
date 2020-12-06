package io.sorne.tlang.generator

import io.sorne.tlang.ast.tmpl.{TmplBlock, TmplBlockAsValue, TmplPkg}
import io.sorne.tlang.interpreter.Value

object ValueMapper {

  def map(blockAsValue: TmplBlockAsValue): TmplBlockAsValue = {
    val block = blockAsValue.block
    block.pkg = mapPkg(block.pkg, blockAsValue.params)
    blockAsValue
  }

  def mapPkg(pkg: Option[TmplPkg], values: Map[String, Value[_]]): Option[TmplPkg] = {
    pkg.foreach(p => p.parts = p.parts.map(mapString(_, values)))
    pkg
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
