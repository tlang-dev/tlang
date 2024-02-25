package dev.tlang.tlang.interpreter.recipe

import scala.collection.mutable

class BoxBuilder {

  private val boxVars = mutable.Map[String, BoxVar]()
  private var boxId = ""

  def addVar(name: String): BoxVar = {
    val boxVar = BoxVar(boxVars.size)
    boxVars += (name -> boxVar)
    boxVar
  }

  def getVar(name: String): Option[BoxVar] = {
    boxVars.get(name)
  }

  def getBoxId: String = boxId

  def setBoxId(id: String): Unit = {
    boxId = id
  }
}
