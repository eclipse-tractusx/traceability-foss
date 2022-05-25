package net.catenax.traceability.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TraceabilityController {

	/**
	 * To be removed. It's here just to preserve the project package structure.
	 */
	@GetMapping("/hello")
	public String helloWorld() {
		return "Hello World!";
	}

}
