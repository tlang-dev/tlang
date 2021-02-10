package dev.tlang.tlang.loader

import dev.tlang.tlang.loader.manifest.Dependency
import dev.tlang.tlang.loader.remote.RemoteLoader
import io.sorne.tlang.loader.manifest.Dependency
import io.sorne.tlang.loader.remote.RemoteLoader

import java.lang
import java.nio.file.{Path, Paths}
import scala.util.{Failure, Success}

object ModuleLoader {

  val userHome: String = System.getProperty("user.home")
  val tLanFolder: Path = Paths.get(userHome, ".tlang")
  val tLandRepo: Path = Paths.get(tLanFolder.toString, "repo")

  def loadModule(dependency: Dependency, cacheId: String)(implicit resourceLoader: ResourceLoader, remote: RemoteLoader, tBagManager: TBagManager): Either[LoaderError, lang.Module] = {

    def callExtractTBag(tBag: Path): Either[LoaderError, lang.Module] = {
      extractTBagInCache(tBag, cacheId) match {
        case Left(error) => Left(error)
        case Right(path) => loadModuleInCache(path, cacheId)
      }
    }

    searchLocalRepo(dependency) match {
      case Left(error) => Left(error)
      case Right(value) => value match {
        case Some(tBag) => callExtractTBag(tBag)
        case None => searchRemoteRepo(dependency) match {
          case Left(error) => Left(error)
          case Right(value) => value match {
            case Some(tBag) => callExtractTBag(tBag)
            case None => Left(LoaderError("MODULE_NOT_FOUND"))
          }
        }
      }
    }
  }

  def loadModuleInCache(tBag: Path, cacheId: String)(implicit resourceLoader: ResourceLoader, remote: RemoteLoader, tBagManager: TBagManager): Either[LoaderError, lang.Module] = {
    BuildModuleTree.build(tBag, None, cacheId)
  }

  def searchLocalRepo(dependency: Dependency)(implicit tBagManager: TBagManager): Either[LoaderError, Option[Path]] = {
    val depFolder = Paths.get(tLandRepo.toString, dependency.organisation, dependency.project, dependency.name, dependency.stability.toString, dependency.version, dependency.releaseNumber.toString)
    if (tBagManager.isDirectory(depFolder)) {
      tBagManager.findTBagFile(depFolder) match {
        case Some(tBag) => Right(Some(tBag.toPath))
        case None => Right(None)
      }
    } else Right(None)
  }

  def searchRemoteRepo(dependency: Dependency)(implicit resourceLoader: ResourceLoader, remote: RemoteLoader): Either[LoaderError, Option[Path]] = {
    remote.fetchRepoManifest(dependency) match {
      case Failure(exception) => Left(LoaderError("REMOTE_EXCEPTION", exception.getMessage))
      case Success(manifest) =>
        remote.fetchTBag(dependency, manifest, repoFolder(dependency)) match {
          case Failure(exception) => Left(LoaderError("REMOTE_EXCEPTION", exception.getMessage))
          case Success(path) => Right(Some(path))
        }
    }
  }

  def extractTBagInCache(tBag: Path, cacheId: String): Either[LoaderError, Path] = {
    val dest = Paths.get(tLanFolder.toString, ".cache", cacheId)
    TBagManager.extract(tBag, dest)
    Right(dest)
  }

  def repoFolder(dependency: Dependency): Path = Paths.get(tLandRepo.toString, dependency.organisation, dependency.project,
    dependency.name, dependency.stability.toString, dependency.version, dependency.releaseNumber.toString)

}
