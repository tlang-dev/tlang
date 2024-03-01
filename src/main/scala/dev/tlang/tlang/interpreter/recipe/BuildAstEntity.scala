package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.PrimitiveValue
import dev.tlang.tlang.interpreter.context.{EntityLabel, JumpIndex}
import dev.tlang.tlang.interpreter.instruction.{EndLabel, Label, Put, Set}
import dev.tlang.tlang.tmpl.{AstEntity, AstEntityAttr, AstListValue, AstValue}

object BuildAstEntity {

  def buildAstEntity(context: BuilderContext, entity: AstEntity): Unit = {
    val label = entity.getType.getType.toString
    val boxBuilder = new BoxBuilder()
    //    boxBuilder.setBoxId(label)
    context.section.addInstruction(Label(label))
    context.labels.addOne(label -> JumpIndex(context.sectionPos, context.instrPos))
    //    context.section.addInstruction(StartBox(label))
    entity.attrs.foreach(_.zipWithIndex.foreach(attr => buildAstEntityAttr(context, boxBuilder, entity, attr._1, attr._2)))
    //    context.section.addInstruction(EndStaticBox(label))

    //Just to have something to return, could be a representation of the entity in the future
    context.section.addInstruction(Set(Some(EntityLabel(entity.getType))))
    context.section.addInstruction(Put())
    context.section.addInstruction(EndLabel(label))
  }

  def buildAstEntityAttr(context: BuilderContext, boxBuilder: BoxBuilder, entity: AstEntity, attr: AstEntityAttr, index: Int): Unit = {
    attr.name.foreach(name => {
      context.section.addInstruction(Label(entity.getType.getType.toString + "." + name))
      context.labels.addOne(entity.getType.getType.toString -> JumpIndex(context.sectionPos, context.instrPos))
    })
    val indexLabel = entity.getType.getType.toString + "." + index.toString
    context.section.addInstruction(Label(indexLabel))
    context.labels.addOne(entity.getType.getType.toString + "." + index.toString -> JumpIndex(context.sectionPos, context.instrPos))
    buildValue(context, boxBuilder, attr.value.get)
    context.section.addInstruction(EndLabel(indexLabel))
  }

  def buildValue(context: BuilderContext, boxBuilder: BoxBuilder, value: AstValue): Unit = {
    value match {
      case operation: Operation => BuildProgram.buildOperation(context, operation)
      case primitive: PrimitiveValue[_] => BuildProgram.buildPrimitive(context, primitive)
      case entity: AstEntity => buildAstEntity(context, entity)
      case astList: AstListValue => println("[" + getClass.getName + "]AstList not yet implemented")
    }

  }

}
