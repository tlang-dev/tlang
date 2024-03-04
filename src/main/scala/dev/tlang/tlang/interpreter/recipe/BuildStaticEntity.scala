package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.ast.common.value.{ComplexAttribute, EntityValue}
import dev.tlang.tlang.interpreter.context.{EntityLabel, JumpIndex}
import dev.tlang.tlang.interpreter.instruction
import dev.tlang.tlang.interpreter.instruction._
import tlang.core.Lazy

object BuildStaticEntity {

  def buildStaticEntity(context: BuilderContext, entity: EntityValue): Unit = {
    val label = entity.getType.getType.toString
    val boxBuilder = new BoxBuilder()
    boxBuilder.setBoxId(label)
    //    context.section.addInstruction(Label(label))
    //    context.labels.addOne(label -> JumpIndex(context.sectionPos, context.instrPos))
    BuildProgram.addLabel(context, label, context.instrPos)
    context.section.addInstruction(StartStaticBox(label))
    entity.attrs.foreach(_.zipWithIndex.foreach(attr => buildStaticEntityAttr(context, boxBuilder, entity, attr._1, attr._2)))
    context.section.addInstruction(EndStaticBox(label))

    //Just to have something to return, could be a representation of the entity in the future
    context.section.addInstruction(Set(Some(EntityLabel(entity.getType))))
    context.section.addInstruction(Put())
  }

  def buildStaticEntityAttr(context: BuilderContext, boxBuilder: BoxBuilder, entity: EntityValue, attr: ComplexAttribute, index: Int): Unit = {
    attr.attr.foreach(attr => {
      context.section.addInstruction(Label(entity.getType.getType.toString + "." + attr))
      context.labels.addOne(entity.getType.getType.toString -> JumpIndex(context.sectionPos, context.instrPos))
    })
    val indexLabel = entity.getType.getType.toString + "." + index.toString
    context.section.addInstruction(Label(indexLabel))
    context.labels.addOne(entity.getType.getType.toString + "." + index.toString -> JumpIndex(context.sectionPos, context.instrPos))
    val callOnce = CallOnce(attr.value, JumpIndex(context.sectionPos, context.instrPos + 3), JumpIndex(context.sectionPos, context.instrPos))
    val lazyVar = boxBuilder.addVar("lazy" + index.toString)
    context.section.addInstruction(instruction.SetStatic(boxBuilder.getBoxId, Some(new Lazy())))
    context.section.addInstruction(callOnce)
    BuildProgram.buildOperation(context, attr.value)
    context.section.addInstruction(SetLazyStatic(boxBuilder.getBoxId, lazyVar.pos))
    callOnce.getIndex = JumpIndex(context.sectionPos, context.instrPos + 1)
    context.section.addInstruction(GetLazyStatic(boxBuilder.getBoxId, lazyVar.pos))
    context.section.addInstruction(EndLabel(indexLabel))
  }

}
