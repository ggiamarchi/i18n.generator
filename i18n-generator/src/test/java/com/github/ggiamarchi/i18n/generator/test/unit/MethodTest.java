package com.github.ggiamarchi.i18n.generator.test.unit;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.github.ggiamarchi.i18n.generator.Method;

public class MethodTest {

	@Test
	public void methodInnerClassTestOk() {

		Method m;
		
		m = new Method("a", "b");
		assertEquals("a", m.getProperty());
		assertEquals("b", m.getName());
		assertArrayEquals(new String [] {}, m.getParameters());
		
		m = new Method("a", "b", "c");
		assertEquals("a", m.getProperty());
		assertEquals("b", m.getName());
		assertArrayEquals(new String [] { "c" }, m.getParameters());

		m = new Method("a", "b", "c", "d");
		assertEquals("a", m.getProperty());
		assertEquals("b", m.getName());
		assertArrayEquals(new String [] { "c", "d" }, m.getParameters());
		
	}

	@Test
	public void methodInnerClassTestPropertyNull() {
		try {
			new Method(null, "b");
		}
		catch (IllegalArgumentException e) {
			return;
		}
		fail("Method contructor with null value for property 'property' should raise an exception");
	}

	@Test
	public void methodInnerClassTestPropertyEmpty() {
		try {
			new Method("", "b");
		}
		catch (IllegalArgumentException e) {
			return;
		}
		fail("Method contructor with empty value for property 'property' should raise an exception");
	}

	@Test
	public void methodInnerClassTestNameNull() {
		try {
			new Method("a", null);
		}
		catch (IllegalArgumentException e) {
			return;
		}
		fail("Method contructor with null value for property 'name' should raise an exception");
	}

	@Test
	public void methodInnerClassTestNameEmpty() {
		try {
			new Method("a", "");
		}
		catch (IllegalArgumentException e) {
			return;
		}
		fail("Method contructor with empty value for property 'name' should raise an exception");
	}

	@Test
	public void methodInnerClassTestEquals() {
		assertTrue(new Method("a", "b").equals(new Method("a", "b")));
		assertTrue(new Method("a", "b", "c").equals(new Method("a", "b", "c")));
		assertTrue(new Method("a", "b", "c", "d").equals(new Method("a", "b", "c", "d")));
		assertFalse(new Method("a", "b").equals(new Method("a", "b", "c")));		
	}

	@Test
	public void methodInnerClassTestHashcode() {
		assertEquals(new Method("a", "b").hashCode(), new Method("a", "b").hashCode());
		assertEquals(new Method("a", "b", "c").hashCode(), new Method("a", "b", "c").hashCode());
		assertEquals(new Method("a", "b", "c", "d").hashCode(), new Method("a", "b", "c", "d").hashCode());
		assertNotEquals(new Method("a", "b").hashCode(), new Method("a", "b", "c").hashCode());		
	}

}
