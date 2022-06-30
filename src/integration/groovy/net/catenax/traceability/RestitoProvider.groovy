package net.catenax.traceability

import com.xebialabs.restito.server.StubServer

interface RestitoProvider {
	StubServer stubServer()
}
