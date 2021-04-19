package dev.tlang.tlang.generator

import dev.tlang.tlang.ast.tmpl.{TmplNode, _}
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.interpreter.{ExecCallObject, ExecError, NoValue, Value}

import scala.collection.mutable.ListBuffer

object TemplateBuilder {

  def buildBlockAsValue(blockAsValue: TmplBlockAsValue): TmplBlockAsValue = {
    buildBlock(blockAsValue.block, blockAsValue.context)
    blockAsValue
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

  def buildContents(tmplContents: Option[List[TmplContent[_]]], context: Context): Either[ExecError, Option[List[TmplContent[_]]]] = {
    if (tmplContents.isDefined) {
      forEach(tmplContents.get, context) match {
        case Left(error) => Left(error)
        case Right(value) => Right(Some(value.asInstanceOf[List[TmplContent[_]]]))
      }
    } else Right(None)
  }

  def includeTmplId(tmplID: TmplID, context: Context): Either[ExecError, List[TmplNode[_]]] = {
    tmplID match {
      case inter: TmplInterpretedID => ExecCallObject.run(inter.call, context) match {
        case Left(error) => Left(error)
        case Right(value) => value match {
          case Some(value) =>
            if (value.length == 1) {
              goFurther(value(1)) match {
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
        if (valueBlock.block.`type`.isDefined) Right(valueBlock.block.content.get.head.asInstanceOf[TmplNode[_]])
        else Right(valueBlock.block)
      case value: Value[_] => Right(value.asInstanceOf[TmplNode[_]])
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
        case _ => newNodes.addOne _
      }
      i = i + 1
    }
    if (err.isDefined) Left(err.get)
    else Right(newNodes.toList)
  }

}
