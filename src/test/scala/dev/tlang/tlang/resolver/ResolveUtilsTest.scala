package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.{DomainModel, DomainUse}
import dev.tlang.tlang.loader.manifest.Manifest
import dev.tlang.tlang.loader.{Module, Resource}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.immutable

class ResolveUtilsTest extends AnyFunSuite {

  test("Find resource") {
    val manifest = Manifest("Name", "Project", "Org", "version", None, 1, None, None)
    val resource = Resource("Root", "", "", "Resource", DomainModel(None, None, List()))
    val module = Module("Root", manifest, immutable.Map("module/Resource" -> resource), None, "Main")
    val use = DomainUse(None, List("module", "Resource"), None)
    val res = ResolveUtils.findResource(use, module).get
    assert("Resource" == res.name)
  }

  test("Find external resource") {
    val manifest = Manifest("Name", "Project", "Org", "version", None, 1, None, None)
    val resource = Resource("Root", "", "", "Main", DomainModel(None, None, List()))
    val extModule = Module("Root", manifest, immutable.Map("Main" -> resource), None, "Main")
    val module = Module("Root", manifest, Map(), Some(immutable.Map("module" -> extModule)), "Main")
    val use = DomainUse(None, List("module", "anything"), None)
    val res = ResolveUtils.findResource(use, module).get
    assert("Main" == res.name)
  }

}
