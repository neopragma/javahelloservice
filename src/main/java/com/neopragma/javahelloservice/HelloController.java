package com.neopragma.javahelloservice;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.neopragma.javahellolib.Hello;

@RestController
public class HelloController {
	
	private Hello hello;
	
	public HelloController(Hello hello) {
		this.hello = hello;
	}
 
    @RequestMapping(method=RequestMethod.GET, value={"/greeting"})
    public String greeting() {
        return hello.greet();
    }
}