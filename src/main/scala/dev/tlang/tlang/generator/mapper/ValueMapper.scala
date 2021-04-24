package dev.tlang.tlang.generator.mapper

import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.tmpl._
import dev.tlang.tlang.ast.tmpl.call._
import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.ast.tmpl.func.{TmplFunc, TmplFuncCurry}
import dev.tlang.tlang.ast.tmpl.primitive.{TmplPrimitiveValue, TmplStringValue, TmplTextValue}
import dev.tlang.tlang.interpreter.ExecCallObject
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.libraries.generator.Generator

object ValueMapper {

  def mapBlock(blockAsValue: TmplBlockAsValue): TmplBlockAsValue = {
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

  def mapContent(content: Option[List[TmplNode[_]]], context: Context): Option[List[TmplNode[_]]] = {
    if (content.isDefined) {
      val newContent: List[TmplNode[_]] = content.get.map {
        case func: TmplFunc => mapFunc(func, context)
        case expr: TmplExpression[_] => mapExpression(expr, context)
        case impl: TmplImpl => mapImpl(impl, context)
        // Specialized content
        case attr: TmplAttribute => mapAttribute(attr, context)
        case setAttr: TmplSetAttribute => mapSetAttribute(setAttr, context)
        case param: TmplParam => mapParam(param, context)
      }
      Some(newContent)
    } else None
  }

  def mapExpressions(exprs: Option[List[TmplExpression[_]]], context: Context): Option[List[TmplExpression[_]]] = {
    if (exprs.isDefined) {
      val newExprs: List[TmplExpression[_]] = exprs.get.map(mapExpression(_, context))
      Some(newExprs)
    } else None
  }

  def mapExpression(expr: TmplExpression[_], context: Context): TmplExpression[_] = {
    expr match {
      case call: TmplCallObj => mapCallObj(call, context)
      case func: TmplFunc => mapFunc(func, context)
      case valueType: TmplValueType[_] => mapValueType(valueType, context)
      case variable: TmplVar => mapVar(variable, context)
      //      case incl: TmplInclude => mapInclude(incl, context)
      case ret: TmplReturn => mapReturn(ret, context)
      case affect: TmplAffect => mapAffect(affect, context)
      case _ => expr
    }
  }

  //  def mapInclude(tmplInclude: TmplInclude, context: Context): TmplExpression[_] = {
  //    val contents = ListBuffer.empty[Either[TLangString, TmplBlockAsValue]]
  //    for (expr <- tmplInclude.calls) {
  //      ExecCallObject.run(expr, context) match {
  //        case Left(error) => println(error.message)
  //        case Right(value) =>
  //          value.get.foreach {
  //            case str: TLangString => contents.addOne(Left(str))
  //            case block: TmplBlockAsValue => contents.addOne(Right(mapBlock(block)))
  //          }
  //      }
  //    }
  //    tmplInclude.results = contents.toList
  //    tmplInclude
  //  }

  def mapImpl(impl: TmplImpl, context: Context): TmplImpl = {
    impl.name = mapID(impl.name, context)
    impl.content = mapContent(impl.content, context)
    impl.fors = mapFors(impl.fors, context)
    impl.withs = mapWiths(impl.withs, context)
    impl
  }

  def mapFors(aFor: Option[TmplImplFor], context: Context): Option[TmplImplFor] = {
    if (aFor.isDefined) {
      val newFor = aFor.get
      newFor.props = mapProps(newFor.props, context)
      newFor.types = newFor.types.map(t => mapType(t, context))
      Some(newFor)
    } else None
  }

  def mapWiths(withs: Option[TmplImplWith], context: Context): Option[TmplImplWith] = {
    if (withs.isDefined) {
      val newWiths = withs.get
      newWiths.props = mapProps(newWiths.props, context)
      newWiths.types = newWiths.types.map(t => mapType(t, context))
      Some(newWiths)
    } else None
  }

  def mapFunc(func: TmplFunc, context: Context): TmplFunc = {
    func.annots = mapAnnots(func.annots, context)
    func.props = mapProps(func.props, context)
    func.name = mapID(func.name, context)
    func.curries = mapCurries(func.curries, context)
    func.content = mapExprBlock(func.content, context)
    func.ret = mapTypes(func.ret, context)
    func
  }

  def mapAnnots(annots: Option[List[TmplAnnotation]], context: Context): Option[List[TmplAnnotation]] = {
    if (annots.isDefined) {
      annots.get.map(annot => {
        annot.name = mapID(annot.name, context)
        annot.values = mapAnnotParams(annot.values, context)
        annot
      })
      annots
    } else None
  }

  def mapAnnotParams(params: Option[List[TmplAnnotationParam]], context: Context): Option[List[TmplAnnotationParam]] = {
    if (params.isDefined) {
      params.get.foreach(param => {
        param.name = mapID(param.name, context)
        param.value = mapPrimitive(param.value, context)
      })
      params
    } else None
  }

  def mapProps(props: Option[TmplProp], context: Context): Option[TmplProp] = {
    if (props.isDefined) {
      val newProps = props.get
      newProps.props = newProps.props.map(p => mapID(p, context))
      Some(newProps)
    } else None
  }

  def mapExprBlock(block: Option[TmplExprBlock], context: Context): Option[TmplExprBlock] = {
    if (block.isDefined) {
      val exprs = block.get.exprs.map(expr => mapExpression(expr, context))
      Some(TmplExprBlock(block.get.context, exprs))
    } else None
  }

  def mapCurries(curries: Option[List[TmplFuncCurry]], context: Context): Option[List[TmplFuncCurry]] = {
    if (curries.isDefined) Some(curries.get.map(c => mapFuncCurry(c, context)))
    else None
  }

  def mapFuncCurry(curry: TmplFuncCurry, context: Context): TmplFuncCurry = {
    if (curry.params.isDefined) {
      curry.params = Some(curry.params.get.map(p => mapParam(p, context)))
    }
    curry
  }

  def mapParam(param: TmplParam, context: Context): TmplParam = {
    param.name = mapID(param.name, context)
    param.`type` = mapType(param.`type`, context)
    param
  }

  def mapReturn(ret: TmplReturn, context: Context): TmplReturn = {
    ret.operation = mapOperation(ret.operation, context)
    ret
  }

  def mapAffect(affect: TmplAffect, context: Context): TmplAffect = {
    affect.variable = mapCallObj(affect.variable, context)
    affect.value = mapOperation(affect.value, context)
    affect
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
    array.elem = mapOperation(array.elem, context)
    array
  }

  def mapCallFunc(func: TmplCallFunc, context: Context): TmplCallFunc = {
    func.name = mapID(func.name, context)
    func.currying = mapCallFuncCurryParams(func.currying, context)
    func
  }

  def mapCallFuncCurryParams(params: Option[List[TmplCurryParam]], context: Context): Option[List[TmplCurryParam]] = {
    if (params.isDefined) {
      val curry: List[TmplCurryParam] = params.get.map(p => TmplCurryParam(p.context, mapSetAttributes(p.params.asInstanceOf[Option[List[TmplSetAttribute]]], context)))
      Some(curry)
    } else None
  }

  def mapSetAttributes(attrs: Option[List[TmplSetAttribute]], context: Context): Option[List[TmplSetAttribute]] = {
    if (attrs.isDefined) {
      val newAttrs: List[TmplSetAttribute] = attrs.get.map(mapSetAttribute(_, context))
      Some(newAttrs)
    } else None
  }

  def mapSetAttribute(attr: TmplSetAttribute, context: Context): TmplSetAttribute = {
    attr.name = if (attr.name.isDefined) Some(mapID(attr.name.get, context)) else None
    attr.value = mapOperation(attr.value, context)
    attr
  }

  def mapAttribute(attrs: TmplAttribute, context: Context): TmplAttribute = {
    attrs.attr = mapOptID(attrs.attr, context)
    if (attrs.`type`.isDefined) attrs.`type` = Some(mapType(attrs.`type`.get, context))
    attrs.value = mapOperation(attrs.value, context)
    attrs
  }

  def mapCallVar(variable: TmplCallVar, context: Context): TmplCallVar = {
    variable.name = mapID(variable.name, context)
    variable
  }

  def mapValueType(value: TmplValueType[_], context: Context): TmplValueType[_] = {
    value match {
      case multi: TmplMultiValue => mapMultiValue(multi, context)
      case primitive: TmplPrimitiveValue[_] => mapPrimitive(primitive, context)
      case call: TmplCallObj => mapCallObj(call, context)
    }
  }

  def mapOperation(op: TmplOperation, context: Context): TmplOperation = {
    op.content match {
      case Left(subOp) => op.content = Left(mapOperation(subOp, context))
      case Right(expr) => op.content = Right(mapExpression(expr, context))
    }
    op.next = if (op.next.isDefined) Some(op.next.get._1, mapOperation(op.next.get._2, context)) else None
    op
  }

  def mapMultiValue(multi: TmplMultiValue, context: Context): TmplMultiValue = {
    multi.values = multi.values.map(mapValueType(_, context))
    multi
  }

  def mapPrimitive(primitive: TmplPrimitiveValue[_], context: Context): TmplPrimitiveValue[_] = {
    primitive match {
      case str: TmplStringValue => TmplStringValue(str.context, mapID(str.value, context))
      case text: TmplTextValue => TmplTextValue(text.context, mapID(text.value, context))
      case _ => primitive
    }
  }

  def mapVar(variable: TmplVar, context: Context): TmplVar = {
    variable.name = mapID(variable.name, context)
    variable.`type` = if (variable.`type`.isDefined) Some(mapType(variable.`type`.get, context)) else None
    variable.value = if (variable.value.isDefined) Some(mapOperation(variable.value.get, context)) else None
    variable
  }

  def mapTypes(types: Option[List[TmplType]], context: Context): Option[List[TmplType]] = {
    if (types.isDefined) Some(types.get.map(t => mapType(t, context)))
    else None
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

  def mapOptID(id: Option[TmplID], context: Context): Option[TmplStringID] = {
    if (id.isDefined) Some(mapID(id.get, context))
    else None
  }

  def mapID(id: TmplID, context: Context): TmplStringID = {
    id match {
      case interId: TmplInterpretedID => ExecCallObject.run(interId.call, context) match {
        case Left(error) => TmplStringID(interId.context, error.message)
        case Right(value) => if (value.isDefined) {
          value.get.head match {
            case str: TLangString => TmplStringID(interId.context, interId.pre.getOrElse("") + str.getElement + interId.post.getOrElse(""))
            case block: TmplBlockAsValue => TmplStringID(interId.context, interId.pre.getOrElse("") + Generator.generate(block, context) + interId.post.getOrElse(""))
            case _ => TmplStringID(interId.context, interId.pre.getOrElse("") + value.get.head.toString + interId.post.getOrElse(""))
          }
        } else TmplStringID(interId.context, "Undefined")
      }
      case replacedId: TmplReplacedId => TmplStringID(replacedId.context, replacedId.pre.getOrElse("") + replacedId.node.toString + replacedId.post.getOrElse(""))
      case str: TmplStringID => TmplStringID(str.context, str.id)
      case block: TmplBlockID => TmplStringID(block.context, "Undefined")
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
