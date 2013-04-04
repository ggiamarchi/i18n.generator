package com.github.ggiamarchi.i18n.generator.test.unit;


import org.junit.Assert;
import org.junit.Test;

import com.github.ggiamarchi.i18n.generator.Main;

public class MainTest {

	
	@Test
	public void getArgsTest() {
		Main main = new Main();
		
		main.getArgs(new String [] {
				"-bundle", "aze",
				"-dir", "rty",
				"-output", "qwe",
				"-noimpl",
				"-interface", "@int",
				"-class", "@class",
		});
		
		Assert.assertEquals("aze", main.getBundle());
		Assert.assertEquals("rty", main.getDir());
		Assert.assertEquals("qwe", main.getOutput());
		Assert.assertTrue(main.isNoimpl());
		Assert.assertEquals("@int", main.getInterfaceName());
		Assert.assertEquals("@class", main.getClassName());
		
	}
	
}
