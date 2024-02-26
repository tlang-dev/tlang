package dev.tlang.tlang.interpreter.context

import dev.tlang.tlang.ast.common.call.CallRefFuncObject
import dev.tlang.tlang.ast.common.value.{TLangBool, TLangDouble, TLangLong, TLangString}
import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.ast.model.set.ModelSetValueType
import dev.tlang.tlang.tmpl.AnyTmplInterpretedBlock
import tlang.core.{Bool, Double, Long, Null, Value}

object ContextUtils {

  val nativeModels: Map[String, ModelSetValueType[_]] = Map(
    TLangString.getType.getType.toString -> new TLangString(Null.empty(), ""),
    TLangBool.getType.getType.toString -> new TLangBool(Null.empty(), new Bool(false)),
    TLangDouble.getType.getType.toString -> new TLangDouble(Null.empty(), 0),
    TLangLong.getType.getType.toString -> new TLangLong(Null.empty(), new Long(0))
  )

  def findVar(context: Context, name: String): Option[Value] = {
    var i = context.scopes.length - 1
    var variable: Option[Value] = None
    while (variable.isEmpty && i >= 0) {
      context.scopes(i).variables.get(name).foreach(value => variable = Some(value))
      i -= 1
    }
    variable
  }

  def findVarInScope(scope: Scope, name: String): Option[Value] = {
    var variable: Option[Value] = None
    scope.variables.get(name).foreach(value => variable = Some(value))
    variable
  }

  def findFunc(context: Context, name: String): Option[HelperFunc] = {
    var i = context.scopes.length - 1
    var func: Option[HelperFunc] = None
    while (func.isEmpty && i >= 0) {
      context.scopes(i).functions.get(name).foreach(value => func = Some(value))
      i -= 1
    }
    func
  }

  def findTmpl(context: Context, name: String): Option[AnyTmplInterpretedBlock[_]] = {
    var i = context.scopes.length - 1
    var tmpl: Option[AnyTmplInterpretedBlock[_]] = None
    while (tmpl.isEmpty && i >= 0) {
      context.scopes(i).templates.get(name).foreach(value => tmpl = Some(value))
      i -= 1
    }
    tmpl
  }

  def findRefFunc(context: Context, name: String): Option[CallRefFuncObject] = {
    var i = context.scopes.length - 1
    var ref: Option[CallRefFuncObject] = None
    while (ref.isEmpty && i >= 0) {
      context.scopes(i).refFunctions.get(name).foreach(value => ref = Some(value))
      i -= 1
    }
    ref
  }

  def findModel(context: Context, name: String): Option[ModelSetValueType[_]] = {
    var i = context.scopes.length - 1
    var model: Option[ModelSetValueType[_]] = None
    nativeModels.get(name) match {
      case Some(value) => model = Some(value)
      case None =>
        while (model.isEmpty && i >= 0) {
          context.scopes(i).models.get(name).foreach(value => model = Some(value))
          i -= 1
        }
    }
    model
  }

  def removeScopeByName(context: Context, name: String): Context = {
    Context(context.scopes.filter(scope => scope.name != name))
  }

  def removeLocalScopes(context: Context): Context = {
    Context(context.scopes.filter(_.local))
  }

}
