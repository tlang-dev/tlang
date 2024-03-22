package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value._
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.interpreter.context.JumpIndex
import dev.tlang.tlang.interpreter.instruction
import dev.tlang.tlang.interpreter.instruction._
import dev.tlang.tlang.interpreter.value.InterTmpl
import dev.tlang.tlang.tmpl.AstAnyTmplBlock
import dev.tlang.tlang.tmpl.lang.ast.LangBlock
import tlang.core
import tlang.core.Lazy
import tlang.core.func.FuncRet
import tlang.internal.{ContextContent, DomainBlock}

import scala.collection.mutable

object BuildProgram {

  def buildProgram(context: BuilderContext): Unit = {
    //    ReferenceFinder.addUses(context)
    //    ReferenceFinder.addCallables(context)

    context.program.addSection(context.section)
    val label = context.resource.ast.getType.getType.toString
    val instructionBlock = context.section.newInstructionBlock(label)
    instructionBlock.addInstruction(StartBlock())
    buildBody(context, context.resource.ast.body)
    instructionBlock.addInstruction(EndBlock())


    context.section.build
    buildLabelsAndJumps(context)
  }

  private def buildLabelsAndJumps(context: BuilderContext): Unit = {
    val labels = mutable.Map[String, JumpIndex]()
    context.section.getInstructions.zipWithIndex.foreach(instr => {
      instr._1 match {
        case label: Label =>
          labels += (label.name -> JumpIndex(0, instr._2))
        case back:Back => back.pos = JumpIndex(back.pos.section, back.pos.instruction + instr._2)
        case _ =>
      }
    })
    context.program.setLabels(labels.toMap)
  }

  def buildBody(context: BuilderContext, bodies: List[DomainBlock]): Unit = {
    bodies.foreach {
      case model: ModelBlock => buildModel(context, model)
      case helper: HelperBlock => buildHelper(context, helper)
      case tmpl: AstAnyTmplBlock => buildTmpl(context, tmpl)
    }
  }

  def buildModel(context: BuilderContext, model: ModelBlock): Unit = {
    val label = model.context.get.getValue.getResource.getPkg + "/" + model.context.get.getValue.getResource.getName
    val boxBuilder = new BoxBuilder()
    boxBuilder.setBoxId(label)
    val jumpIndex = JumpIndex(context.sectionPos, context.instrPos + 1)
    context.section.addStaticLabel(jumpIndex)
    context.section.getCurrentInstructionBlock.get.addInstruction(StartStaticBox(label))
    model.content.foreach(_.foreach {
      case assignVar: AssignVar => buildAssignVarInModel(context, boxBuilder, assignVar)
      case setEntity: ModelSetEntity => BuildStaticModel.buildStaticModel(context, setEntity)
      case _ => println(getClass.getName + ": Not implemented yet")
    })
    context.section.getCurrentInstructionBlock.get.addInstruction(EndStaticBox(label))
  }

  def buildAssignVarInModel(context: BuilderContext, boxBuilder: BoxBuilder, assignVar: AssignVar): Unit = {
    implicit val isStatic: Boolean = true
    val label = getContentType(assignVar.context, Some(assignVar.name))
    val instructionBlock = context.section.newInstructionBlock(label)
    val callOnce = CallOnce(assignVar.value, JumpIndex(context.sectionPos, context.instrPos + 2), JumpIndex(context.sectionPos, context.instrPos))
    val lazyVar = boxBuilder.addVar("lazy" + assignVar.name.capitalize)
    instructionBlock.addInstruction(callOnce)
    BuildProgram.buildOperation(context, assignVar.value)
    instructionBlock.addInstruction(instruction.SetStatic(boxBuilder.getBoxId, Some(new Lazy())))
    instructionBlock.addInstruction(SetLazyStatic(boxBuilder.getBoxId, lazyVar.pos))
    callOnce.getIndex = JumpIndex(context.sectionPos, context.instrPos + 1)
    instructionBlock.addInstruction(GetLazyStatic(boxBuilder.getBoxId, lazyVar.pos))
    instructionBlock.addInstruction(EndLabel(label))
  }

  def buildHelper(context: BuilderContext, helper: HelperBlock): Unit = {
    helper.funcs.foreach(_.foreach(func => buildFunc(context, func)))
  }

  def buildFunc(context: BuilderContext, func: HelperFunc): Unit = {
    val label = func.getType.getType.toString
    context.section.getCurrentInstructionBlock.get.addInstruction(Label(label))
    context.labels.addOne(label -> JumpIndex(context.sectionPos, context.instrPos))
    context.section.getCurrentInstructionBlock.get.addInstruction(StartBox())

    func.currying.foreach(_.foreach(_.params.foreach(_ => {
      //context.section.addInstruction(Label(func.getType.getType.toString))
    })))

    if (func.block.content.isDefined) {
      func.block.content.get.foreach(buildStatement(context, _))
      context.section.getCurrentInstructionBlock.get.addInstruction(FuncRetSet())
    } else context.section.getCurrentInstructionBlock.get.addInstruction(Set(Some(FuncRet.VOID)))

    context.section.getCurrentInstructionBlock.get.addInstruction(Put(popFromBox = true))

    context.section.getCurrentInstructionBlock.get.addInstruction(EndBox())
    context.section.getCurrentInstructionBlock.get.addInstruction(EndLabel(label))
  }

  def buildContent(context: BuilderContext, content: HelperContent): Unit = {
    context.section.getCurrentInstructionBlock.get.addInstruction(StartBlock())
    content.content.foreach(_.foreach(buildStatement(context, _)))
    context.section.getCurrentInstructionBlock.get.addInstruction(EndBlock())
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
    context.section.getCurrentInstructionBlock.get.addInstruction(Put())
  }

  def buildOperation(context: BuilderContext, operation: Operation)(implicit isStatic: Boolean = false): Unit = {
    operation.content match {
      case Left(op) => buildOperation(context, op)
      case Right(value) => buildComplexValue(context, value)
    }
    if (operation.next.isDefined) {
      context.section.getCurrentInstructionBlock.get.addInstruction(Load())
      buildOperation(context, operation.next.get._2)
      context.section.getCurrentInstructionBlock.get.addInstruction(Load())
      context.section.getCurrentInstructionBlock.get.addInstruction(Put(popFromBox = true, pos = 1))
      context.section.getCurrentInstructionBlock.get.addInstruction(Put(popFromBox = true))
      context.section.getCurrentInstructionBlock.get.addInstruction(Comp(operation.next.get._1))
    }
    //    context.section.addInstruction(Set(Some(new core.String("This is a test"))))
  }

  def buildComplexValue(context: BuilderContext, value: ComplexValueStatement[_])(implicit isStatic: Boolean = false): Unit = {
    value match {
      case callObj: CallObject => BuildCall.buildCallObject(context, callObj)
      case primitive: PrimitiveValue[_] => buildPrimitive(context, primitive)
      case multiValue: MultiValue => buildMultiValue(context, multiValue)
      case lazyVal: LazyValue[_] => buildLazyValue(context, lazyVal)
      case impl: EntityImpl => buildEntityImpl(context, impl)
      //      case either: Either[_, _] => buildEither(context, either)
    }
  }


  def buildSetAttribute(context: BuilderContext, setAttribute: SetAttribute): Unit = {
    buildOperation(context, setAttribute.value)
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
    context.section.getCurrentInstructionBlock.get.addInstruction(Set(Some(new core.Bool(bool.getValue))))
    context.section.getCurrentInstructionBlock.get.addInstruction(Put(popFromBox = true))
  }

  def buildString(context: BuilderContext, str: TLangString): Unit = {
    context.section.getCurrentInstructionBlock.get.addInstruction(Set(Some(new core.String(str.getValue))))
    context.section.getCurrentInstructionBlock.get.addInstruction(Put(popFromBox = true))
  }

  def buildLong(context: BuilderContext, long: TLangLong): Unit = {
    context.section.getCurrentInstructionBlock.get.addInstruction(Set(Some(new core.Long(long.getValue.intValue()))))
    context.section.getCurrentInstructionBlock.get.addInstruction(Put(popFromBox = true))
  }

  def buildDouble(context: BuilderContext, double: TLangDouble): Unit = {
    context.section.getCurrentInstructionBlock.get.addInstruction(Set(Some(new core.Double(double.getValue))))
    context.section.getCurrentInstructionBlock.get.addInstruction(Put(popFromBox = true))
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
    context.section.getCurrentInstructionBlock.get.addInstruction(ifInst)
    ifStatement.ifTrue.foreach(_.content.foreach(_.foreach(buildStatement(context, _))))
    if (ifStatement.ifFalse.isDefined) {
      ifInst.jumpFalse = Some(JumpIndex(context.sectionPos, context.instrPos + 2))
      ifStatement.ifFalse.foreach(_.content.foreach(_.foreach(buildStatement(context, _))))
    }
  }

  def buildFor(context: BuilderContext, forStatement: HelperFor): Unit = {
    //    context.section.getCurrentInstructionBlock.get.addInstruction(ForInstruction(forStatement.condition, forStatement.content))
  }

  def buildTmpl(context: BuilderContext, tmpl: AstAnyTmplBlock): Unit = {
    tmpl match {
      case block: LangBlock => buildLang(context, block)
      case _ =>
    }
  }

  def buildLang(context: BuilderContext, lang: LangBlock): Unit = {
    val label = getContentType(lang.context, Some(lang.name))
    val instructionBlock = context.section.newInstructionBlock(label)
    instructionBlock.addInstruction(StartBox())
    BuildAstEntity.buildAstEntity(context, lang.toEntity)

    instructionBlock.addInstruction(Set(Some(InterTmpl(lang.toEntity))))
    instructionBlock.addInstruction(Put())

    instructionBlock.addInstruction(EndBox())
    instructionBlock.addInstruction(EndLabel(label))
  }

  def getContentType(context: Option[ContextContent], name: Option[String] = None): String = {
    var pkg = ""
    var newName = name.getOrElse("")
    if (context.isDefined) {
      pkg = context.get.getValue.getResource.getPkg.toString
      if (name.isEmpty) newName = context.get.getValue.getResource.getName.toString
    }
    if (pkg.isEmpty) newName
    else pkg + "/" + newName
  }

  def addLabel(context: BuilderContext, name: String, instrPos: Int, isStatic: Boolean = false): Unit = {
    val jumpIndex = JumpIndex(context.sectionPos, instrPos)
    context.section.getCurrentInstructionBlock.get.addInstruction(Label(name))
    context.labels.addOne(name -> jumpIndex)
    if (isStatic) context.section.addStaticLabel(jumpIndex)
  }
}
