package dev.tlang.tlang.resolver

import dev.tlang.tlang.astbuilder.context.ContextContent

class ResolverError(val code: String, val message: String = "")

object ResolverError {
  def genMessage(context: Option[ContextContent]): String = {
    if (context.isDefined) "[" + context.get.resource.fromRoot + "/" + context.get.resource.pkg + "/" + context.get.resource.name + "(" + context.get.line + ":" + context.get.charPos + ")] "
    else ""
  }
}

case class ResourceNotFound(context: Option[ContextContent], resource: String) extends ResolverError("ResourceNotFound", ResourceNotFound.genMessage(context, resource))

object ResourceNotFound {
  def genMessage(context: Option[ContextContent], resource: String): String = {
    ResolverError.genMessage(context) + "Resource not found: " + resource
  }
}

case class TypeError(context: Option[ContextContent], actual: String, expected: String) extends ResolverError("TypeError", TypeError.genMessage(context, actual, expected))

object TypeError {
  def genMessage(context: Option[ContextContent], actual: String, expected: String): String = {
    ResolverError.genMessage(context) + "Actual type: " + actual + ", expected: " + expected
  }
}

case class DoesNotExist(context: Option[ContextContent], call: String) extends ResolverError("DoesNotExist", "'" + call + "' does not exist")

case class NameAlreadyUsed(context: Option[ContextContent], name: String) extends ResolverError("NameAlreadyUsed", NameAlreadyUsed.genMessage(context, name))

object NameAlreadyUsed {
  def genMessage(context: Option[ContextContent], name: String): String = {
    ResolverError.genMessage(context) + "name already used in scope: " + name
  }
}