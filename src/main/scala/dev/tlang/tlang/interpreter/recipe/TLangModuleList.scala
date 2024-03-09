package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.interpreter.value.InterJVM
import tlang.core
import tlang.mutable.ArrayBuilder
import tlang.tio.{IOFile, Terminal}

import scala.collection.mutable

object TLangModuleList {

  def getClass(name: String, project: Option[String] = None): Option[InterJVM] = {
    if (project.isDefined) {
      val module = modules(project.get)
      if (module.classes.contains(name)) return module.classes.get(name)
    }
    coreClasses.get(name)
  }

  val coreClasses: mutable.Map[String, InterJVM] = {
    val pkg = "TLang/Core"
    val classes = mutable.Map[String, InterJVM]()
    classes += getClassDef(pkg, classOf[core.Array])
    classes += getClassDef(pkg, classOf[core.Bool])
    //    classes += getClassDef(classOf[core.Case])
    classes += getClassDef(pkg, classOf[core.Date])
    classes += getClassDef(pkg, classOf[core.Double])
    classes += getClassDef(pkg, classOf[core.Either])
    classes += getClassDef(pkg, classOf[core.Empty])
    classes += getClassDef(pkg, classOf[core.Empty])
    classes += getClassDef(pkg, classOf[core.Equal])
    classes += getClassDef(pkg, classOf[core.Error])
    classes += getClassDef(pkg, classOf[core.Float])
    classes += getClassDef(pkg, classOf[core.Instance])
    classes += getClassDef(pkg, classOf[core.Int])
    classes += getClassDef(pkg, classOf[core.Lazy])
    classes += getClassDef(pkg, classOf[core.Long])
    classes += getClassDef(pkg, classOf[core.Loop])
    classes += getClassDef(pkg, classOf[core.Match])
  }

  val mutableClasses: mutable.Map[String, InterJVM] = {
    val pkg = "TLang/Mutable"
    val classes = mutable.Map[String, InterJVM]()
    classes += getClassDef(pkg, classOf[ArrayBuilder])
    classes += getClassDef(pkg, classOf[tlang.mutable.List])
    classes += getClassDef(pkg, classOf[tlang.mutable.Map])
    classes += getClassDef(pkg, classOf[tlang.mutable.Var])
  }

  val tioClasses: mutable.Map[String, InterJVM] = {
    val pkg = "TLang/IO"
    val classes = mutable.Map[String, InterJVM]()
    classes += getClassDef(pkg, classOf[IOFile])
    classes += getClassDef(pkg, classOf[Terminal])
  }

  val internalClasses: Map[String, InterJVM] = {
    val classes = mutable.Map[String, InterJVM]()
    classes ++= coreClasses
    classes ++= mutableClasses
    classes ++= tioClasses
    classes.toMap
  }

  private def getClassDef(pkgName: String, clazz: Class[_]): (String, InterJVM) = {
    (pkgName + "/" + clazz.getSimpleName, InterJVM(clazz))
  }

  val modules: Map[String, TLangModule] = {
    Map(
      "core" -> TLangModule("Core", "TLang", coreClasses.toMap),
      "mutable" -> TLangModule("Mutable", "TLang", mutableClasses.toMap),
      "io" -> TLangModule("IO", "TLang", tioClasses.toMap)
    )
  }

}
