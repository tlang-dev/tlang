package io.sorne.tlang.bagman

import java.io.File
import java.net.URL
import java.nio.file.{Files, Path, Paths}

import scala.sys.process._
import scala.util.{Failure, Success, Try}

import scala.language.postfixOps

object TBagManager {

  val USER_HOME: String = System.getProperty("user.home")
  val TBAG_MANAGER_DIRECTORY = ".tbag"

  def fetch(repositories: List[Repository], dependency: String): Either[BGError, Unit] = {

    Right(())
  }

  def findInLocalRepo(dependency: String): Either[BGError, Boolean] = {
    if (!dependency.contains("/")) Left(BGError("DEPENDENCY_FORMAT_ERROR", "The dependency does not contain any slashes, should be: organisation/name/version"))
    else {
      val chunks = dependency.split("/")
      if (chunks.size != 3) Left(BGError("DEPENDENCY_FORMAT_ERROR", "The dependency does not contain three sections, should be: organisation/name/version"))
      else {
        val path = Paths.get(USER_HOME, TBAG_MANAGER_DIRECTORY, chunks(0), chunks(1), chunks(2), "MANIFEST")
        if (Files.isRegularFile(path)) {
          readManifest(path) match {
            case Left(error) => Left(error)
            case Right(value) => null
          }
        } else Left(BGError("MANIFEST_NOT_FOUND", "The MANIFEST file does not exist"))
      }
    }
  }

  def readManifest(path: Path): Either[BGError, PkgManifest] = {
    null
  }

  def syncFiles(manifest: PkgManifest): Either[BGError, Unit] = {
    manifest.files.map(syncFile).find(_.isLeft).getOrElse(Right(()))
  }

  def syncFile(file: PkgManifestFile): Either[BGError, Unit] = {
    file.remote match {
      case Some(url) => file.local match {
        case Some(local) => fileDownloader(url, local) match {
          case Failure(exception) => Left(BGError("DOWNLOAD_ERROR", exception.getMessage))
          case Success(_) => checksum(local, file.checksum) match {
            case Left(error) => Left(error)
            case Right(res) => if (res) Right(()) else Left(BGError("CHECKSUM_NOT_MATCHING", "Checksum error for " + file.name))
          }
        }
        case None => Left(BGError("NO_LOCAL_DEFINED", "Local destination file not defined for " + file.name))
      }
      case None => Left(BGError("NO_URL", "Not URL defined in the file to download => " + file.name))
    }
  }

  def fileDownloader(url: URL, filename: String): Try[Unit] = Try {
    url #> new File(filename) !!
  }

  def checksum(path: String, checksum: String): Either[BGError, Boolean] = {
    null
  }

}
