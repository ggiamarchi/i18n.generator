package com.github.ggiamarchi.i18n.test;

import static org.junit.Assert.*;
import org.junit.Test;

import com.github.ggiamarchi.i18n.I18NGeneratorException;

public class I18NGeneratorExceptionTest {

	@Test
	public void constructI18NGeneratorExceptionTest() {
	
		I18NGeneratorException e;
		RuntimeException re;
		
		e = new I18NGeneratorException("a simple massage");
		assertEquals("a simple massage", e.getMessage());

		re = new RuntimeException("this is a RuntimeException");
		e = new I18NGeneratorException("a simple massage", re);
		assertEquals("a simple massage", e.getMessage());
		assertEquals(re, e.getCause());
		
		re = new RuntimeException("this is a RuntimeException");
		e = new I18NGeneratorException(re);
		assertEquals(re, e.getCause());
		assertTrue(e.getMessage().contains(re.getMessage()));
		
	}	
	
}
