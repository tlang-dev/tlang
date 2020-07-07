package io.sorne.tlang.generator.scala

import io.sorne.tlang.ast.tmpl.func.{TmplFunc, TmplFuncCurry}
import io.sorne.tlang.ast.tmpl.{TmplGeneric, TmplParam, TmplType}

object ScalaImplFuncGenerator {

  def gen(func: TmplFunc): String = {
    val str = new StringBuilder
    str ++= "def " ++= func.name
    str ++= genCurry(func.curries)
    str ++= " {\n}\n\n"
    str.toString
  }

  def genCurry(curries: Option[List[TmplFuncCurry]]): String = {
    val str = new StringBuilder
    curries.foreach(_.foreach(curry => {
      str ++= "("
      str ++= genParams(curry.params)
      str ++= ")"
    }))
    str.toString
  }

  def genParams(params: Option[List[TmplParam]]): String = {
    val str = new StringBuilder
    params.foreach(_.zipWithIndex.foreach(param => {
      if (param._2 > 0) str ++= ", "
      str ++= param._1.name ++= ": "
      str ++= genType(param._1.`type`)
    }))
    str.toString
  }

  def genType(`type`: TmplType): String = {
    val str = new StringBuilder
    if (`type`.isArray) str ++= "Array["
    str ++= `type`.name ++= genGeneric(`type`.generic)
    if (`type`.isArray) str ++= "]"
    str.toString
  }

  def genGeneric(gen: Option[TmplGeneric]): String = {
    val str = new StringBuilder
    gen.foreach(g => {
      str ++= "["
      g.types.zipWithIndex.foreach(t => {
        if (t._2 > 0) str ++= ", "
        str ++= genType(t._1)
      })
      str ++= "]"
    })
    str.toString
  }
}
