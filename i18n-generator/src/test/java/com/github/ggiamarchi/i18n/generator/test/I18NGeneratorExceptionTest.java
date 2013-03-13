package com.github.ggiamarchi.i18n.generator.test;

import static org.junit.Assert.*;
import org.junit.Test;

import com.github.ggiamarchi.i18n.generator.GeneratorException;

public class I18NGeneratorExceptionTest {

	@Test
	public void constructI18NGeneratorExceptionTest() {
	
		GeneratorException e;
		RuntimeException re;
		
		e = new GeneratorException("a simple massage");
		assertEquals("a simple massage", e.getMessage());

		re = new RuntimeException("this is a RuntimeException");
		e = new GeneratorException("a simple massage", re);
		assertEquals("a simple massage", e.getMessage());
		assertEquals(re, e.getCause());
		
		re = new RuntimeException("this is a RuntimeException");
		e = new GeneratorException(re);
		assertEquals(re, e.getCause());
		assertTrue(e.getMessage().contains(re.getMessage()));
		
	}	
	
}
