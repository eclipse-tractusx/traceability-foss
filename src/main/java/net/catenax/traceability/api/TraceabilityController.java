package net.catenax.traceability.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class TraceabilityController {

    /**
     * To be removed. It's here just to preserve the project package structure.
     */
    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello World!";
    }

}
