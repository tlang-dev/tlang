package dev.tlang.tlang.loader

import dev.tlang.tlang.ast.DomainModel

case class Resource(rootDir: String, fromRoot:String,  pkg: String, name: String, ast: DomainModel)
