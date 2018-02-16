package io.egia.mqi.web;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {

	@RequestMapping("/greetings")
	public String greetings() {
		return "Greetings from Spring Boot!";
	}

}