package dev.tlang.tlang.interpreter.context

import dev.tlang.tlang.ast.common.call.CallRefFuncObject
import dev.tlang.tlang.ast.common.value.{TLangBool, TLangDouble, TLangLong, TLangString}
import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.ast.model.set.ModelSetValueType
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.LangBlock

object ContextUtils {

  val nativeModels: Map[String, ModelSetValueType[_]] = Map(
    TLangString.getType -> new TLangString(None, ""),
    TLangBool.getType -> new TLangBool(None, false),
    TLangDouble.getType -> new TLangDouble(None, 0),
    TLangLong.getType -> new TLangLong(None, 0)
  )

  def findVar(context: Context, name: String): Option[Value[_]] = {
    var i = 0
    var variable: Option[Value[_]] = None
    while (variable.isEmpty && i < context.scopes.length) {
      context.scopes(i).variables.get(name).foreach(value => variable = Some(value))
      i += 1
    }
    variable
  }

  def findVarInScope(scope: Scope, name: String): Option[Value[_]] = {
    var variable: Option[Value[_]] = None
    scope.variables.get(name).foreach(value => variable = Some(value))
    variable
  }

  def findFunc(context: Context, name: String): Option[HelperFunc] = {
    var i = 0
    var func: Option[HelperFunc] = None
    while (func.isEmpty && i < context.scopes.length) {
      context.scopes(i).functions.get(name).foreach(value => func = Some(value))
      i += 1
    }
    func
  }

  def findTmpl(context: Context, name: String): Option[LangBlock] = {
    var i = 0
    var tmpl: Option[LangBlock] = None
    while (tmpl.isEmpty && i < context.scopes.length) {
      context.scopes(i).templates.get(name).foreach(value => tmpl = Some(value))
      i += 1
    }
    tmpl
  }

  def findRefFunc(context: Context, name: String): Option[CallRefFuncObject] = {
    var i = 0
    var ref: Option[CallRefFuncObject] = None
    while (ref.isEmpty && i < context.scopes.length) {
      context.scopes(i).refFunctions.get(name).foreach(value => ref = Some(value))
      i += 1
    }
    ref
  }

  def findModel(context: Context, name: String): Option[ModelSetValueType[_]] = {
    var i = 0
    var model: Option[ModelSetValueType[_]] = None
    nativeModels.get(name) match {
      case Some(value) => model = Some(value)
      case None =>
        while (model.isEmpty && i < context.scopes.length) {
          context.scopes(i).models.get(name).foreach(value => model = Some(value))
          i += 1
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
