package dev.tlang.tlang.generator

import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.ast.tmpl.primitive.TmplEntityValue
import dev.tlang.tlang.ast.tmpl.{TmplNode, _}
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.interpreter.{ExecCallObject, ExecError, NoValue, Value}

import scala.collection.mutable.ListBuffer

object TemplateBuilder {

  def buildBlockAsValue(blockAsValue: TmplBlockAsValue): Either[ExecError, TmplBlockAsValue] = {
    ValueMapper.mapBlock(blockAsValue)
    buildBlock(blockAsValue.block, blockAsValue.context) match {
      case Left(error) => Left(error)
      case Right(_) => Right(blockAsValue)
    }
  }

  def buildBlock(block: TmplBlock, context: Context): Either[ExecError, TmplBlock] = {
    buildPkg(block.pkg, context) match {
      case Left(error) => Left(error)
      case Right(value) => block.pkg = value
        buildContents(block.content, context) match {
          case Left(error) => Left(error)
          case Right(value) => block.content = value
            Right(block)
        }
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

  def buildEntity(entity: TmplEntityValue, context: Context): Either[ExecError, TmplEntityValue] = {

    def params(): Either[ExecError, TmplEntityValue] = {
      if (entity.params.isDefined) {
        buildInclAttributes(entity.params, context) match {
          case Left(error) => Left(error)
          case Right(nodes) =>
            entity.params = nodes
            attrs()
        }
      } else attrs()
    }

    def attrs(): Either[ExecError, TmplEntityValue] = {
      if (entity.attrs.isDefined) {
        buildInclAttributes(entity.attrs, context) match {
          case Left(error) => Left(error)
          case Right(nodes) =>
            entity.attrs = nodes
            Right(entity)
        }
      } else Right(entity)
    }

    if (entity.name.isDefined) includeTmplId(entity.name.get, context) match {
      case Left(error) => Left(error)
      case Right(value) =>
        entity.name = Some(value.head.asInstanceOf[TmplID])
        params()
    } else params()

  }

  def buildInclAttributes(nodes: Option[List[TmplNode[_]]], context: Context): Either[ExecError, Option[List[TmplNode[_]]]] = {
    if (nodes.isDefined) optionalForEach(nodes.get, context)
    else Right(None)
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

  def callObject(callObject: CallObject, context: Context): Either[ExecError, List[TmplNode[_]]] = {
    ExecCallObject.run(callObject, context) match {
      case Left(error) => Left(error)
      case Right(value) => value match {
        case Some(value) =>
          if (value.length == 1) {
            goFurther(value.head) match {
              case Left(error) => Left(error)
              case Right(value) => Right(List(value))
            }
          } else goFurther(value)
        case None => Left(NoValue("No value returned", callObject.getContext))
      }
    }
  }

  def includeTmplId(tmplID: TmplID, context: Context): Either[ExecError, List[TmplNode[_]]] = {
    tmplID match {
      case inter: TmplInterpretedID => ExecCallObject.run(inter.call, context) match {
        case Left(error) => Left(error)
        case Right(value) => value match {
          case Some(value) =>
            if (value.length == 1) {
              goFurther(value.head) match {
                case Left(error) => Left(error)
                case Right(value) => Right(List(TmplReplacedId(tmplID.getContext, inter.pre, value, inter.post)))
              }
            } else goFurther(value)
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

  def goFurther(values: List[Value[_]]): Either[ExecError, List[TmplNode[_]]] = {
    var err: Option[ExecError] = None
    var i = 0
    val newNodes = ListBuffer.empty[TmplNode[_]]
    while (err.isEmpty && i < values.length) {
      goFurther(values(i)) match {
        case Left(error) => err = Some(error)
        case Right(value) => newNodes.addOne(value)
      }
      i = i + 1
    }
    if (err.isDefined) Left(err.get)
    else Right(newNodes.toList)
  }

  def goFurther(value: Value[_]): Either[ExecError, TmplNode[_]] = {
    value match {
      case valueBlock: TmplBlockAsValue =>
        buildBlockAsValue(valueBlock)
        if (valueBlock.block.specialised) Right(valueBlock.block.content.get.head)
        else Right(valueBlock.block)
      case value: Value[_] => Right(value.asInstanceOf[TmplNode[_]])
    }
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
        case node: TmplNode[_] => newNodes.addOne(node)
      }
      i = i + 1
    }
    if (err.isDefined) Left(err.get)
    else Right(newNodes.toList)
  }

}
