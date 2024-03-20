package dev.tlang.tlang.interpreter.value

import dev.tlang.tlang.tmpl.AstEntity
import tlang.core.Type

case class InterTmpl(entity: AstEntity) extends InterValue(InterValueType.Tmpl) {

  override def getType: Type = entity.getType

  override def getAttrPath(name: String): String = entity.getType.getType.toString + "/" + name

  override def getAttrPathByPos(pos: Int): String = entity.getType.getType.toString + "/" + pos.toString

  override def getValue: InterTmpl = this

  def getAstAttrByName(name:String):InterTmpl = InterTmpl(entity.attrs.get.find(attr => attr.name.get == name).get.value.get.asInstanceOf[AstEntity])

}
