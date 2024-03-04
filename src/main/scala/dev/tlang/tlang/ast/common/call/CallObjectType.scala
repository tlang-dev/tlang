package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.ast.helper.HelperStatement

trait CallObjectType extends HelperStatement {
  def getName: String
}
