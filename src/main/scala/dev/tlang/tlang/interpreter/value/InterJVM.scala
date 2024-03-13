package dev.tlang.tlang.interpreter.value

import tlang.core
import tlang.core.func.FuncRet
import tlang.core.{Type, Value}
import tlang.internal.ClassType

import java.lang.reflect.Method

case class InterJVM(clazz: Class[_]) extends InterValue(InterValueType.JVM) {

  override def getType: Type = ClassType.of(clazz)

  override def getValue: InterJVM = this

  override def callFunc(name: core.String, args: core.Array): FuncRet = {
    val method = getMethod(name.toString, args.getRecords.map(arg => arg.getClass.asInstanceOf[Class[Value]]))
    invokeJavaMethodWithVarargs(method, args.getRecords).asInstanceOf[FuncRet]
  }

  private def getMethod(name: String, args: Array[Class[Value]]): Method = {
    clazz.getMethods.array.find(method => method.getName == name).get
  }

  private def invokeJavaMethodWithVarargs(method: Method, args: Array[Value]): Any = {
    method.invoke(null, args: _*)
  }

  override def getAttrPath(name: String): String = ???

  override def getAttrPathByPos(pos: Int): String = ???
}
