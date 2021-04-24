package dev.tlang.tlang.generator.builder

import dev.tlang.tlang.ast.tmpl.TmplID
import dev.tlang.tlang.ast.tmpl.primitive.TmplEntityValue
import dev.tlang.tlang.generator.builder.TemplateBuilder.{buildInclAttributes, includeTmplId}
import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.Context

object EntityBuilder {

  def buildEntity(entity: TmplEntityValue, context: Context): Either[ExecError, TmplEntityValue] = {

    def params(): Either[ExecError, TmplEntityValue] = {
      if (entity.params.isDefined) {
        buildInclAttributes(entity.params, context) match {
          case Left(error) => Left(error)
          case Right(nodes) =>
            entity.params = nodes
            attrs()
        }
      } else attrs()
    }

    def attrs(): Either[ExecError, TmplEntityValue] = {
      if (entity.attrs.isDefined) {
        buildInclAttributes(entity.attrs, context) match {
          case Left(error) => Left(error)
          case Right(nodes) =>
            entity.attrs = nodes
            Right(entity)
        }
      } else Right(entity)
    }

    if (entity.name.isDefined) includeTmplId(entity.name.get, context) match {
      case Left(error) => Left(error)
      case Right(value) =>
        entity.name = Some(value.head.asInstanceOf[TmplID])
        params()
    } else params()

  }

}
