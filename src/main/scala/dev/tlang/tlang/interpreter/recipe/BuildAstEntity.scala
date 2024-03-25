package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.PrimitiveValue
import dev.tlang.tlang.interpreter.context.JumpIndex
import dev.tlang.tlang.interpreter.instruction.{EndLabel, Label, Put, Set}
import dev.tlang.tlang.interpreter.value.InterEntity
import dev.tlang.tlang.tmpl.{AstEntity, AstEntityAttr, AstListValue, AstValue}

object BuildAstEntity {

  def buildAstEntity(context: BuilderContext, entity: AstEntity): Unit = {
    val label = entity.getType.getType.toString
    val boxBuilder = new BoxBuilder()
    //    boxBuilder.setBoxId(label)
    val instructionBlock = context.section.newInstructionBlock(label)
    context.labels.addOne(label -> JumpIndex(context.sectionPos, context.instrPos))
    //    context.section.addInstruction(StartBox(label))
    entity.attrs.foreach(_.zipWithIndex.foreach(attr => buildAstEntityAttr(context, boxBuilder, label, entity, attr._1, attr._2)))
    //    context.section.addInstruction(EndStaticBox(label))

    //Just to have something to return, could be a representation of the entity in the future
    instructionBlock.addInstruction(Set(Some(InterEntity(entity.getType))))
    instructionBlock.addInstruction(Put())
  }

  def buildAstEntityAttr(context: BuilderContext, boxBuilder: BoxBuilder, entityLabel: String, entity: AstEntity, attr: AstEntityAttr, index: Int): Unit = {
    val indexLabel = entity.getType.getType.toString + "." + index.toString
    val instructionBlock = context.section.newInstructionBlock(indexLabel)
    //    context.section.addInstruction(Label(indexLabel))
    attr.name.foreach(name => {
      val label = entityLabel + "/" + name
      instructionBlock.addInstruction(Label(label))
      //      BuildProgram.addLabel(context, label, context.instrPos + 1)
    })

    context.labels.addOne(entity.getType.getType.toString + "." + index.toString -> JumpIndex(context.sectionPos, context.instrPos))

    buildValue(context, boxBuilder, attr.value.get)

    // End Labels
    attr.name.foreach(attr => {
      val label = entityLabel + "/" + attr
      instructionBlock.addInstruction(EndLabel(label))
    })
  }

  private def buildValue(context: BuilderContext, boxBuilder: BoxBuilder, value: AstValue): Unit = {
    value match {
      case operation: Operation => BuildProgram.buildOperation(context, operation)
      case primitive: PrimitiveValue[_] => BuildProgram.buildPrimitive(context, primitive)
      case entity: AstEntity => buildAstEntity(context, entity)
      case astList: AstListValue => buildAstList(context, boxBuilder, astList)
    }

  }

  private def buildAstList(context: BuilderContext, boxBuilder: BoxBuilder, list: AstListValue): Unit = {
    list.values.foreach(buildValue(context, boxBuilder, _))
  }

}
