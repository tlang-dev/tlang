package dev.tlang.tlang.generator.builder

import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.ast.tmpl._
import dev.tlang.tlang.ast.tmpl.call.{TmplCallFunc, TmplCallObj, TmplCallObjectLink, TmplCurryParam}
import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.ast.tmpl.func.TmplFunc
import dev.tlang.tlang.ast.tmpl.primitive.{TmplArrayValue, TmplEntityValue, TmplStringValue, TmplTextValue}
import dev.tlang.tlang.generator.mapper.ValueMapper
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.interpreter.{ExecCallObject, ExecError, NoValue, Value}

import scala.collection.mutable.ListBuffer

object TemplateBuilder {

  def buildBlockAsValue(blockAsValue: TmplBlockAsValue): Either[ExecError, TmplBlockAsValue] = {
    buildBlock(blockAsValue.block, blockAsValue.context) match {
      case Left(error) => Left(error)
      case Right(newBLock) =>
        blockAsValue.block = newBLock
        ValueMapper.mapBlockAsValue(blockAsValue)
        Right(blockAsValue)
    }
  }

  def buildBlock(block: TmplBlock, context: Context): Either[ExecError, TmplBlock] = {
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

  def buildCallObject(callObject: TmplCallObj, context: Context): Either[ExecError, TmplCallObj] = {
    forEach(callObject.calls, context) match {
      case Left(error) => Left(error)
      case Right(values) =>
        callObject.calls = values.asInstanceOf[List[TmplCallObjectLink]]
        Right(callObject)
    }
  }

  def buildPkg(pkg: Option[TmplPkg], context: Context): Either[ExecError, Option[TmplPkg]] = {
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

  def buildCallFunc(callFunc: TmplCallFunc, context: Context): Either[ExecError, TmplCallFunc] = {
    if (callFunc.currying.isDefined) {
      buildCallCurryParams(callFunc.currying.get, context) match {
        case Left(error) => Left(error)
        case Right(value) =>
          callFunc.currying = Some(value)
          Right(callFunc)
      }
    } else Right(callFunc)
  }

  def buildCallCurryParams(curryParams: List[TmplCurryParam], context: Context): Either[ExecError, List[TmplCurryParam]] = {
    forEach(curryParams, context) match {
      case Left(error) => Left(error)
      case Right(value) => Right(value.asInstanceOf[List[TmplCurryParam]])
    }
  }

  def buildCallCurry(curry: TmplCurryParam, context: Context): Either[ExecError, TmplCurryParam] = {
    if (curry.params.isDefined) {
      forEach(curry.params.get, context) match {
        case Left(error) => Left(error)
        case Right(params) =>
          curry.params = Some(params.asInstanceOf[List[TmplSetAttribute]])
          Right(curry)
      }
    } else Right(curry)
  }

  def buildSetAttribute(setAttribute: TmplSetAttribute, context: Context): Either[ExecError, TmplSetAttribute] = {
    buildOperation(setAttribute.value, context) match {
      case Left(error) => Left(error)
      case Right(value) => setAttribute.value = value
        Right(setAttribute)
    }
  }

  def buildArray(array: TmplArrayValue, context: Context): Either[ExecError, TmplArrayValue] = {

    def buildParams(): Either[ExecError, TmplArrayValue] = {
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

  def buildType(tmplType: TmplType, context: Context): Either[ExecError, TmplType] = {
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

  def buildGeneric(gen: TmplGeneric, context: Context): Either[ExecError, TmplGeneric] = {
    forEach(gen.types, context) match {
      case Left(error) => Left(error)
      case Right(value) =>
        gen.types = value.asInstanceOf[List[TmplType]]
        Right(gen)
    }
  }

  def buildOperation(operation: TmplOperation, context: Context): Either[ExecError, TmplOperation] = {
    operation.content match {
      case Left(subOp) => buildOperation(subOp, context) match {
        case Left(error) => Left(error)
        case Right(value) => operation.content = Left(value)
          Right(operation)
      }
      case Right(expr) => visitNode(expr, context) match {
        case Left(error) => Left(error)
        case Right(value) => operation.content = Right(value.head.asInstanceOf[TmplExpression[_]])
          Right(operation)
      }
    }
  }

  def buildExpBlock(exprBlock: TmplExprBlock, context: Context): Either[ExecError, TmplExprBlock] = {
    forEach(exprBlock.exprs, context) match {
      case Left(error) => Left(error)
      case Right(value) => exprBlock.exprs = value.asInstanceOf[List[TmplExpression[_]]]
        Right(exprBlock)
    }
  }

  def buildExpContent(exprContent: TmplExprContent[_], context: Context): Either[ExecError, TmplExprContent[_]] = {
    exprContent match {
      case block: TmplExprBlock => buildExpBlock(block, context)
      case expr: TmplExpression[_] => visitNode(expr, context) match {
        case Left(error) => Left(error)
        case Right(value) => Right(value.head.asInstanceOf[TmplExprContent[_]])
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

  def buildValueType(value: TmplValueType[_], context: Context): Either[ExecError, TmplValueType[_]] = {
    value match {
      case strValue: TmplStringValue => buildStringValue(strValue, context)
      case strValue: TmplTextValue => buildTextValue(strValue, context)
      case _ => Right(value)
    }
  }

  def buildStringValue(str: TmplStringValue, context: Context): Either[ExecError, TmplStringValue] = {
    includeTmplId(str.value, context) match {
      case Left(error) => Left(error)
      case Right(value) =>
        str.value = value.head.asInstanceOf[TmplID]
        Right(str)
    }
  }

  def buildTextValue(str: TmplTextValue, context: Context): Either[ExecError, TmplTextValue] = {
    includeTmplId(str.value, context) match {
      case Left(error) => Left(error)
      case Right(value) =>
        str.value = value.head.asInstanceOf[TmplID]
        Right(str)
    }
  }

  def includeTmplInclude(tmplInclude: TmplInclude, context: Context): Either[ExecError, List[TmplNode[_]]] = {
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
      case inter: TmplInterpretedID => ExecCallObject.run(inter.call, context) match {
        case Left(error) => Left(error)
        case Right(value) => value match {
          case Some(value) =>
            if (value.length == 1) {
              buildValue(value.head) match {
                case Left(error) => Left(error)
                case Right(value) => Right(List(TmplReplacedId(tmplID.getContext, inter.pre, value, inter.post)))
              }
            } else buildValues(value)
          case None => Left(NoValue("No value returned", tmplID.getContext))
        }
      }
      case block: TmplBlockID => buildBlock(block.block, Context(List(block.block.scope))) match {
        case Left(error) => Left(error)
        case Right(value) => Right(List(value))
      }
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
      case valueBlock: TmplBlockAsValue =>
        buildBlockAsValue(valueBlock)
        if (valueBlock.block.specialised) Right(valueBlock.block.content.get.head)
        else Right(valueBlock.block)
      case value: Value[_] => Right(value.asInstanceOf[TmplNode[_]])
    }
  }

  def buildReturn(ret: TmplReturn, context: Context): Either[ExecError, TmplReturn] =
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
        case tmplInclude: TmplInclude => includeTmplInclude(tmplInclude, context) match {
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

  def buildAnnotation(param: TmplAnnotation, context: Context): Either[ExecError, TmplAnnotation] = {
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

  def buildVar(variable: TmplVar, context: Context): Either[ExecError, TmplVar] = {
    var err: Option[ExecError] = None
    if (variable.annots.isDefined) {
      val newAnnots = ListBuffer.empty[TmplAnnotation]
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
      case entity: TmplEntityValue => toList(EntityBuilder.buildEntity(entity, context))
      case impl: TmplImpl => toList(ImplBuilder.buildImpl(impl, context))
      case callFunc: TmplCallFunc => toList(buildCallFunc(callFunc, context))
      case curry: TmplCurryParam => toList(buildCallCurry(curry, context))
      case setAttr: TmplSetAttribute => toList(buildSetAttribute(setAttr, context))
      case callObject: TmplCallObj => toList(buildCallObject(callObject, context))
      case tmplType: TmplType => toList(buildType(tmplType, context))
      case generic: TmplGeneric => toList(buildGeneric(generic, context))
      case array: TmplArrayValue => toList(buildArray(array, context))
      case ret: TmplReturn => toList(buildReturn(ret, context))
      case func: TmplFunc => toList(FuncBuilder.buildFunc(func, context))
      case exprBlock: TmplExprBlock => toList(buildExpBlock(exprBlock, context))
      case stringValue: TmplStringValue => toList(buildStringValue(stringValue, context))
      case variable: TmplVar => toList(buildVar(variable, context))
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
