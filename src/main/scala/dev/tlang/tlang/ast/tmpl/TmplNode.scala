package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.interpreter.Value

trait TmplNode[T] extends Value[T] with DeepCopy
