package io.sorne.tlang.loader

import io.sorne.tlang.ast.DomainModel

case class Resource(rootDir: String, fromRoot:String,  pkg: String, name: String, ast: DomainModel)
