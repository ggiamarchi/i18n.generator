package com.github.ggiamarchi.i18n.generator.test.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.ggiamarchi.i18n.generator.GeneratorLauncher;

public class LauncherTest {

	@Test
	public void normalizeFolderPathTest() {
		assertEquals("/path/to/directory/", GeneratorLauncher.normalizeFolderPath("/path/to/directory"));
		assertEquals("/path/to/directory/", GeneratorLauncher.normalizeFolderPath("/path/to/directory/"));
		assertEquals("/path/to/directory/", GeneratorLauncher.normalizeFolderPath("///path/to//directory"));
		assertEquals("/path/to/directory/", GeneratorLauncher.normalizeFolderPath("/path///to/directory//"));
		assertEquals("C:/path/to/directory/", GeneratorLauncher.normalizeFolderPath("C:\\path\\to\\directory"));
		assertEquals("C:/path/to/directory/", GeneratorLauncher.normalizeFolderPath("C:\\path\\to\\directory\\"));
		assertEquals("C:/path/to/directory/", GeneratorLauncher.normalizeFolderPath("C:\\\\path\\/\\to\\//directory/\\"));
		assertEquals("C:/path/to/directory/", GeneratorLauncher.normalizeFolderPath("C:/\\path\\/to\\directory"));
	}
	
}
