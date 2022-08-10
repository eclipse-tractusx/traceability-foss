package net.catenax.traceability.common.support

import com.xebialabs.restito.semantics.Action
import org.springframework.http.HttpHeaders

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp
import static com.xebialabs.restito.semantics.Action.header
import static com.xebialabs.restito.semantics.Action.ok
import static com.xebialabs.restito.semantics.Action.resourceContent
import static com.xebialabs.restito.semantics.Condition.get
import static com.xebialabs.restito.semantics.Condition.post
import static com.xebialabs.restito.semantics.Condition.withHeader

trait IrsApiSupport implements RestitoProvider {

	void irsApiTriggerJob() {
		whenHttp(stubServer()).match(
			post("/irs/jobs"),
			withHeader(HttpHeaders.AUTHORIZATION)
		).then(
				ok(),
				header("Content-Type", "application/json"),
				jsonResponseFromFile("./stubs/irs/post/jobs/response_200.json")
			)
	}

	void irsApiReturnsJobDetails() {
		whenHttp(stubServer()).match(
			get("/irs/jobs/ebb79c45-7bba-4169-bf17-3e719989ab54"),
			withHeader(HttpHeaders.AUTHORIZATION)
		)
			.then(
				ok(),
				header("Content-Type", "application/json"),
				jsonResponseFromFile("./stubs/irs/get/jobs/jobId/response_200.json")
			)
	}

	private Action jsonResponseFromFile(String location) {
		return resourceContent(location)
	}
}
