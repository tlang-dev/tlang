package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.interpreter.context.JumpIndex
import dev.tlang.tlang.interpreter.instruction._
import dev.tlang.tlang.interpreter.recipe.BuildProgram.{buildSetAttribute, getContentType}
import dev.tlang.tlang.interpreter.value._

object BuildCall {

  def buildCallObject(context: BuilderContext, callObject: CallObject): Unit = {
    findValue(context, callObject, 0)
  }

  private def findValue(context: BuilderContext, callObject: CallObject, callIndex: Int): Unit = {
    val currentCall = callObject.statements(callIndex)
    val name = currentCall.getName
    val fullPath = callObject.path.get + "/" + name
    val callable = findCallable(context, fullPath)
    if (callable.isDefined) applyValue(context, callable.get, callObject, callIndex)
  }

  private def applyNext(context: BuilderContext, callObject: CallObject, callIndex: Int): Unit = {
    val nextCall = callObject.statements(callIndex)
    nextCall match {
      case array: CallArrayObject => ???
      case func: CallFuncObject => applyNextFunc(context, func, callIndex)
      case refFunc: CallRefFuncObject => ???
      case callVar: CallVarObject => context.section.addInstruction(CallNextVar(callVar.name))
      case _ => println(getClass.getName + ": Does not exist")
    }
  }

  private def applyNextFunc(context: BuilderContext, callObject: CallFuncObject, callIndex: Int): Unit = {
    var totalArgs = 0
    callObject.currying.foreach(_.foreach(_.params.foreach(_.foreach(param => {
      BuildProgram.buildOperation(context, param.value)
      totalArgs += 1
    }))))
    context.section.addInstruction(CallNextFunc(callObject.getName, totalArgs))
  }

  private def applyValue(context: BuilderContext, value: InterValue, callObject: CallObject, callIndex: Int): Unit = {
    value match {
      case func: InterFunction => applyFunc(context, func, callObject, callIndex)
      case entity: InterEntity =>
      case jvm: InterJVM => BuildCallJVM.applyJVM(context, jvm, callObject, callIndex)
      //      case string: InterString => applySimpleValue(context, string, callObject, callIndex)
      //      case long: InterValue => applySimpleValue(context, long, callObject, callIndex)
      //      case double: InterDouble => applySimpleValue(context, double, callObject, callIndex)
      case model: InterModel =>
      case resource: InterResource =>
      case array: InterArray => applyArray(context, array, callObject, callIndex)
      case variable: InterVar => applyVar(context, variable, callObject, callIndex)
      case param: InterParam => applyParam(context, param, callObject, callIndex)
      case attr: InterAttr =>
    }
  }

  private def applyFunc(context: BuilderContext, value: InterFunction, callObject: CallObject, callIndex: Int): Unit = {
    context.section.addInstruction(GotoLabel(value.getFullName))
    context.section.addInstruction(Back(JumpIndex(context.sectionPos, context.instrPos + 2)))
    if (callIndex < callObject.statements.size - 1) {
      context.section.addInstruction(RefFuncGet())
      findValue(context, callObject, callIndex + 1)
    }
  }

  // Local var in func
  private def applyVar(context: BuilderContext, value: InterVar, callObject: CallObject, callIndex: Int): Unit = {
    context.section.addInstruction(Get(value.pos))
    if (callIndex < callObject.statements.size - 1) {
      findValue(context, callObject, callIndex + 1)
    }
  }

  private def applyParam(context: BuilderContext, value: InterParam, callObject: CallObject, callIndex: Int): Unit = {
    context.section.addInstruction(Get(value.pos))
    if (callIndex < callObject.statements.size - 1) {
      findValue(context, callObject, callIndex + 1)
    }
  }

  private def applyArray(context: BuilderContext, value: InterArray, callObject: CallObject, callIndex: Int): Unit = {
    callObject.statements(callIndex) match {
      case callVar: CallVarObject =>
        context.section.addInstruction(GotoLabel(value.getFullName))
        context.section.addInstruction(Back(JumpIndex(context.sectionPos, context.instrPos + 2)))
      case callArray: CallArrayObject =>
        context.section.addInstruction(GotoLabel(value.getFullName + "/" + callArray.name))
        context.section.addInstruction(Back(JumpIndex(context.sectionPos, context.instrPos + 2)))
    }
    if (callIndex < callObject.statements.size - 1) {
      findValue(context, callObject, callIndex + 1)
    }
  }

  //  private def findUse(context: BuilderContext, name: String): Option[DomainUse] = {
  //    if (context.resource.ast.header.isDefined && context.resource.ast.header.get.uses.isDefined) {
  //      val uses = context.resource.ast.header.get.uses.get
  //      uses.find(part => part.parts.last == name)
  //    }
  //    else None
  //  }

  def buildCallObjectChained(context: BuilderContext, callObject: CallObject): Unit = {
    val head = callObject.statements.head
    if (head.isInstanceOf[CallVarObject]) {
      val callVar = head.asInstanceOf[CallVarObject]
      val callable = findCallable(context, callVar.name)
      if (callable.isDefined) {
        //        context.section.addInstruction(GotoLabel(callable.get))
        context.section.addInstruction(Back(JumpIndex(context.sectionPos, context.instrPos + 2)))
      } else {
        val use = context.resource.ast.header.get.uses.get.filter(part => part.parts.last == callVar.name)
        val optClazz = {
          if (use.isEmpty) TLangModuleList.getClass(callVar.name)
          else TLangModuleList.getClass(callVar.name, Some(use.head.parts.head))
        }
        if (optClazz.isDefined) {
          val clazz = optClazz.get
          val callFunc = callObject.statements(1).asInstanceOf[CallFuncObject]

          var totParam = 0
          callFunc.currying.foreach(_.foreach(_.params.foreach(params => params.foreach(attr => {
            buildSetAttribute(context, attr)
            totParam += 1
          }))))
          context.section.addInstruction(CallCore(clazz.className, callFunc.name.get, totParam))
        }
      }
    }
  }

  def buildCallFunc(context: BuilderContext, func: CallFuncObject, hasOtherCallAfterwards: Boolean = false): Unit = {
    //func.currying.foreach(_.foreach(_.params.foreach(_.foreach(buildOperation(context, _)))))
    context.section.addInstruction(GotoLabel(getContentType(func.context, func.name)))
    context.section.addInstruction(Back(JumpIndex(context.sectionPos, context.instrPos + 2)))
    if (!hasOtherCallAfterwards) context.section.addInstruction(RefFuncGet())
  }

  private def findCallable(context: BuilderContext, name: String): Option[InterValue] = {
    context.callables.get(name) match {
      case Some(value) => Some(value)
      case None => context.callables.get(name) match {
        case Some(value) => Some(value)
        case None => None
      }
    }
  }
}