package dev.tlang.tlang.loader.remote

import dev.tlang.tlang.loader.manifest.Dependency
import dev.tlang.tlang.loader.manifest.Dependency

import java.nio.file.Path
import scala.util.Try

trait RemoteLoader {

  def fetchRepoManifest(dependency: Dependency): Try[RepoManifest]

  def fetchTBag(dependency: Dependency, manifest: RepoManifest, dest: Path): Try[Path]

}

object RemoteLoader extends RemoteLoader {

  override def fetchRepoManifest(dependency: Dependency): Try[RepoManifest] = ???

  override def fetchTBag(dependency: Dependency, manifest: RepoManifest, dest: Path): Try[Path] = ???
}
