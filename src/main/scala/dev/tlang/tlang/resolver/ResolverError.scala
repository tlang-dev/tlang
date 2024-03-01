package dev.tlang.tlang.resolver

import tlang.internal.ContextContent

class ResolverError(val code: String, val message: String = "")

object ResolverError {
  def genMessage(context: Option[ContextContent]): String = {
    if (context.isDefined) "[" + context.get.asInstanceOf[ContextContent].getResource.getFromRoot + "/" + context.get.asInstanceOf[ContextContent].getResource.getPkg + "/" + context.get.asInstanceOf[ContextContent].getResource.getName + "(" + context.get.asInstanceOf[ContextContent].getLine + ":" + context.get.asInstanceOf[ContextContent].getCharPos + ")] "
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