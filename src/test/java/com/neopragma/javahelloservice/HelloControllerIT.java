package com.neopragma.javahelloservice;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import com.neopragma.javahellolib.Hello;

@Category(IntegrationTest.class)
public class HelloControllerIT {
	
	@Test
	public void itReturnsHelloWorld() {
		HelloController controller = new HelloController(new Hello());
		assertEquals("Hello, World!", controller.greeting());
	}

}
