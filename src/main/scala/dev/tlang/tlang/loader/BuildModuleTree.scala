package dev.tlang.tlang.loader

import dev.tlang.tlang.astbuilder.BuildAst
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.libraries.Modules
import dev.tlang.tlang.loader.manifest.{Manifest, ManifestLoader}
import dev.tlang.tlang.loader.remote.RemoteLoader
import dev.tlang.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}

import java.nio.file.Path
import java.util.UUID.randomUUID
import scala.collection.mutable


object BuildModuleTree {

  def build(rootDir: Path, mainFile: Option[String], cacheId: String = randomUUID().toString)(implicit resourceLoader: ResourceLoader, remote: RemoteLoader, tBagManager: TBagManager): Either[LoaderError, Module] = {
    val resources = mutable.Map.empty[String, Resource]
    val (fromRoot, pkg, name) = mainFile match {
      case Some(value) =>
        val parts: Array[String] = value.split("/")
        val fromRoot = if (parts.length > 2) {
          parts.slice(0, parts.length - 2).mkString("/")
        } else ""
        val pkg = parts(parts.length - 2)
        val name = parts.last
        (fromRoot, pkg, name)
      case None => ("", "", "Main")
    }

    browseResources(rootDir.toString, fromRoot, pkg, name, resources) match {
      case Some(error) => Left(error)
      case None =>
        buildManifest(rootDir) match {
          case Left(error) => Left(error)
          case Right(manifest) =>
            browseExternalResources(manifest, cacheId) match {
              case Left(error) => Left(error)
              case Right(value) =>
                val modules = if (value.nonEmpty) Some(value) else None
                Right(Module(rootDir.toString, manifest, resources.toMap, modules, createPkg(fromRoot, pkg, name)))
            }
        }
    }
  }

  def buildManifest(rootDir: Path)(implicit resourceLoader: ResourceLoader): Either[LoaderError, Manifest] = {
    resourceLoader.load(rootDir.toString, "", "", "manifest.yaml") match {
      case Left(error) => Left(error)
      case Right(manifest) => Right(ManifestLoader.parseManifest(manifest))
    }
  }

  def browseExternalResources(manifest: Manifest, cacheId: String)(implicit resourceLoader: ResourceLoader, remote: RemoteLoader, tBagManager: TBagManager): Either[LoaderError, Map[String, Module]] = {
    val modules = mutable.Map.empty[String, Module]
    manifest.dependencies.foreach(_.foreach(dependency => {
      //      if (dependency.organisation == Modules.organisation) {
      Modules.findModule(dependency) match {
        case Some(module) => modules.addOne(dependency.alias.getOrElse(module.manifest.name), module)
        case None =>
          ModuleLoader.loadModule(dependency, cacheId) match {
            case Left(error) => println("CANNOT GET MODULE:" + error.message)
            case Right(module) => modules.addOne(dependency.alias.getOrElse(module.manifest.name), module)
          }
      }
      //      }
    }))
    Right(modules.toMap)
  }

  def browseResources(root: String, fromRoot: String, pkg: String, name: String, resources: mutable.Map[String, Resource])(implicit resourceLoader: ResourceLoader): Option[LoaderError] = {
    resourceLoader.load(root, fromRoot, pkg, name + ".tlang") match {
      case Left(error) => Some(error)
      case Right(value) =>
        val resource = buildResourceAST(root, fromRoot, pkg, name, value)
        resources.addOne(createPkg(fromRoot, pkg, name) -> resource)
        resource.ast.header.foreach(_.uses.foreach(_.foreach(use => {
          val (nextFromRoot, nextPkg, nextName) = if (use.parts.size == 2) (createPkg(fromRoot, pkg), use.parts.head, use.parts.last) else (fromRoot, pkg, use.parts.head)
          browseResources(root, nextFromRoot, nextPkg, nextName, resources)(resourceLoader)
        })))
        None
    }
  }

  def buildResourceAST(root: String, fromRoot: String, pkg: String, name: String, content: String): Resource = {
    val parser = new TLangParser(new CommonTokenStream(new TLangLexer(CharStreams.fromString(content))))
    Resource(root, fromRoot, pkg, name, BuildAst.build(ContextResource(root, fromRoot, pkg, name), parser.domainModel()))
  }

  def createPkg(parts: String*): String = {
    val str = new StringBuilder()
    parts.foreach(part => {
      if (part.nonEmpty) {
        if (str.nonEmpty) {
          str ++= "/"
        }
        str ++= part
      }
    })
    str.toString()
  }

}
