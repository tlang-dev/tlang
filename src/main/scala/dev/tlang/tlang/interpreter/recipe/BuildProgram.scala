package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.ast.DomainModel
import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallObject, CallVarObject, ComplexValueStatement}
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value._
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.interpreter.context.JumpIndex
import dev.tlang.tlang.interpreter.instruction
import dev.tlang.tlang.interpreter.instruction._
import dev.tlang.tlang.tmpl.lang.ast.LangBlock
import tlang.core
import tlang.core.Lazy
import tlang.core.func.FuncRet
import tlang.internal.{AnyTmplBlock, ContextContent, DomainBlock}

object BuildProgram {

  def buildProgram(context: BuilderContext, domain: DomainModel): Unit = {
    context.program.addSection(context.section)
    context.section.addInstruction(Label(domain.getType.getType.toString))
    context.labels.addOne(domain.getType.getType.toString -> JumpIndex(context.sectionPos, context.instrPos))
    context.section.addInstruction(StartBlock())
    buildBody(context, domain.body)
    context.section.addInstruction(EndBlock())
    context.program.setLabels(context.labels.toMap)
  }

  def buildBody(context: BuilderContext, bodies: List[DomainBlock]): Unit = {
    bodies.foreach {
      case model: ModelBlock => buildModel(context, model)
      case helper: HelperBlock => buildHelper(context, helper)
      case tmpl: AnyTmplBlock[_] => buildTmpl(context, tmpl)
    }
  }

  def buildModel(context: BuilderContext, model: ModelBlock): Unit = {
    val label = model.context.get.getValue.getResource.getPkg + "." + model.context.get.getValue.getResource.getName
    val boxBuilder = new BoxBuilder()
    boxBuilder.setBoxId(label)
    context.section.addInstruction(StartStaticBox(label))
    model.content.foreach(_.foreach {
      case assignVar: AssignVar => buildAssignVarInModel(context, boxBuilder, assignVar)
      case setEntity: ModelSetEntity => ???
      case _ =>
    })
    context.section.addInstruction(EndStaticBox(label))
  }

  def buildAssignVarInModel(context: BuilderContext, boxBuilder: BoxBuilder, assignVar: AssignVar): Unit = {
    implicit val isStatic: Boolean = true
    val label = getContentType(assignVar.context, Some(assignVar.name))
    context.section.addInstruction(Label(label))
    context.labels.addOne(label -> JumpIndex(context.sectionPos, context.instrPos))
    val callOnce = CallOnce(assignVar.value, JumpIndex(context.sectionPos, context.instrPos + 3), JumpIndex(context.sectionPos, context.instrPos + 3))
    val lazyVar = boxBuilder.addVar("lazy" + assignVar.name.capitalize)
    context.section.addInstruction(instruction.SetStatic(boxBuilder.getBoxId, Some(new Lazy())))
    context.section.addInstruction(callOnce)
    BuildProgram.buildOperation(context, assignVar.value)
    context.section.addInstruction(SetLazyStatic(boxBuilder.getBoxId, lazyVar.pos))
    callOnce.getIndex = JumpIndex(context.sectionPos, context.instrPos + 1)
    context.section.addInstruction(GetLazyStatic(boxBuilder.getBoxId, lazyVar.pos))
  }

  def buildHelper(context: BuilderContext, helper: HelperBlock): Unit = {
    helper.funcs.foreach(_.foreach(func => buildFunc(context, func)))
  }

  def buildFunc(context: BuilderContext, func: HelperFunc): Unit = {
    val label = func.getType.getType.toString
    context.section.addInstruction(Label(label))
    context.labels.addOne(label -> JumpIndex(context.sectionPos, context.instrPos))
    context.section.addInstruction(StartBox())

    func.currying.foreach(_.foreach(_.params.foreach(_ => {
      context.section.addInstruction(Label(func.getType.getType.toString))
    })))

    if (func.block.content.isDefined) {
      func.block.content.get.foreach(buildStatement(context, _))
      context.section.addInstruction(RefFuncSet())
    } else context.section.addInstruction(Set(Some(FuncRet.VOID)))

    context.section.addInstruction(Put(popFromBox = true))

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

  def buildOperation(context: BuilderContext, operation: Operation)(implicit isStatic: Boolean = false): Unit = {
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

  def buildComplexValue(context: BuilderContext, value: ComplexValueStatement[_])(implicit isStatic: Boolean = false): Unit = {
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
    if (callObject.statements.size == 1) {
      callObject.statements.head match {
        case call: CallObject => buildCallObject(context, call)
        case func: CallFuncObject => buildCallFunc(context, func)
      }
    } else buildCallObjectChained(context, callObject)
  }

  def buildCallObjectChained(context: BuilderContext, callObject: CallObject): Unit = {
    val head = callObject.statements.head
    if (head.isInstanceOf[CallVarObject]) {
      val callVar = head.asInstanceOf[CallVarObject]
      val use = context.resource.ast.header.get.uses.get.filter(part => part.parts.last == callVar.name)
      val clazz = if (use.isEmpty) TLangModuleList.getClass(callVar.name)
      else TLangModuleList.getClass(callVar.name, Some(use.head.parts.head))
      val callFunc = callObject.statements(1).asInstanceOf[CallFuncObject]
      context.section.addInstruction(CallCore(clazz.className, callFunc.name.get, new core.Array(Array(new core.String("This is a super test")))))
    }
  }

  def buildCallFunc(context: BuilderContext, func: CallFuncObject, hasOtherCallAfterwards: Boolean = false): Unit = {
    //func.currying.foreach(_.foreach(_.params.foreach(_.foreach(buildOperation(context, _)))))
    context.section.addInstruction(GotoLabel(getContentType(func.context, func.name)))
    context.section.addInstruction(Back(JumpIndex(context.sectionPos, context.instrPos + 2)))
    if (!hasOtherCallAfterwards) context.section.addInstruction(RefFuncGet())
  }

  def buildPrimitive(context: BuilderContext, primitive: PrimitiveValue[_])(implicit isStatic: Boolean = false): Unit = {
    primitive match {
      case bool: TLangBool => buildBool(context, bool)
      case str: TLangString => buildString(context, str)
      case long: TLangLong => buildLong(context, long)
      case double: TLangDouble => buildDouble(context, double)
      case entityValue: EntityValue =>
        if (isStatic) BuildStaticEntity.buildStaticEntity(context, entityValue)
    }
  }

  def buildBool(context: BuilderContext, bool: TLangBool): Unit = {
    context.section.addInstruction(Set(Some(new core.Bool(bool.getValue))))
    context.section.addInstruction(Put(popFromBox = true))
  }

  def buildString(context: BuilderContext, str: TLangString): Unit = {
    context.section.addInstruction(Set(Some(new core.String(str.getValue))))
    context.section.addInstruction(Put(popFromBox = true))
  }

  def buildLong(context: BuilderContext, long: TLangLong): Unit = {
    context.section.addInstruction(Set(Some(new core.Long(long.getValue.intValue()))))
    context.section.addInstruction(Put(popFromBox = true))
  }

  def buildDouble(context: BuilderContext, double: TLangDouble): Unit = {
    context.section.addInstruction(Set(Some(new core.Double(double.getValue))))
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
    val ifInst = IfInstr(JumpIndex(context.sectionPos, context.instrPos + 2), None)
    context.section.addInstruction(ifInst)
    ifStatement.ifTrue.foreach(_.content.foreach(_.foreach(buildStatement(context, _))))
    if (ifStatement.ifFalse.isDefined) {
      ifInst.jumpFalse = Some(JumpIndex(context.sectionPos, context.instrPos + 2))
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

  def getContentType(context: Option[ContextContent], name: Option[String] = None): String = {
    var pkg = ""
    var newName = name.getOrElse("")
    if (context.isDefined) {
      pkg = context.get.getValue.getResource.getPkg.toString
      if (name.isEmpty) newName = context.get.getValue.getResource.getName.toString
    }
    if (pkg.isEmpty) newName
    else pkg + "." + newName
  }
}
