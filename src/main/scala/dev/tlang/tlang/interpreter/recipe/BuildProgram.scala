package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.ast.DomainModel
import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallObject, ComplexValueStatement}
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value._
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.interpreter.context.LabelIndex
import dev.tlang.tlang.interpreter.instruction._
import dev.tlang.tlang.tmpl.lang.ast.LangBlock
import tlang.core
import tlang.internal.{AnyTmplBlock, DomainBlock}

object BuildProgram {

  def buildProgram(context: BuilderContext, domain: DomainModel): Unit = {
    context.program.addSection(context.section)
    context.section.addInstruction(Label(domain.getType.getType.toString))
    context.labels.addOne(domain.getType.getType.toString -> LabelIndex(context.sectionPos, context.instrPos))
    context.section.addInstruction(StartBlock())
    buildBody(context, domain.body)
    context.section.addInstruction(EndBlock())
  }

  def buildBody(context: BuilderContext, bodies: List[DomainBlock]): Unit = {
    bodies.foreach {
      case model: ModelBlock => buildModel(context, model)
      case helper: HelperBlock => buildHelper(context, helper)
      case tmpl: AnyTmplBlock[_] => buildTmpl(context, tmpl)
    }
  }

  def buildModel(context: BuilderContext, model: ModelBlock): Unit = {

  }

  def buildHelper(context: BuilderContext, helper: HelperBlock): Unit = {
    helper.funcs.foreach(_.foreach(func => buildFunc(context, func)))
  }

  def buildFunc(context: BuilderContext, func: HelperFunc): Unit = {
    context.section.addInstruction(Label(func.getType.getType.toString))
    val name = func.getType.getType.toString
    context.labels.addOne(func.getType.getType.toString -> LabelIndex(context.sectionPos, context.instrPos))
    context.section.addInstruction(StartBox())

    func.currying.foreach(_.foreach(_.params.foreach(_ => {
      context.section.addInstruction(Label(func.getType.getType.toString))
    })))

    buildContent(context, func.block)

    context.section.addInstruction(EndBox())
  }

  def buildContent(context: BuilderContext, content: HelperContent): Unit = {
    context.section.addInstruction(StartBlock())
    content.content.foreach(_.foreach(buildStatement(context, _)))
    context.section.addInstruction(EndBlock())
  }

  def buildStatement(context: BuilderContext, statement: HelperStatement): Unit = {
    statement match {
      case func: HelperFunc => buildFunc(context, func)
      case assign: AssignVar => buildAssignVar(context, assign)
      case operation: Operation => buildOperation(context, operation)
      case ifStatement: HelperIf => buildIf(context, ifStatement)
      case forStatement: HelperFor => buildFor(context, forStatement)
      case _ =>
    }
  }

  def buildAssignVar(context: BuilderContext, assign: AssignVar): Unit = {
    buildOperation(context, assign.value)
    context.section.addInstruction(Put())
  }

  def buildOperation(context: BuilderContext, operation: Operation): Unit = {
    operation.content match {
      case Left(op) => buildOperation(context, op)
      case Right(value) => buildComplexValue(context, value)
    }
    if (operation.next.isDefined) {
      context.section.addInstruction(Load())
      buildOperation(context, operation.next.get._2)
      context.section.addInstruction(Load())
      context.section.addInstruction(Put(popFromBox = true, pos = 1))
      context.section.addInstruction(Put(popFromBox = true))
      context.section.addInstruction(Comp(operation.next.get._1))
    }
    //    context.section.addInstruction(Set(Some(new core.String("This is a test"))))
  }

  def buildComplexValue(context: BuilderContext, value: ComplexValueStatement[_]): Unit = {
    value match {
      case callObj: CallObject => buildCallObject(context, callObj)
      case primitive: PrimitiveValue[_] => buildPrimitive(context, primitive)
      case multiValue: MultiValue => buildMultiValue(context, multiValue)
      case lazyVal: LazyValue[_] => buildLazyValue(context, lazyVal)
      case impl: EntityImpl => buildEntityImpl(context, impl)
      //      case either: Either[_, _] => buildEither(context, either)
    }
  }

  def buildCallObject(context: BuilderContext, callObject: CallObject): Unit = {
    callObject.statements.head match {
      case call: CallObject => buildCallObject(context, call)
      case func: CallFuncObject => buildCallFunc(context, func)
    }
  }

  def buildCallFunc(context: BuilderContext, func: CallFuncObject): Unit = {
    //func.currying.foreach(_.foreach(_.params.foreach(_.foreach(buildOperation(context, _)))))
    context.section.addInstruction(Jump(context.labels(func.name.get)))
    context.section.addInstruction(Back(LabelIndex(context.sectionPos, context.instrPos + 2)))
  }

  def buildPrimitive(context: BuilderContext, primitive: PrimitiveValue[_]): Unit = {
    primitive match {
      case bool: TLangBool => buildBool(context, bool)
      case str: TLangString => buildString(context, str)
      case long: TLangLong => buildLong(context, long)
      case double: TLangDouble => buildDouble(context, double)
    }
  }

  def buildBool(context: BuilderContext, bool: TLangBool): Unit = {
    context.section.addInstruction(Set(Some(new core.Bool(bool.getElement.get()))))
    context.section.addInstruction(Put(popFromBox = true))
  }

  def buildString(context: BuilderContext, str: TLangString): Unit = {
    context.section.addInstruction(Set(Some(new core.String(str.getElement))))
    context.section.addInstruction(Put(popFromBox = true))
  }

  def buildLong(context: BuilderContext, long: TLangLong): Unit = {
    context.section.addInstruction(Set(Some(new core.Long(long.getElement.get()))))
    context.section.addInstruction(Put(popFromBox = true))
  }

  def buildDouble(context: BuilderContext, double: TLangDouble): Unit = {
    context.section.addInstruction(Set(Some(new core.Double(double.getElement))))
    context.section.addInstruction(Put(popFromBox = true))
  }

  def buildMultiValue(context: BuilderContext, multiValue: MultiValue): Unit = {

  }

  def buildLazyValue(context: BuilderContext, lazyValue: LazyValue[_]): Unit = {

  }

  def buildEntityImpl(context: BuilderContext, entityImpl: EntityImpl): Unit = {

  }

  def buildIf(context: BuilderContext, ifStatement: HelperIf): Unit = {
    buildOperation(context, ifStatement.condition)
    val ifInst = IfInstr(LabelIndex(context.sectionPos, context.instrPos + 2), None)
    context.section.addInstruction(ifInst)
    ifStatement.ifTrue.foreach(_.content.foreach(_.foreach(buildStatement(context, _))))
    if (ifStatement.ifFalse.isDefined) {
      ifInst.jumpFalse = Some(LabelIndex(context.sectionPos, context.instrPos + 2))
      ifStatement.ifFalse.foreach(_.content.foreach(_.foreach(buildStatement(context, _))))
    }
  }

  def buildFor(context: BuilderContext, forStatement: HelperFor): Unit = {
    //    context.section.addInstruction(ForInstruction(forStatement.condition, forStatement.content))
  }

  def buildTmpl(context: BuilderContext, tmpl: AnyTmplBlock[_]): Unit = {
    tmpl match {
      case block: LangBlock => buildLang(context, block)
      case _ =>
    }
  }

  def buildLang(context: BuilderContext, lang: LangBlock): Unit = {

  }
}
