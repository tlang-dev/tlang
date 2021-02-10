package dev.tlang.tlang.resolver

import io.sorne.tlang.ast.{DomainModel, DomainUse}
import io.sorne.tlang.loader.{Module, Resource}
import io.sorne.tlang.loader.manifest.Manifest
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.immutable

class ResolveUtilsTest extends AnyFunSuite {

  test("Find resource") {
    val manifest = Manifest("Name", "Project", "Org", "version", None, 1, None)
    val resource = Resource("Root", "", "", "Resource", DomainModel(None, List()))
    val module = Module("Root", manifest, immutable.Map("module/Resource" -> resource), None, "Main")
    val use = DomainUse(List("module", "Resource"))
    val res = ResolveUtils.findResource(use, module).get
    assert("Resource" == res.name)
  }

  test("Find external resource") {
    val manifest = Manifest("Name", "Project", "Org", "version", None, 1, None)
    val resource = Resource("Root", "", "", "Main", DomainModel(None, List()))
    val extModule = Module("Root", manifest, immutable.Map("Main" -> resource), None, "Main")
    val module = Module("Root", manifest, Map(), Some(immutable.Map("module" -> extModule)), "Main")
    val use = DomainUse(List("module", "anything"))
    val res = ResolveUtils.findResource(use, module).get
    assert("Main" == res.name)
  }

}
