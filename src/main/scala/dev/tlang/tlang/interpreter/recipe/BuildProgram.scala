package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.ast.DomainModel
import dev.tlang.tlang.ast.common.call.{CallObject, ComplexValueStatement}
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value._
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.interpreter.instruction._
import dev.tlang.tlang.tmpl.lang.ast.LangBlock
import tlang.core
import tlang.internal.{AnyTmplBlock, DomainBlock}

object BuildProgram {

  def buildProgram(context: BuilderContext, domain: DomainModel): Unit = {
    context.program.addInstruction(Label(domain.getType.getType.toString))
    context.labels.addOne(domain.getType.getType.toString -> context.pos)
    context.program.addInstruction(StartBlock())
    buildBody(context, domain.body)
    context.program.addInstruction(EndBlock())
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
    context.program.addInstruction(Label(func.getType.getType.toString))
    context.labels.addOne(func.getType.getType.toString -> context.pos)
    context.program.addInstruction(StartBox())

    func.currying.foreach(_.foreach(_.params.foreach(_ => {
      context.program.addInstruction(Label(func.getType.getType.toString))
    })))

    buildContent(context, func.block)

    context.program.addInstruction(EndBox())
  }

  def buildContent(context: BuilderContext, content: HelperContent): Unit = {
    context.program.addInstruction(StartBlock())
    content.content.foreach(_.foreach(buildStatement(context, _)))
    context.program.addInstruction(EndBlock())
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
    context.program.addInstruction(Put())
  }

  def buildOperation(context: BuilderContext, operation: Operation): Unit = {
    operation.content match {
      case Left(op) => buildOperation(context, op)
      case Right(value) => buildComplexValue(context, value)
    }
    if (operation.next.isDefined) {
      context.program.addInstruction(Load())
      buildOperation(context, operation.next.get._2)
      context.program.addInstruction(Load())
      context.program.addInstruction(Put(popFromBox = true, pos = 1))
      context.program.addInstruction(Put(popFromBox = true))
      context.program.addInstruction(Comp(operation.next.get._1))
    }
    //    context.program.addInstruction(Set(Some(new core.String("This is a test"))))
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
    context.program.addInstruction(Set(Some(new core.Bool(bool.getElement.get()))))
    context.program.addInstruction(Put(true))
  }

  def buildString(context: BuilderContext, str: TLangString): Unit = {
    context.program.addInstruction(Set(Some(new core.String(str.getElement))))
    context.program.addInstruction(Put(true))
  }

  def buildLong(context: BuilderContext, long: TLangLong): Unit = {
    context.program.addInstruction(Set(Some(new core.Long(long.getElement.get()))))
    context.program.addInstruction(Put(true))
  }

  def buildDouble(context: BuilderContext, double: TLangDouble): Unit = {
    context.program.addInstruction(Set(Some(new core.Double(double.getElement))))
    context.program.addInstruction(Put(true))
  }

  def buildMultiValue(context: BuilderContext, multiValue: MultiValue): Unit = {

  }

  def buildLazyValue(context: BuilderContext, lazyValue: LazyValue[_]): Unit = {

  }

  def buildEntityImpl(context: BuilderContext, entityImpl: EntityImpl): Unit = {

  }

  def buildIf(context: BuilderContext, ifStatement: HelperIf): Unit = {
    buildOperation(context, ifStatement.condition)
    val ifInst = IfInstr(context.pos + 2, None)
    context.program.addInstruction(ifInst)
    ifStatement.ifTrue.foreach(_.content.foreach(_.foreach(buildStatement(context, _))))
    if (ifStatement.ifFalse.isDefined) {
      ifInst.jumpFalse = Some(context.pos + 2)
      ifStatement.ifFalse.foreach(_.content.foreach(_.foreach(buildStatement(context, _))))
    }
  }

  def buildFor(context: BuilderContext, forStatement: HelperFor): Unit = {
    //    context.program.addInstruction(ForInstruction(forStatement.condition, forStatement.content))
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
