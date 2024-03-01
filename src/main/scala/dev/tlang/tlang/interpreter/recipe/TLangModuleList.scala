package dev.tlang.tlang.interpreter.recipe

import tlang.core
import tlang.mutable.ArrayBuilder
import tlang.tio.{IOFile, Terminal}

import scala.collection.mutable

object TLangModuleList {

  def getClass(name: String, project: Option[String] = None): Option[TLangClass] = {
    if (project.isDefined) {
      val module = modules(project.get)
      if (module.classes.contains(name)) return module.classes.get(name)
    }
    coreClasses.get(name)
  }

  val coreClasses: mutable.Map[String, TLangClass] = {
    val classes = mutable.Map[String, TLangClass]()
    classes += getClassDef(classOf[core.Array])
    classes += getClassDef(classOf[core.Bool])
    //    classes += getClassDef(classOf[core.Case])
    classes += getClassDef(classOf[core.Date])
    classes += getClassDef(classOf[core.Double])
    classes += getClassDef(classOf[core.Either])
    classes += getClassDef(classOf[core.Empty])
    classes += getClassDef(classOf[core.Empty])
    classes += getClassDef(classOf[core.Equal])
    classes += getClassDef(classOf[core.Error])
    classes += getClassDef(classOf[core.Float])
    classes += getClassDef(classOf[core.Instance])
    classes += getClassDef(classOf[core.Int])
    classes += getClassDef(classOf[core.Lazy])
    classes += getClassDef(classOf[core.Long])
    classes += getClassDef(classOf[core.Loop])
    classes += getClassDef(classOf[core.Match])
  }

  val mutableClasses: mutable.Map[String, TLangClass] = {
    val classes = mutable.Map[String, TLangClass]()
    classes += getClassDef(classOf[ArrayBuilder])
    classes += getClassDef(classOf[tlang.mutable.List])
    classes += getClassDef(classOf[tlang.mutable.Map])
    classes += getClassDef(classOf[tlang.mutable.Var])
  }

  val tioClasses: mutable.Map[String, TLangClass] = {
    val classes = mutable.Map[String, TLangClass]()
    classes += getClassDef(classOf[IOFile])
    classes += getClassDef(classOf[Terminal])
  }

  def getClassDef(clazz: Class[_]): (String, TLangClass) = {
    (clazz.getSimpleName, TLangClass(clazz.getName))
  }

  val modules: Map[String, TLangModule] = {
    Map(
      "core" -> TLangModule("core", "tlang", coreClasses.toMap),
      "mutable" -> TLangModule("mutable", "tlang", mutableClasses.toMap),
      "io" -> TLangModule("io", "tlang", tioClasses.toMap)
    )
  }

}
