package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.{DomainModel, DomainUse}
import dev.tlang.tlang.loader.manifest.Manifest
import dev.tlang.tlang.loader.{Module, Resource}
import org.scalatest.funsuite.AnyFunSuite
import tlang.core.Null

import scala.collection.immutable

class ResolveUtilsTest extends AnyFunSuite {

  test("Find resource") {
    val manifest = Manifest("Name", "Project", "Org", "version", None, 1, None, None)
    val resource = Resource("Root", "", "", "Resource", DomainModel(Null.empty(), None, List()))
    val module = Module("Root", manifest, immutable.Map("module/Resource" -> resource), None, "Main")
    val use = DomainUse(Null.empty(), List("module", "Resource"), None)
    val res = ResolveUtils.findResource(use, module).get
    assert("Resource" == res.name)
  }

  test("Find external resource") {
    val manifest = Manifest("Name", "Project", "Org", "version", None, 1, None, None)
    val resource = Resource("Root", "", "", "Main", DomainModel(Null.empty(), None, List()))
    val extModule = Module("Root", manifest, immutable.Map("Main" -> resource), None, "Main")
    val module = Module("Root", manifest, Map(), Some(immutable.Map("module" -> extModule)), "Main")
    val use = DomainUse(Null.empty(), List("module", "anything"), None)
    val res = ResolveUtils.findResource(use, module).get
    assert("Main" == res.name)
  }

}
