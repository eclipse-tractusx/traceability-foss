package net.catenax.traceability.common.support

import com.xebialabs.restito.server.StubServer

interface RestitoProvider {
	StubServer stubServer()
}
