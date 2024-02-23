package dev.tlang.tlang.libraries.builtin

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils}
import dev.tlang.tlang.libraries.ModulePattern
import tlang.core.Null
import tlang.core.func.FuncRet

import java.lang.reflect.Method
import scala.collection.mutable

object BuiltInModule {

  val modules: mutable.Map[String, GenericModule] = mutable.Map.empty

  val funcClasses: List[Class[_]] = List(
    classOf[tlang.generator.Generator],
    classOf[tlang.tio.IOFile],
    classOf[tlang.tio.Terminal],
  )

  val modelClasses: List[Class[_]] = List(
    classOf[tlang.mutable.List[_]],
    //    classOf[tlang.mutable.Map],
    //    classOf[tlang.mutable.Var],
    classOf[tlang.std.entity.StdEntity],
    classOf[tlang.std.strbuilder.StrBuilder],
  )

  def buildModule(clazz: Class[_]): ModulePattern = {
    val pkg = clazz.getPackage.getName.split('.').last
    val module = GenericModule(pkg, clazz.getSimpleName)
    module.functions ++= buildFuncs(clazz)
    module
  }

  def buildFuncs(clazz: Class[_]): List[HelperFunc] = {
    clazz.getDeclaredMethods
      .filter(method => java.lang.reflect.Modifier.isStatic(method.getModifiers))
      .map(buildFunc).toList
  }

  def buildFunc(method: Method): HelperFunc = {
    val name: String = method.getName
    val returnType: String = method.getReturnType.getSimpleName
    val parameters: Array[Class[_]] = method.getParameterTypes
    HelperFunc(Null.empty(), name,
      None,
      //if (parameters.isEmpty) None else Some(List(HelperCurrying(Null.empty(), parameters.zipWithIndex.map(param => HelperParam(Null.empty(), None, ObjType(Null.empty(), Some("arg" + (param._2 + 1).toString), param._1.get))).toList))),
//      Null.of(List(ObjType(Null.empty(), None, classOf[FuncRet[_]].getSimpleName))),
      None,

      HelperContent(Null.empty(), Some(List(
        HelperInternalFunc((context: Context) => {

          val args = parameters.zipWithIndex.map(param => ContextUtils.findVar(context, "arg" + (param._2 + 1).toString) match {
            case Some(arg1) => arg1
            case None => new TLangString(Null.empty(), "Not found")
          })

          val ret = method.invoke(null, args: _*).asInstanceOf[FuncRet[_]]
          Right(Some(List(ret)))
        })
      )))

    )
  }

}
