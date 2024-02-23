package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.ast.DomainModel
import dev.tlang.tlang.ast.helper.{HelperBlock, HelperContent, HelperFunc, HelperStatement}
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.interpreter.instruction._
import dev.tlang.tlang.tmpl.lang.ast.LangBlock
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
      case _ =>
    }
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
