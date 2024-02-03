package dev.tlang.tlang.generator.mapper

import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.interpreter.ExecCallObject
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.libraries.generator.Generator
import dev.tlang.tlang.tmpl.common.ast.{LangBlockID, TmplID, TmplInterpretedID, TmplReplacedId, TmplStringID}
import dev.tlang.tlang.tmpl.TmplNode
import dev.tlang.tlang.tmpl.doc.ast.DocBlock
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.lang.ast.call._
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.func.{LangAnnotationParam, LangAnonFunc, LangFunc}
import dev.tlang.tlang.tmpl.lang.ast.loop.LangFor
import dev.tlang.tlang.tmpl.lang.ast.primitive._

import scala.collection.mutable.ListBuffer

object ValueMapper {

  def mapBlockAsValue(blockAsValue: LangBlockAsValue): LangBlockAsValue = {
    val block = blockAsValue.block
    val con = blockAsValue.context
    block match {
      case doc: DocBlock =>
      case lang: LangBlock => mapBlock(lang, con)
      case _ => println("ValueMapper TmplBlock type not implemented: " + block.getClass.getName)
    }
    blockAsValue
  }


  def mapBlock(block: LangBlock, context: Context): LangBlock = {
    block.content = mapFullBlock(block.content, context)
    block
  }

  def mapFullBlock(block: LangFullBlock, context: Context): LangFullBlock = {
    block.pkg = mapPkg(block.pkg, context)
    block.uses = mapUses(block.uses, context)
    block
  }

  def mapPkg(pkg: Option[LangPkg], context: Context): Option[LangPkg] = {
    pkg.foreach(p => p.parts = p.parts.map(mapID(_, context)))
    pkg
  }

  def mapUses(uses: Option[List[LangUse]], context: Context): Option[List[LangUse]] = {
    uses.foreach(_.foreach(use => mapUse(use, context)))
    uses
  }

  def mapUse(use: LangUse, context: Context): LangUse = {
    use.parts = use.parts.map(mapID(_, context))
    use
  }

  def mapContents(content: Option[List[TmplNode[_]]], context: Context): Option[List[TmplNode[_]]] = {
    if (content.isDefined) {
      val newContent = ListBuffer.empty[TmplNode[_]]

      content.get.foreach {
        case func: LangFunc => newContent += mapFunc(func, context)
        case block: LangSpecialBlock => newContent += mapSpecialBlock(block, context)
        case expr: LangExpression[_] => newContent += mapExpression(expr, context)
        //        case block: TmplBlock => mapContent(block.content, context).foreach { blocks => newContent.addAll(blocks) }
        // Specialized content
        case impl: LangImpl => newContent += mapImpl(impl, context)
        case attr: LangAttribute => newContent += mapAttribute(attr, context)
        case setAttr: LangSetAttribute => newContent += mapSetAttribute(setAttr, context)
        case param: LangParam => newContent += mapParam(param, context)
        case use: LangUse => newContent += mapUse(use, context)
        case block: LangBlock => newContent += mapBlock(block, context)
        //        newContent ++= mapNode(_, context)
      }
      Some(newContent.toList)
    } else None
  }

  def mapExpressions(exprs: Option[List[LangExpression[_]]], context: Context): Option[List[LangExpression[_]]] = {
    if (exprs.isDefined) {
      val newExprs: List[LangExpression[_]] = exprs.get.map(mapExpression(_, context))
      Some(newExprs)
    } else None
  }

  def mapExpression(expr: LangExpression[_], context: Context): LangExpression[_] = {
    expr match {
      case func: LangFunc => mapFunc(func, context)
      case variable: LangVar => mapVar(variable, context)
      case call: LangCallObj => mapCallObj(call, context)
      //      case incl: TmplInclude => mapInclude(incl, context)
      case primitiveValue: LangPrimitiveValue[_] => mapPrimitive(primitiveValue, context)
      case valueType: LangValueType[_] => mapValueType(valueType, context)
      case ret: LangReturn => mapReturn(ret, context)
      case affect: LangAffect => mapAffect(affect, context)
      case tmplFor: LangFor => mapFor(tmplFor, context)
      case anonFunc: LangAnonFunc => mapAnonFunc(anonFunc, context)
      case block: LangSpecialBlock => mapSpecialBlock(block, context)
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

  def mapImpl(impl: LangImpl, context: Context): LangImpl = {
    impl.name = mapID(impl.name, context)
    impl.content = mapContents(impl.content, context)
    impl.fors = mapFors(impl.fors, context)
    impl.withs = mapWiths(impl.withs, context)
    impl
  }

  def mapFors(aFor: Option[LangImplFor], context: Context): Option[LangImplFor] = {
    if (aFor.isDefined) {
      val newFor = aFor.get
      newFor.props = mapProps(newFor.props, context)
      newFor.types = newFor.types.map(t => mapType(t, context))
      Some(newFor)
    } else None
  }

  def mapWiths(withs: Option[LangImplWith], context: Context): Option[LangImplWith] = {
    if (withs.isDefined) {
      val newWiths = withs.get
      newWiths.props = mapProps(newWiths.props, context)
      newWiths.types = newWiths.types.map(t => mapType(t, context))
      Some(newWiths)
    } else None
  }

  def mapFunc(func: LangFunc, context: Context): LangFunc = {
    func.annots = mapAnnots(func.annots, context)
    func.props = mapProps(func.props, context)
    func.preNames = if (func.preNames.isDefined) Some(func.preNames.get.map(name => mapID(name, context))) else None
    func.name = mapID(func.name, context)
    //    func.curries = mapCurries(func.curries, context)
    func.content = func.content.map(mapExprContent(_, context))
    func.ret = mapTypes(func.ret, context)
    func
  }

  def mapAnnots(annots: Option[List[LangAnnotation]], context: Context): Option[List[LangAnnotation]] = {
    if (annots.isDefined) {
      annots.get.map(annot => {
        annot.name = mapID(annot.name, context)
        annot.values = mapAnnotParams(annot.values, context)
        annot
      })
      annots
    } else None
  }

  def mapAnnotParams(params: Option[List[LangAnnotationParam]], context: Context): Option[List[LangAnnotationParam]] = {
    if (params.isDefined) {
      params.get.foreach(param => {
        param.name = mapOptID(param.name, context)
        param.value = mapValueType(param.value, context)
      })
      params
    } else None
  }

  def mapProps(props: Option[LangProp], context: Context): Option[LangProp] = {
    if (props.isDefined) {
      val newProps = props.get
      newProps.props = newProps.props.map(p => mapID(p, context))
      Some(newProps)
    } else None
  }

  def mapExprContent(content: LangExprContent[_], context: Context): LangExprContent[_] = {
    content match {
      case block: LangExprBlock => mapExprBlock(block, context)
      case expression: LangExpression[_] => mapExpression(expression, context)
    }
  }

  def mapOptExprBlock(block: Option[LangExprBlock], context: Context): Option[LangExprBlock] = {
    if (block.isDefined) Some(mapExprBlock(block.get, context))
    else None
  }

  def mapExprBlock(block: LangExprBlock, context: Context): LangExprBlock = {
    val exprs = ListBuffer.empty[TmplNode[_]]
    //    block.exprs.foreach(_.getType)
    block.exprs.foreach {
      case block: LangBlock => exprs += mapFullBlock(block.content, context)
      case expr: LangExpression[_] => exprs += mapExpression(expr, context)
    }
    LangExprBlock(block.context, exprs.toList)
  }

  def mapSpecialBlock(block: LangSpecialBlock, context: Context): LangSpecialBlock = {
    //    block.curries = mapCurries(block.curries, context)
    if (block.content.isDefined) block.content = Some(mapExprContent(block.content.get, context))

    block
  }

  /*  def mapCurries(curries: Option[List[TmplFuncCurry]], context: Context): Option[List[TmplFuncCurry]] = {
      if (curries.isDefined) Some(curries.get.map(c => mapFuncCurry(c, context)))
      else None
    }

    def mapFuncCurry(curry: TmplFuncCurry, context: Context): TmplFuncCurry = {
      if (curry.params.isDefined) {
        curry.params = Some(curry.params.get.map(p => mapParam(p, context)))
      }
      curry
    }*/

  def mapParam(param: LangParam, context: Context): LangParam = {
    param.annots = mapAnnots(param.annots, context)
    param.name = mapID(param.name, context)
    if (param.`type`.isDefined) param.`type` = Some(mapType(param.`type`.get, context))
    param
  }

  def mapAnonFunc(anonFunc: LangAnonFunc, context: Context): LangAnonFunc = {
    //    anonFunc.currying = mapFuncCurry(anonFunc.currying, context)
    anonFunc.content = mapExprContent(anonFunc.content, context)
    anonFunc
  }

  def mapFor(tmplFor: LangFor, context: Context): LangFor = {
    tmplFor.variable = mapID(tmplFor.variable, context)
    if (tmplFor.start.isDefined) tmplFor.start = Some(mapOperation(tmplFor.start.get, context))
    tmplFor.cond = mapOperation(tmplFor.cond, context)
    tmplFor.content = mapExprContent(tmplFor.content, context)
    tmplFor
  }

  def mapReturn(ret: LangReturn, context: Context): LangReturn = {
    ret.operation = mapOperation(ret.operation, context)
    ret
  }

  def mapAffect(affect: LangAffect, context: Context): LangAffect = {
    affect.variable = mapCallObj(affect.variable, context)
    affect.value = mapOperation(affect.value, context)
    affect
  }

  def mapCallObj(call: LangCallObj, context: Context): LangCallObj = {
    call.firstCall = mapCallObjType(call.firstCall, context)
    call.calls = call.calls.map(link => mapCallObjLink(link, context))
    call
  }

  def mapCallObjLink(call: LangCallObjectLink, context: Context): LangCallObjectLink = {
    call.call = mapCallObjType(call.call, context)
    call
  }

  def mapCallObjType(objType: LangCallObjType[_], context: Context): LangCallObjType[_] = {
    objType match {
      case array: LangCallArray => mapCallArray(array, context)
      case func: LangCallFunc => mapCallFunc(func, context)
      case variable: LangCallVar => mapCallVar(variable, context)
      case primitive: LangPrimitiveValue[_] => mapPrimitive(primitive, context)
    }
  }

  def mapCallArray(array: LangCallArray, context: Context): LangCallArray = {
    array.name = mapID(array.name, context)
    array.elem = mapOperation(array.elem, context)
    array
  }

  def mapCallFunc(func: LangCallFunc, context: Context): LangCallFunc = {
    func.name = mapID(func.name, context)
    //    func.currying = mapCallFuncCurryParams(func.currying, context)
    func
  }

  /*def mapCallFuncCurryParams(params: Option[List[TmplCurryParam]], context: Context): Option[List[TmplCurryParam]] = {
    if (params.isDefined) {
      val curry: List[TmplCurryParam] = params.get.map(p => TmplCurryParam(p.context, mapCallFuncCurryParamTypes(p.params, context)))
      Some(curry)
    } else None
  }

  def mapCallFuncCurryParamTypes(params: List[TmplCallFuncParam], context: Context): List[TmplCallFuncParam] = {
    params.map(param => mapCallFuncCurryParamType(param, context))
  }

  def mapCallFuncCurryParamType(params: TmplCallFuncParam, context: Context): TmplCallFuncParam = {
    if (params.params.isDefined) params.params.get.map(attr => mapSetAttribute(attr.asInstanceOf[TmplSetAttribute], context))
    params
  }*/

  def mapSetAttributes(attrs: Option[List[LangSetAttribute]], context: Context): Option[List[LangSetAttribute]] = {
    if (attrs.isDefined) {
      val newAttrs: List[LangSetAttribute] = attrs.get.map(mapSetAttribute(_, context))
      Some(newAttrs)
    } else None
  }

  def mapSetAttribute(attr: LangSetAttribute, context: Context): LangSetAttribute = {
    attr.name = if (attr.name.isDefined) Some(mapID(attr.name.get, context)) else None
    attr.value = mapOperation(attr.value, context)
    attr
  }

  def mapAttribute(attrs: LangAttribute, context: Context): LangAttribute = {
    attrs.attr = mapOptID(attrs.attr, context)
    if (attrs.`type`.isDefined) attrs.`type` = Some(mapType(attrs.`type`.get, context))
    attrs.value = mapOperation(attrs.value, context)
    attrs
  }

  def mapCallVar(variable: LangCallVar, context: Context): LangCallVar = {
    variable.name = mapID(variable.name, context)
    variable
  }

  def mapValueType(value: LangValueType[_], context: Context): LangValueType[_] = {
    value match {
      case multi: LangMultiValue => mapMultiValue(multi, context)
      case primitive: LangPrimitiveValue[_] => mapPrimitive(primitive, context)
      case call: LangCallObj => mapCallObj(call, context)
    }
  }

  def mapOperation(op: LangOperation, context: Context): LangOperation = {
    op.content match {
      case Left(subOp) => op.content = Left(mapOperation(subOp, context))
      case Right(expr) => op.content = Right(mapExpression(expr, context))
    }
    op.next = if (op.next.isDefined) Some(op.next.get._1, mapOperation(op.next.get._2, context)) else None
    op
  }

  def mapMultiValue(multi: LangMultiValue, context: Context): LangMultiValue = {
    multi.values = multi.values.map(mapValueType(_, context))
    multi
  }

  def mapPrimitive(primitive: LangPrimitiveValue[_], context: Context): LangPrimitiveValue[_] = {
    primitive match {
      case str: LangStringValue => LangStringValue(str.context, mapID(str.value, context))
      case text: LangTextValue => LangTextValue(text.context, mapID(text.value, context))
      case entityValue: LangEntityValue => mapEntityValue(entityValue, context)
      case array: LangArrayValue => mapArrayValue(array, context)
    }
  }

  def mapEntityValue(entity: LangEntityValue, context: Context): LangEntityValue = {
    entity.name = mapOptID(entity.name, context)
    entity.params = mapSetAttributes(entity.params.asInstanceOf[Option[List[LangSetAttribute]]], context)
    entity.attrs = mapSetAttributes(entity.attrs.asInstanceOf[Option[List[LangSetAttribute]]], context)
    entity
  }

  def mapArrayValue(array: LangArrayValue, context: Context): LangArrayValue = {
    if (array.`type`.isDefined) array.`type` = Some(mapType(array.`type`.get, context))
    array.params = mapSetAttributes(array.params.asInstanceOf[Option[List[LangSetAttribute]]], context)
    array
  }

  def mapVar(variable: LangVar, context: Context): LangVar = {
    variable.name = mapID(variable.name, context)
    variable.`type` = if (variable.`type`.isDefined) Some(mapType(variable.`type`.get, context)) else None
    variable.value = if (variable.value.isDefined) Some(mapOperation(variable.value.get, context)) else None
    variable
  }

  def mapTypes(types: Option[List[LangType]], context: Context): Option[List[LangType]] = {
    if (types.isDefined) Some(types.get.map(t => mapType(t, context)))
    else None
  }

  def mapType(`type`: LangType, context: Context): LangType = {
    `type`.name = mapID(`type`.name, context)
    `type`.generic = mapGeneric(`type`.generic, context)
    `type`
  }

  def mapGeneric(gen: Option[LangGeneric], context: Context): Option[LangGeneric] = {
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
            case block: LangBlockAsValue => TmplStringID(interId.context, interId.pre.getOrElse("") + Generator.generate(block, context) + interId.post.getOrElse(""))
            case _ => TmplStringID(interId.context, interId.pre.getOrElse("") + value.get.head.toString + interId.post.getOrElse(""))
          }
        } else TmplStringID(interId.context, "Undefined")
      }
      case replacedId: TmplReplacedId => TmplStringID(replacedId.context, replacedId.pre.getOrElse("") + replacedId.node.toString + replacedId.post.getOrElse(""))
      case str: TmplStringID => TmplStringID(str.context, str.id)
      case block: LangBlockID => TmplStringID(block.context, "Undefined")
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
