package dev.tlang.tlang.generator.builder

import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.generator.mapper.ValueMapper
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.interpreter.{ExecCallObject, ExecError, NoValue}
import dev.tlang.tlang.tmpl.AnyTmplInterpretedBlock
import dev.tlang.tlang.tmpl.doc.ast.DocBlock
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.lang.ast.call._
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.func.LangFunc
import dev.tlang.tlang.tmpl.lang.ast.primitive.{LangArrayValue, LangEntityValue, LangStringValue, LangTextValue}
import tlang.core.Value
import tlang.internal._

import scala.collection.mutable.ListBuffer

object TemplateBuilder {

  def buildBlockAsValue(blockAsValue: LangBlockAsValue): Either[ExecError, LangBlockAsValue] = {
    buildBlock(blockAsValue.block, blockAsValue.context) match {
      case Left(error) => Left(error)
      case Right(newBLock) =>
        blockAsValue.block = newBLock
        ValueMapper.mapBlockAsValue(blockAsValue)
        Right(blockAsValue)
    }
  }

  def buildBlock(block: AnyTmplInterpretedBlock[_], context: Context): Either[ExecError, AnyTmplInterpretedBlock[_]] = {
    block match {
      case doc: DocBlock => buildDocBlock(doc, context)
//      case lang: LangBlock => buildLangBlock(lang, context)
      case _ => println("TemplateBuilder: TmplBlock type not implemented")
        Right(block)
    }
  }

  def buildLangBlock(block: LangBlock, context: Context): Either[ExecError, LangBlock] = {
    buildFullBlock(block.content, context) match {
      case Left(error) => Left(error)
      case Right(value) =>
        block.content = value
        Right(block)
    }
  }

  def buildDocBlock(block: DocBlock, context: Context): Either[ExecError, DocBlock] = {
    Right(block)
  }

  def buildFullBlock(block: LangFullBlock, context: Context): Either[ExecError, LangFullBlock] = {
    buildPkg(block.pkg, context) match {
      case Left(error) => Left(error)
      case Right(value) =>
        block.pkg = value
        buildContents(block.content, context) match {
          case Left(error) => Left(error)
          case Right(value) => block.content = value
            Right(block)
        }
    }
  }

  def buildCallObject(callObject: LangCallObj, context: Context): Either[ExecError, LangCallObj] = {
    forEach(callObject.calls, context) match {
      case Left(error) => Left(error)
      case Right(values) =>
        callObject.calls = values.asInstanceOf[List[LangCallObjectLink]]
        Right(callObject)
    }
  }

  def buildPkg(pkg: Option[LangPkg], context: Context): Either[ExecError, Option[LangPkg]] = {
    if (pkg.isDefined) {
      forEach(pkg.get.parts, context) match {
        case Left(error) => Left(error)
        case Right(value) => pkg.get.parts = value.asInstanceOf[List[TmplID]]
          Right(pkg)
      }
    } else Right(None)
  }

  def buildContents(tmplContents: Option[List[TmplNode[_]]], context: Context): Either[ExecError, Option[List[TmplNode[_]]]] = {
    if (tmplContents.isDefined) optionalForEach(tmplContents.get, context)
    else Right(None)
  }

  def buildInclAttributes(nodes: Option[List[TmplNode[_]]], context: Context): Either[ExecError, Option[List[TmplNode[_]]]] = {
    if (nodes.isDefined) optionalForEach(nodes.get, context)
    else Right(None)
  }

  def callObject(callObject: CallObject, context: Context): Either[ExecError, List[TmplNode[_]]] = {
    ExecCallObject.run(callObject, context) match {
      case Left(error) => Left(error)
      case Right(value) => value match {
        case Some(value) =>
          if (value.length == 1) {
            buildValue(value.head) match {
              case Left(error) => Left(error)
              case Right(value) => Right(List(value))
            }
          } else buildValues(value)
        case None => Left(NoValue("No value returned", callObject.getContext))
      }
    }
  }

  /* def buildCallFunc(callFunc: TmplCallFunc, context: Context): Either[ExecError, TmplCallFunc] = {
     if (callFunc.currying.isDefined) {
       buildCallCurryParams(callFunc.currying.get, context) match {
         case Left(error) => Left(error)
         case Right(value) =>
           callFunc.currying = Some(value)
           Right(callFunc)
       }
     } else Right(callFunc)
   }*/

  /*def buildCallCurryParams(curryParams: List[TmplCurryParam], context: Context): Either[ExecError, List[TmplCurryParam]] = {
    forEach(curryParams, context) match {
      case Left(error) => Left(error)
      case Right(value) => Right(value.asInstanceOf[List[TmplCurryParam]])
    }
  }

  def buildCallCurry(curry: TmplCurryParam, context: Context): Either[ExecError, TmplCurryParam] = {
    buildCallCurryParamType(curry.params, context) match {
      case Left(error) => Left(error)
      case Right(value) => curry.params = value
        Right(curry)
    }
  }*/

  def buildCallCurryParamType(params: List[LangCallFuncParam], context: Context): Either[ExecError, List[LangCallFuncParam]] = {
    forEach(params, context) match {
      case Left(error) => Left(error)
      case Right(params) =>
        Right(params.asInstanceOf[List[LangCallFuncParam]])
    }
  }

  def buildSetAttribute(setAttribute: LangSetAttribute, context: Context): Either[ExecError, LangSetAttribute] = {
    buildOperation(setAttribute.value, context) match {
      case Left(error) => Left(error)
      case Right(value) => setAttribute.value = value
        Right(setAttribute)
    }
  }

  def buildArray(array: LangArrayValue, context: Context): Either[ExecError, LangArrayValue] = {

    def buildParams(): Either[ExecError, LangArrayValue] = {
      if (array.params.isDefined) forEach(array.params.get, context) match {
        case Left(error) => Left(error)
        case Right(value) => array.params = Some(value)
          Right(array)
      }
      else Right(array)
    }

    if (array.`type`.isDefined) {
      buildType(array.`type`.get, context) match {
        case Left(error) => Left(error)
        case Right(value) => array.`type` = Some(value)
          buildParams()
      }
    } else buildParams()
  }

  def buildType(tmplType: LangType, context: Context): Either[ExecError, LangType] = {
    includeTmplId(tmplType.name, context) match {
      case Left(error) => Left(error)
      case Right(name) =>
        tmplType.name = name.head.asInstanceOf[TmplID]
        if (tmplType.generic.isDefined) {
          buildGeneric(tmplType.generic.get, context) match {
            case Left(error) => Left(error)
            case Right(value) =>
              tmplType.generic = Some(value)
              Right(tmplType)
          }
        } else Right(tmplType)
    }
  }

  def buildGeneric(gen: LangGeneric, context: Context): Either[ExecError, LangGeneric] = {
    forEach(gen.types, context) match {
      case Left(error) => Left(error)
      case Right(value) =>
        gen.types = value.asInstanceOf[List[LangType]]
        Right(gen)
    }
  }

  def buildOperation(operation: LangOperation, context: Context): Either[ExecError, LangOperation] = {
    operation.content match {
      case Left(subOp) => buildOperation(subOp, context) match {
        case Left(error) => Left(error)
        case Right(value) => operation.content = Left(value)
          Right(operation)
      }
      case Right(expr) => visitNode(expr, context) match {
        case Left(error) => Left(error)
        case Right(value) => operation.content = Right(value.head.asInstanceOf[LangExpression[_]])
          Right(operation)
      }
    }
  }

  def buildExpBlock(exprBlock: LangExprBlock, context: Context): Either[ExecError, LangExprBlock] = {
    forEach(exprBlock.exprs, context) match {
      case Left(error) => Left(error)
      case Right(value) => exprBlock.exprs = value.asInstanceOf[List[LangExpression[_]]]
        Right(exprBlock)
    }
  }

  def buildExpContent(exprContent: LangExprContent[_], context: Context): Either[ExecError, LangExprContent[_]] = {
    exprContent match {
      case block: LangExprBlock => buildExpBlock(block, context)
      case expr: LangExpression[_] => visitNode(expr, context) match {
        case Left(error) => Left(error)
        case Right(value) => Right(value.head.asInstanceOf[LangExprContent[_]])
      }
    }
  }

  /*def buildExpression(expr: TmplExpression[_], context: Context): Either[ExecError, List[TmplExpression[_]]] = {
    //    expr match {
    //      case callObject: TmplCallObj => toList[TmplExpression[_]](buildCallObject(callObject, context))
    //      case value: TmplValueType[_] => toList[TmplExpression[_]](buildValueType(value, context))
    //      case incl: TmplInclude => includeTmplInclude(incl, context) match {
    //        case Left(error) => Left(error)
    //        case Right(value) => Right(List(value.asInstanceOf[TmplExpression[_]]))
    //      }
    //      case func: TmplFunc => toList[TmplExpression[_]](FuncBuilder.buildFunc(func, context))
    //      case ret: TmplReturn => toList[TmplExpression[_]](buildReturn(ret, context))
    //      case _: TmplNode[_] => Right(List(expr))
    //    }
    visitNode(expr, context) match {
      case Left(value) => Left(value)
      case Right(value) => Right(value.asInstanceOf[List[TmplExpression[_]]])
    }
  }*/

  def buildValueType(value: LangValueType[_], context: Context): Either[ExecError, LangValueType[_]] = {
    value match {
      case strValue: LangStringValue => buildStringValue(strValue, context)
      case strValue: LangTextValue => buildTextValue(strValue, context)
      case _ => Right(value)
    }
  }

  def buildStringValue(str: LangStringValue, context: Context): Either[ExecError, LangStringValue] = {
    includeTmplId(str.value, context) match {
      case Left(error) => Left(error)
      case Right(value) =>
        str.value = value.head.asInstanceOf[TmplID]
        Right(str)
    }
  }

  def buildTextValue(str: LangTextValue, context: Context): Either[ExecError, LangTextValue] = {
    includeTmplId(str.value, context) match {
      case Left(error) => Left(error)
      case Right(value) =>
        str.value = value.head.asInstanceOf[TmplID]
        Right(str)
    }
  }

  def includeTmplInclude(tmplInclude: LangInclude, context: Context): Either[ExecError, List[TmplNode[_]]] = {
    var err: Option[ExecError] = None
    var i = 0
    val newNodes = ListBuffer.empty[TmplNode[_]]
    while (err.isEmpty && i < tmplInclude.calls.length) {
      callObject(tmplInclude.calls(i), context) match {
        case Left(error) => err = Some(error)
        case Right(nodes) => newNodes.addAll(nodes)
      }
      i = i + 1
    }
    if (err.isDefined) Left(err.get)
    else Right(newNodes.toList)
  }

  def includeTmplId(tmplID: TmplID, context: Context): Either[ExecError, List[TmplNode[_]]] = {
    tmplID match {
//      case inter: TmplInterpretedId => ExecCallObject.run(inter.call, context) match {
//        case Left(error) => Left(error)
//        case Right(value) => value match {
//          case Some(value) =>
//            if (value.length == 1) {
//              buildValue(value.head) match {
//                case Left(error) => Left(error)
//                case Right(value) => Right(List(new TmplReplacedId(tmplID.getContext, inter.getPre, value, inter.getPost)))
//              }
//            } else buildValues(value)
//          case None => Left(NoValue("No value returned", tmplID.getContext))
//        }
//      }
//      case block: TmplBlockId => buildBlock(block.getBlock, Context(List(block.getBlock.getScope))) match {
//        case Left(error) => Left(error)
//        case Right(value) => Right(List(value))
//      }
      case _ => Right(List(tmplID))
    }
  }

  def buildValues(values: List[Value[_]]): Either[ExecError, List[TmplNode[_]]] = {
    var err: Option[ExecError] = None
    var i = 0
    val newNodes = ListBuffer.empty[TmplNode[_]]
    while (err.isEmpty && i < values.length) {
      buildValue(values(i)) match {
        case Left(error) => err = Some(error)
        case Right(value) => newNodes.addOne(value)
      }
      i = i + 1
    }
    if (err.isDefined) Left(err.get)
    else Right(newNodes.toList)
  }

  def buildValue(value: Value[_]): Either[ExecError, TmplNode[_]] = {
    value match {
      case valueBlock: LangBlockAsValue =>
        buildBlockAsValue(valueBlock)
      case value: Value[_] => Right(value.asInstanceOf[TmplNode[_]])
    }
  }

  def buildReturn(ret: LangReturn, context: Context): Either[ExecError, LangReturn] =
    buildOperation(ret.operation, context) match {
      case Left(error) => Left(error)
      case Right(value) => ret.operation = value
        Right(ret)
    }

  def optionalForEach(nodes: List[TmplNode[_]], context: Context): Either[ExecError, Option[List[TmplNode[_]]]] = {
    forEach(nodes, context) match {
      case Left(err) => Left(err)
      case Right(nodes) => Right(Some(nodes))
    }
  }

  def forEach(nodes: List[TmplNode[_]], context: Context): Either[ExecError, List[TmplNode[_]]] = {
    var err: Option[ExecError] = None
    var i = 0
    val newNodes = ListBuffer.empty[TmplNode[_]]
    while (err.isEmpty && i < nodes.length) {
      nodes(i) match {
        case tmplID: TmplID => includeTmplId(tmplID, context) match {
          case Left(error) => err = Some(error)
          case Right(values) => newNodes.addAll(values)
        }
        case tmplInclude: LangInclude => includeTmplInclude(tmplInclude, context) match {
          case Left(error) => err = Some(error)
          case Right(values) => newNodes.addAll(values)
        }
        case node: TmplNode[_] =>
          visitNode(node, context) match {
            case Left(error) => err = Some(error)
            case Right(visitedNode) => newNodes.addAll(visitedNode)
          }
      }
      i = i + 1
    }
    if (err.isDefined) Left(err.get)
    else Right(newNodes.toList)
  }

  def buildAnnotation(param: LangAnnotation, context: Context): Either[ExecError, LangAnnotation] = {
    var err: Option[ExecError] = None
    if (param.values.isDefined) {
      //      val result = ListBuffer.empty[TmplValueType[_]]

      param.values.get.foreach {
        param =>
          buildValueType(param.value, context) match {
            case Left(error) => err = Some(error)
            case Right(value) => param.value = value
          }
      }
      //      param.values = Some(result.toList)
    }

    if (err.isDefined) Left(err.get)
    else Right(param)
  }

  def buildVar(variable: LangVar, context: Context): Either[ExecError, LangVar] = {
    var err: Option[ExecError] = None
    if (variable.annots.isDefined) {
      val newAnnots = ListBuffer.empty[LangAnnotation]
      variable.annots.get.foreach(annot => TemplateBuilder.buildAnnotation(annot, context) match {
        case Left(error) => err = Some(error)
        case Right(value) => newAnnots += value
      })
      variable.annots = Some(newAnnots.toList)
    }
    if (err.isDefined) Left(err.get)
    else Right(variable)
  }

  def visitNodes(nodes: List[TmplNode[_]], context: Context): Either[ExecError, List[TmplNode[_]]] = {
    var err: Option[ExecError] = None
    var i = 0
    val visitedNodes = ListBuffer.empty[TmplNode[_]]
    while (err.isEmpty && i < nodes.length) {
      visitNode(nodes(i), context) match {
        case Left(error) => err = Some(error)
        case Right(node) => visitedNodes.addAll(node)
      }
      i = i + 1
    }
    if (err.isDefined) Left(err.get)
    else Right(visitedNodes.toList)
  }

  def visitNode(node: TmplNode[_], context: Context): Either[ExecError, List[TmplNode[_]]] = {
    node match {
      case entity: LangEntityValue => toList(EntityBuilder.buildEntity(entity, context))
      case impl: LangImpl => toList(ImplBuilder.buildImpl(impl, context))
      //      case callFunc: TmplCallFunc => toList(buildCallFunc(callFunc, context))
      //      case curry: TmplCurryParam => toList(buildCallCurry(curry, context))
      case setAttr: LangSetAttribute => toList(buildSetAttribute(setAttr, context))
      case callObject: LangCallObj => toList(buildCallObject(callObject, context))
      case tmplType: LangType => toList(buildType(tmplType, context))
      case generic: LangGeneric => toList(buildGeneric(generic, context))
      case array: LangArrayValue => toList(buildArray(array, context))
      case ret: LangReturn => toList(buildReturn(ret, context))
      case func: LangFunc => toList(FuncBuilder.buildFunc(func, context))
      case exprBlock: LangExprBlock => toList(buildExpBlock(exprBlock, context))
      case stringValue: LangStringValue => toList(buildStringValue(stringValue, context))
      case variable: LangVar => toList(buildVar(variable, context))
      //case expr: TmplExpression[_] => buildExpression(expr, context)
      case _: TmplNode[_] => Right(List(node))
    }
  }

  def toList[TYPE](either: Either[ExecError, TmplNode[_]]): Either[ExecError, List[TYPE]] = {
    either match {
      case Left(error) => Left(error)
      case Right(value) => Right(List(value.asInstanceOf[TYPE]))
    }
  }

}
