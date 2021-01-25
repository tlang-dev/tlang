package io.sorne.tlang.generator

import io.sorne.tlang.ast.tmpl._
import io.sorne.tlang.ast.tmpl.call._
import io.sorne.tlang.ast.tmpl.condition.TmplConditionBlock
import io.sorne.tlang.ast.tmpl.func.{TmplFunc, TmplFuncCurry}
import io.sorne.tlang.ast.tmpl.primitive.{TmplPrimitiveValue, TmplStringValue, TmplTextValue}
import io.sorne.tlang.interpreter.ExecCallObject
import io.sorne.tlang.interpreter.context.Context

object ValueMapper {

  def map(blockAsValue: TmplBlockAsValue): TmplBlockAsValue = {
    val block = blockAsValue.block
    val con = blockAsValue.context
    block.pkg = mapPkg(block.pkg, con)
    block.uses = mapUses(block.uses, con)
    block.content = mapContent(block.content, con)
    blockAsValue
  }

  def mapPkg(pkg: Option[TmplPkg], context: Context): Option[TmplPkg] = {
    pkg.foreach(p => p.parts = p.parts.map(mapID(_, context)))
    pkg
  }

  def mapUses(uses: Option[List[TmplUse]], context: Context): Option[List[TmplUse]] = {
    uses.foreach(_.foreach(use => use.parts = use.parts.map(mapID(_, context))))
    uses
  }

  def mapContent(content: Option[List[TmplContent]], context: Context): Option[List[TmplContent]] = {
    if (content.isDefined) {
      val newContent: List[TmplContent] = content.get.map {
        case func: TmplFunc => mapFunc(func, context)
        case expr: TmplExpression => mapExpression(expr, context)
        case impl: TmplImpl => mapImpl(impl, context)
      }
      Some(newContent)
    } else None
  }

  def mapExpressions(exprs: Option[List[TmplExpression]], context: Context): Option[List[TmplExpression]] = {
    if (exprs.isDefined) {
      val newExprs: List[TmplExpression] = exprs.get.map(mapExpression(_, context))
      Some(newExprs)
    } else None
  }

  def mapExpression(expr: TmplExpression, context: Context): TmplExpression = {
    expr match {
      case call: TmplCallObj => mapCallObj(call, context)
      case func: TmplFunc => mapFunc(func, context)
      case valueType: TmplValueType => mapValueType(valueType, context)
      case variable: TmplVar => mapVar(variable, context)
    }
  }

  def mapImpl(impl: TmplImpl, context: Context): TmplImpl = {
    impl.name = mapID(impl.name, context)
    impl.content = mapContent(impl.content, context)
    impl.fors = mapFors(impl.fors, context)
    impl
  }

  def mapFors(fors: Option[List[TmplImplFor]], context: Context): Option[List[TmplImplFor]] = {
    if (fors.isDefined) {
      val newFors: List[TmplImplFor] = fors.get.map(f => {
        f.name = mapID(f.name, context)
        f
      })
      Some(newFors)
    } else None
  }

  def mapFunc(func: TmplFunc, context: Context): TmplFunc = {
    func.name = mapID(func.name, context)
    func.curries = mapCurries(func.curries, context)
    func.content = mapExprBlock(func.content, context)
    func
  }

  def mapExprBlock(block: Option[TmplExprBlock], context: Context): Option[TmplExprBlock] = {
    if (block.isDefined) {
      val exprs = block.get.exprs.map(expr => mapExpression(expr, context))
      Some(TmplExprBlock(exprs))
    } else None
  }

  def mapCurries(curries: Option[List[TmplFuncCurry]], context: Context): Option[List[TmplFuncCurry]] = {
    curries
  }

  def mapCallObj(call: TmplCallObj, context: Context): TmplCallObj = {
    call.calls = call.calls.map {
      case array: TmplCallArray => mapCallArray(array, context)
      case func: TmplCallFunc => mapCallFunc(func, context)
      case variable: TmplCallVar => mapCallVar(variable, context)
    }
    call
  }

  def mapCallArray(array: TmplCallArray, context: Context): TmplCallArray = {
    array.name = mapID(array.name, context)
    array.elem = mapValueType(array.elem, context)
    array
  }

  def mapCallFunc(func: TmplCallFunc, context: Context): TmplCallFunc = {
    func.name = mapID(func.name, context)
    func.currying = mapCallFuncCurryParams(func.currying, context)
    func
  }

  def mapCallFuncCurryParams(params: Option[List[TmplCurryParam]], context: Context): Option[List[TmplCurryParam]] = {
    if (params.isDefined) {
      val curry: List[TmplCurryParam] = params.get.map(p => TmplCurryParam(mapSetAttributes(p.params, context)))
      Some(curry)
    } else None
  }

  def mapSetAttributes(attrs: Option[List[TmplSetAttribute]], context: Context): Option[List[TmplSetAttribute]] = {
    if (attrs.isDefined) {
      val newAttrs: List[TmplSetAttribute] = attrs.get.map(attr => {
        attr.name = if (attr.name.isDefined) Some(mapID(attr.name.get, context)) else None
        attr.value = mapValueType(attr.value, context)
        attr
      })
      Some(newAttrs)
    } else None
  }

  def mapCallVar(variable: TmplCallVar, context: Context): TmplCallVar = {
    variable.name = mapID(variable.name, context)
    variable
  }

  def mapValueType(value: TmplValueType, context: Context): TmplValueType = {
    value match {
      case cond: TmplConditionBlock => mapCondition(cond, context)
      case multi: TmplMultiValue => mapMultiValue(multi, context)
      case primitive: TmplPrimitiveValue => mapPrimitive(primitive, context)
      case call: TmplCallObj => mapCallObj(call, context)
    }
  }

  def mapCondition(cond: TmplConditionBlock, context: Context): TmplConditionBlock = {
    cond
  }

  def mapMultiValue(multi: TmplMultiValue, context: Context): TmplMultiValue = {
    multi.values = multi.values.map(mapValueType(_, context))
    multi
  }

  def mapPrimitive(primitive: TmplPrimitiveValue, context: Context): TmplPrimitiveValue = {
    primitive match {
      case str: TmplStringValue => str
      case text: TmplTextValue => text
      case _ => primitive
    }
  }

  def mapVar(variable: TmplVar, context: Context): TmplVar = {
    variable.name = mapID(variable.name, context)
    variable.`type` = mapType(variable.`type`, context)
    variable.value = mapExpression(variable.value, context)
    variable
  }

  def mapType(`type`: TmplType, context: Context): TmplType = {
    `type`.name = mapID(`type`.name, context)
    `type`.generic = mapGeneric(`type`.generic, context)
    `type`
  }

  def mapGeneric(gen: Option[TmplGeneric], context: Context): Option[TmplGeneric] = {
    if (gen.isDefined) {
      gen.get.types = gen.get.types.map(mapType(_, context))
      gen
    } else None
  }

  def mapID(id: TmplID, context: Context): TmplStringID = {
    id match {
      case interId: TmplInterpretedID => ExecCallObject.run(interId.call, context) match {
        case Left(error) => TmplStringID(error.message)
        case Right(value) => TmplStringID(interId.pre.getOrElse("") + value.fold("")(v => v.head.toString) + interId.post)
      }
      case str: TmplStringID => TmplStringID(str.id)
    }
    //      var pos = str.indexOf("${")
    //      val ret = new StringBuilder(str)
    //      var end = 0
    //      var search = ""
    //      while (pos > -1) {
    //        end = ret.indexOf("}", pos)
    //        search = ret.substring(pos + 2, end)
    //        val newVal = resolveValue(search, context)
    //        ret.replace(pos, end + 1, newVal)
    //        pos = ret.indexOf("${", pos + (newVal.length - (search.length + 3)))
    //      }
    //      ret.toString
  }

}
