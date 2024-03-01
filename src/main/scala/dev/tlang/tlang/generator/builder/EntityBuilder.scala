package dev.tlang.tlang.generator.builder

object EntityBuilder {

//  def buildEntity(entity: LangEntityValue, context: Context): Either[ExecError, LangEntityValue] = {
//
//    def params(): Either[ExecError, LangEntityValue] = {
//      if (entity.params.isDefined) {
//        buildInclAttributes(entity.params, context) match {
//          case Left(error) => Left(error)
//          case Right(nodes) =>
//            entity.params = nodes
//            attrs()
//        }
//      } else attrs()
//    }
//
//    def attrs(): Either[ExecError, LangEntityValue] = {
//      if (entity.attrs.isDefined) {
//        buildInclAttributes(entity.attrs, context) match {
//          case Left(error) => Left(error)
//          case Right(nodes) =>
//            entity.attrs = nodes
//            Right(entity)
//        }
//      } else Right(entity)
//    }
//
//    if (entity.name.isDefined) includeTmplId(entity.name.get, context) match {
//      case Left(error) => Left(error)
//      case Right(value) =>
//        entity.name = Some(value.head.asInstanceOf[TmplID])
//        params()
//    } else params()
//
//  }

}
