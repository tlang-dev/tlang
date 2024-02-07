package dev.tlang.tlang.libraries.builtin

import java.lang.reflect.{InvocationHandler, Method}

class JavaProxy[T] extends InvocationHandler {


  override def invoke(o: Any, method: Method, objects: Array[AnyRef]): AnyRef = {
    null
  }
}
