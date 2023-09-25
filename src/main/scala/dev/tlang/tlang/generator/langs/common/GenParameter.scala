package dev.tlang.tlang.generator.langs.common

case class GenParameter(
                         addEOS: Boolean = false
                       )

object GenParameter {
  def default(): GenParameter = GenParameter()
}