package io.sorne.tlang.bagman

import io.sorne.tlang.loader.manifest.{Manifest, Stability}
import io.sorne.tlang.loader.{BuildModuleTree, FileResourceLoader, ModuleLoader, ResourceLoader, TBagManager}

import java.net.URL
import java.nio.file.{Path, Paths}

object Packager {

  def createDefaultPackage(folder: Path): Either[BGError, Path] = {
    implicit val resourceLoader: ResourceLoader = FileResourceLoader
    implicit val resourceWriter: ResourceWriter = FileResourceWriter
    implicit val tBagManager: TBagManager = TBagManager
    Packager.createPackage(folder, ModuleLoader.tLandRepo)
  }

  def createPackage(folder: Path, dest: Path)(implicit resourceLoader: ResourceLoader, resourceWriter: ResourceWriter, tBagManager: TBagManager): Either[BGError, Path] = {
    BuildModuleTree.buildManifest(folder) match {
      case Left(value) => Left(BGError(value.code, value.message))
      case Right(manifest) => genManifest(manifest) match {
        case Left(value) => Left(BGError(value.code, value.message))
        case Right(pkgManifest) =>
          val manifestStr = pkgManifest.toYaml
          val pkgPath = Paths.get(dest.toString, pkgManifest.path)
          resourceWriter.write(pkgPath, "TBagManifest.yaml", manifestStr)
          tBagManager.compress(folder, Paths.get(pkgPath.toString, manifest.name + ".tbag"))
          Right(pkgPath)
      }
    }
  }

  def genManifest(manifest: Manifest): Either[BGError, PkgManifest] = {
    val name = List(manifest.organisation, manifest.project, manifest.name, manifest.version, manifest.stability.getOrElse(Stability.UNKNOWN).toString.toLowerCase, manifest.releaseNumber.toString).mkString("/")
    val tbagName = manifest.name + ".tbag"
    val pkgMan = PkgManifest(
      name,
      List(PkgManifestFile(manifest.name + ".tbag", "tbag", "", Some(new URL("https://house.tlang.dev/" + name + "/" + tbagName))))
    )
    Right(pkgMan)
  }

}
