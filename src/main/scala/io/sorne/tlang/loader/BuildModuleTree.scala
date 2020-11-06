package io.sorne.tlang.loader

import java.io.File
import java.nio.file.Path

import io.sorne.tlang.astbuilder.BuildAst
import io.sorne.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}

import scala.collection.mutable

object BuildModuleTree {

  def build(rootDir: Path, mainFile: Option[String])(implicit resourceLoader: ResourceLoader): Either[LoaderError, Module] = {
    val resources = mutable.Map.empty[String, Resource]
    val (fromRoot, pkg, name) = mainFile match {
      case Some(value) =>
        val parts: Array[String] = value.split(File.separator)
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
        Right(Module(rootDir.toString, resources.toMap, None, createPkg(fromRoot, pkg, name)))
    }
  }

  def browseResources(root: String, fromRoot: String, pkg: String, name: String, resources: mutable.Map[String, Resource])(implicit resourceLoader: ResourceLoader): Option[LoaderError] = {
    resourceLoader.load(root, fromRoot, pkg, name) match {
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
    Resource(root, fromRoot, pkg, name, BuildAst.build(parser.domainModel()))
  }

  def createPkg(parts: String*): String = {
    val str = new StringBuilder()
    parts.foreach(part => {
      if (!part.isEmpty) {
        if (str.nonEmpty) {
          str ++= "/"
        }
        str ++= part
      }
    })
    str.toString()
  }

}
