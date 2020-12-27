package io.sorne.tlang.generator

import io.sorne.tlang.ast.tmpl._
import io.sorne.tlang.ast.tmpl.call._
import io.sorne.tlang.ast.tmpl.condition.TmplConditionBlock
import io.sorne.tlang.ast.tmpl.func.{TmplFunc, TmplFuncCurry}
import io.sorne.tlang.ast.tmpl.primitive.{TmplPrimitiveValue, TmplStringValue, TmplTextValue}
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
    if (content.isDefined) {
      val newContent: List[TmplContent] = content.get.map {
        case expr: TmplExpression => mapExpression(expr, values)
        case func: TmplFunc => mapFunc(func, values)
        case impl: TmplImpl => mapImpl(impl, values)
      }
      Some(newContent)
    } else None
  }

  def mapExpressions(exprs: Option[List[TmplExpression]], values: Map[String, Value[_]]): Option[List[TmplExpression]] = {
    if (exprs.isDefined) {
      val newExprs: List[TmplExpression] = exprs.get.map(mapExpression(_, values))
      Some(newExprs)
    } else None
  }

  def mapExpression(expr: TmplExpression, values: Map[String, Value[_]]): TmplExpression = {
    expr match {
      case call: TmplCallObj => mapCallObj(call, values)
      case func: TmplFunc => mapFunc(func, values)
      case valueType: TmplValueType => mapValueType(valueType, values)
      case variable: TmplVar => mapVar(variable, values)
    }
  }

  def mapImpl(impl: TmplImpl, values: Map[String, Value[_]]): TmplImpl = {
    impl.name = mapString(impl.name, values)
    impl.content = mapContent(impl.content, values)
    impl.fors = mapFors(impl.fors, values)
    impl
  }

  def mapFors(fors: Option[List[TmplImplFor]], values: Map[String, Value[_]]): Option[List[TmplImplFor]] = {
    if (fors.isDefined) {
      val newFors: List[TmplImplFor] = fors.get.map(f => {
        f.name = mapString(f.name, values)
        f
      })
      Some(newFors)
    } else None
  }

  def mapFunc(func: TmplFunc, values: Map[String, Value[_]]): TmplFunc = {
    func.name = mapString(func.name, values)
    func.curries = mapCurries(func.curries, values)
    func.content = mapExpressions(func.content, values)
    func
  }

  def mapCurries(curries: Option[List[TmplFuncCurry]], values: Map[String, Value[_]]): Option[List[TmplFuncCurry]] = {
    curries
  }

  def mapCallObj(call: TmplCallObj, values: Map[String, Value[_]]): TmplCallObj = {
    call.calls = call.calls.map {
      case array: TmplCallArray => mapCallArray(array, values)
      case func: TmplCallFunc => mapCallFunc(func, values)
      case variable: TmplCallVar => mapCallVar(variable, values)
    }
    call
  }

  def mapCallArray(array: TmplCallArray, values: Map[String, Value[_]]): TmplCallArray = {
    array.name = mapString(array.name, values)
    array.elem = mapValueType(array.elem, values)
    array
  }

  def mapCallFunc(func: TmplCallFunc, values: Map[String, Value[_]]): TmplCallFunc = {
    func.name = mapString(func.name, values)
    func.currying = mapCallFuncCurryParams(func.currying, values)
    func
  }

  def mapCallFuncCurryParams(params: Option[List[TmplCurryParam]], values: Map[String, Value[_]]): Option[List[TmplCurryParam]] = {
    if (params.isDefined) {
      val curry: List[TmplCurryParam] = params.get.map(p => TmplCurryParam(mapSetAttributes(p.params, values)))
      Some(curry)
    } else None
  }

  def mapSetAttributes(attrs: Option[List[TmplSetAttribute]], values: Map[String, Value[_]]): Option[List[TmplSetAttribute]] = {
    if (attrs.isDefined) {
      val newAttrs: List[TmplSetAttribute] = attrs.get.map(attr => {
        attr.name = if (attr.name.isDefined) Some(mapString(attr.name.get, values)) else None
        attr.value = mapValueType(attr.value, values)
        attr
      })
      Some(newAttrs)
    } else None
  }

  def mapCallVar(variable: TmplCallVar, values: Map[String, Value[_]]): TmplCallVar = {
    variable.name = mapString(variable.name, values)
    variable
  }

  def mapValueType(value: TmplValueType, values: Map[String, Value[_]]): TmplValueType = {
    value match {
      case cond: TmplConditionBlock => mapCondition(cond, values)
      case multi: TmplMultiValue => mapMultiValue(multi, values)
      case primitive: TmplPrimitiveValue => mapPrimitive(primitive, values)
      case call: TmplCallObj => mapCallObj(call, values)
    }
  }

  def mapCondition(cond: TmplConditionBlock, values: Map[String, Value[_]]): TmplConditionBlock = {
    cond
  }

  def mapMultiValue(multi: TmplMultiValue, values: Map[String, Value[_]]): TmplMultiValue = {
    multi.values = multi.values.map(mapValueType(_, values))
    multi
  }

  def mapPrimitive(primitive: TmplPrimitiveValue, values: Map[String, Value[_]]): TmplPrimitiveValue = {
    primitive match {
      case str: TmplStringValue => str.value = mapString(str.value, values)
        str
      case text: TmplTextValue => text.value = mapString(text.value, values)
        text
      case _ => primitive
    }
  }

  def mapVar(variable: TmplVar, values: Map[String, Value[_]]): TmplVar = {
    variable.name = mapString(variable.name, values)
    variable.`type` = mapType(variable.`type`, values)
    variable.value = mapExpression(variable.value, values)
    variable
  }

  def mapType(`type`: TmplType, values: Map[String, Value[_]]): TmplType = {
    `type`.name = mapString(`type`.name, values)
    `type`.generic = mapGeneric(`type`.generic, values)
    `type`
  }

  def mapGeneric(gen: Option[TmplGeneric], values: Map[String, Value[_]]): Option[TmplGeneric] = {
    if (gen.isDefined) {
      gen.get.types = gen.get.types.map(mapType(_, values))
      gen
    } else None
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
