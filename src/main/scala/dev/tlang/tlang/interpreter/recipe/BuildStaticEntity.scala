package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.ast.common.value.{ComplexAttribute, EntityValue}
import dev.tlang.tlang.interpreter.context.{EntityLabel, JumpIndex}
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
    BuildProgram.addLabel(context, label, context.instrPos)
    context.section.addInstruction(StartStaticBox(label))
    entity.attrs.foreach(_.zipWithIndex.foreach(attr => buildStaticEntityAttr(context, boxBuilder, label, attr._1, attr._2)))
    context.section.addInstruction(EndStaticBox(label))

    //Just to have something to return, could be a representation of the entity in the future
    context.section.addInstruction(Set(Some(InterEntity(entity.getType))))
    context.section.addInstruction(Put())
  }

  def buildStaticEntityAttr(context: BuilderContext, boxBuilder: BoxBuilder, entityLabel: String, attr: ComplexAttribute, index: Int): Unit = {
    attr.attr.foreach(attr => {
      val label = entityLabel + "/" + attr
      BuildProgram.addLabel(context, label, context.instrPos + 1)
      //      context.section.addInstruction(Label(label))
      //      context.labels.addOne(label -> JumpIndex(context.sectionPos, context.instrPos))
    })
    val indexLabel = entityLabel + "/" + index.toString
    context.section.addInstruction(Label(indexLabel))
    context.labels.addOne(entityLabel + "/" + index.toString -> JumpIndex(context.sectionPos, context.instrPos + 1))
    val callOnce = CallOnce(attr.value, JumpIndex(context.sectionPos, context.instrPos + 2), JumpIndex(context.sectionPos, context.instrPos))
    val lazyVar = boxBuilder.addVar("lazy" + index.toString)
    context.section.addInstruction(callOnce)
    BuildProgram.buildOperation(context, attr.value)(isStatic = false)
    context.section.addInstruction(instruction.SetStatic(boxBuilder.getBoxId, Some(new Lazy())))
    context.section.addInstruction(SetLazyStatic(boxBuilder.getBoxId, lazyVar.pos))
    callOnce.getIndex = JumpIndex(context.sectionPos, context.instrPos + 1)
    context.section.addInstruction(GetLazyStatic(boxBuilder.getBoxId, lazyVar.pos))
    context.section.addInstruction(EndLabel(indexLabel))
  }

}
