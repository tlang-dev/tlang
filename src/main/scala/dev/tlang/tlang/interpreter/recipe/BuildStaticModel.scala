package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.ast.model.set._
import dev.tlang.tlang.interpreter.context.JumpIndex
import dev.tlang.tlang.interpreter.instruction.{EndLabel, EndStaticBox, Label, Put, Set, StartStaticBox}
import tlang.core

object BuildStaticModel {

  def buildStaticModel(context: BuilderContext, model: ModelSetEntity): Unit = {
    val label = model.name.getType.toString
    val boxBuilder = new BoxBuilder()
    boxBuilder.setBoxId(label)
    val instructionBlock = context.section.newInstructionBlock(label)
    context.labels.addOne(label -> JumpIndex(context.sectionPos, context.instrPos))
    instructionBlock.addInstruction(StartStaticBox(label))
    model.attrs.foreach(_.zipWithIndex.foreach(attr => buildStaticModelAttr(context, boxBuilder, model, attr._1, attr._2)))
    instructionBlock.addInstruction(EndStaticBox(label))

    //Just to have something to return, could be a representation of the entity in the future
    instructionBlock.addInstruction(Set(Some(new core.String("This is a model"))))
    instructionBlock.addInstruction(Put())
  }

  def buildStaticModelAttr(context: BuilderContext, boxBuilder: BoxBuilder, model: ModelSetEntity, attr: ModelSetAttribute, index: Int): Unit = {
    val indexLabel = model.name.getType.toString + "." + index.toString
    val instructionBlock = context.section.newInstructionBlock(indexLabel)
    attr.attr.foreach(attr => {
      instructionBlock.addInstruction(Label(model.name.getType.toString + "." + attr))
//      context.labels.addOne(model.name.getType.toString -> JumpIndex(context.sectionPos, context.instrPos))
    })

    context.labels.addOne(model.name.getType.toString + "." + index.toString -> JumpIndex(context.sectionPos, context.instrPos))

    buildModelSetValueType(context, boxBuilder, attr.value)
    //    val callOnce = CallOnce(attr.value, JumpIndex(context.sectionPos, context.instrPos + 3), JumpIndex(context.sectionPos, context.instrPos))
    //    val lazyVar = boxBuilder.addVar("lazy" + index.toString)
    //    context.section.addInstruction(SetStatic(boxBuilder.getBoxId, Some(new Lazy())))
    //    context.section.addInstruction(callOnce)
    //    BuildProgram.buildOperation(context, attr.value)
    //    context.section.addInstruction(SetLazyStatic(boxBuilder.getBoxId, lazyVar.pos))
    //    callOnce.getIndex = JumpIndex(context.sectionPos, context.instrPos + 1)
    //    context.section.addInstruction(GetLazyStatic(boxBuilder.getBoxId, lazyVar.pos))

    attr.attr.foreach(attr => {
      instructionBlock.addInstruction(EndLabel(model.name.getType.toString + "." + attr))
      //      context.labels.addOne(model.name.getType.toString -> JumpIndex(context.sectionPos, context.instrPos))
    })
  }

  def buildModelSetValueType(context: BuilderContext, boxBuilder: BoxBuilder, model: ModelSetValueType[_]): Unit = {
    model match {
      case setType: ModelSetType =>
      case setArray: ModelSetArray =>
      case setFuncDef: ModelSetFuncDef =>
      case setRef: ModelSetRef =>
      case setImpl: ModelSetImpl =>
      case setImplArray: ModelSetImplArray =>
    }
  }

}
