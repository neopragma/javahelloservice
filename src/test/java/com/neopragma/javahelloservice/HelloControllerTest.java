package com.neopragma.javahelloservice;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.neopragma.javahellolib.Hello;

@RunWith(MockitoJUnitRunner.class)
public class HelloControllerTest {
	
	@Mock Hello hello;
	
	@Test
	public void itRetrievesTheGreeting() {
		HelloController controller = new HelloController(hello);
		controller.greeting();
		verify(hello).greet();
	}

}
