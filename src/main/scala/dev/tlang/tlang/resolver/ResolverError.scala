package dev.tlang.tlang.resolver

import tlang.core.Null
import tlang.internal.ContextContent

class ResolverError(val code: String, val message: String = "")

object ResolverError {
  def genMessage(context: Null[ContextContent]): String = {
    if (context.isNotNull.get()) "[" + context.get.getResource.getFromRoot + "/" + context.get.getResource.getPkg + "/" + context.get.getResource.getName + "(" + context.get.getLine + ":" + context.get.getCharPos + ")] "
    else ""
  }
}

case class ResourceNotFound(context: Null[ContextContent], resource: String) extends ResolverError("ResourceNotFound", ResourceNotFound.genMessage(context, resource))

object ResourceNotFound {
  def genMessage(context: Null[ContextContent], resource: String): String = {
    ResolverError.genMessage(context) + "Resource not found: " + resource
  }
}

case class TypeError(context: Null[ContextContent], actual: String, expected: String) extends ResolverError("TypeError", TypeError.genMessage(context, actual, expected))

object TypeError {
  def genMessage(context: Null[ContextContent], actual: String, expected: String): String = {
    ResolverError.genMessage(context) + "Actual type: " + actual + ", expected: " + expected
  }
}

case class DoesNotExist(context: Null[ContextContent], call: String) extends ResolverError("DoesNotExist", "'" + call + "' does not exist")

case class NameAlreadyUsed(context: Null[ContextContent], name: String) extends ResolverError("NameAlreadyUsed", NameAlreadyUsed.genMessage(context, name))

object NameAlreadyUsed {
  def genMessage(context: Null[ContextContent], name: String): String = {
    ResolverError.genMessage(context) + "name already used in scope: " + name
  }
}