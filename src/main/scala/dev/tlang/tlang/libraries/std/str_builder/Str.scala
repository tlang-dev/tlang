package dev.tlang.tlang.libraries.std.str_builder

import dev.tlang.tlang.ast.model.set.ModelSetEntity

object Str {

  def strBuilder: ModelSetEntity = new StrBuilderEntity(new StringBuilder()).entity

}
