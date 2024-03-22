package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.ast.common.value.{ComplexAttribute, EntityValue}
import dev.tlang.tlang.interpreter.context.JumpIndex
import dev.tlang.tlang.interpreter.instruction
import dev.tlang.tlang.interpreter.instruction._
import dev.tlang.tlang.interpreter.value.InterEntity
import tlang.core.Lazy

object BuildStaticEntity {

  def buildStaticEntity(context: BuilderContext, entity: EntityValue): Unit = {
    val entityName = entity.getType.getSimpleType.toString
    //    val label = BuildProgram.getContentType(entity.context) + "/" + entityName
    val label = BuildProgram.getContentType(entity.context) + "/" + entityName
    val boxBuilder = new BoxBuilder()
    boxBuilder.setBoxId(label)
    //    context.section.addInstruction(Label(label))
    //    context.labels.addOne(label -> JumpIndex(context.sectionPos, context.instrPos))
    //    BuildProgram.addLabel(context, label, context.instrPos)
    val instructionBlock = context.section.newInstructionBlock(label)
    instructionBlock.addInstruction(StartStaticBox(label))
    entity.attrs.foreach(_.zipWithIndex.foreach(attr => buildStaticEntityAttr(context, boxBuilder, label, attr._1, attr._2)))
    instructionBlock.addInstruction(EndStaticBox(label))

    instructionBlock.addInstruction(Set(Some(InterEntity(entity.getType))))
    instructionBlock.addInstruction(Put())
  }

  private def buildStaticEntityAttr(context: BuilderContext, boxBuilder: BoxBuilder, entityLabel: String, attr: ComplexAttribute, index: Int): Unit = {
    val indexLabel = entityLabel + "/" + index.toString
    val instructionBlock = context.section.newInstructionBlock(indexLabel)
    attr.attr.foreach(attr => {
      val label = entityLabel + "/" + attr
      instructionBlock.addInstruction(Label(label))
      //      context.labels.addOne(label -> JumpIndex(context.sectionPos, context.instrPos))
    })

    context.labels.addOne(entityLabel + "/" + index.toString -> JumpIndex(context.sectionPos, context.instrPos + 1))
    val callOnce = CallOnce(attr.value, JumpIndex(context.sectionPos, context.instrPos + 2), JumpIndex(context.sectionPos, context.instrPos))
    val lazyVar = boxBuilder.addVar("lazy" + index.toString)
    instructionBlock.addInstruction(callOnce)
    BuildProgram.buildOperation(context, attr.value)(isStatic = false)
    instructionBlock.addInstruction(instruction.SetStatic(boxBuilder.getBoxId, Some(new Lazy())))
    instructionBlock.addInstruction(SetLazyStatic(boxBuilder.getBoxId, lazyVar.pos))
    callOnce.getIndex = JumpIndex(context.sectionPos, context.instrPos + 1)
    instructionBlock.addInstruction(GetLazyStatic(boxBuilder.getBoxId, lazyVar.pos))

    // End Labels
    attr.attr.foreach(attr => {
      val label = entityLabel + "/" + attr
      instructionBlock.addInstruction(EndLabel(label))
    })
  }

}
