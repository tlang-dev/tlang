package dev.tlang.tlang.ast.tmpl

case class TmplImpl(var annots: Option[List[TmplAnnotation]] = None, var props: Option[TmplProp] = None, var name: TmplID, var fors: Option[List[TmplImplFor]], var withs:Option[List[TmplImplWith]], var content: Option[List[TmplContent]] = None) extends TmplContent
