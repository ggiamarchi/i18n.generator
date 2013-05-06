package com.test.i18n;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.github.ggiamarchi.i18n.generator.runtime.LocaleProvider;

public class TestI18N {

	@Test
	public void testGenratedI18N() throws Exception {
		
		MessagesImpl i18n = new MessagesImpl();
		
		Assert.assertEquals("Bonjour", i18n.hello());
		Assert.assertEquals("Bonjour Guillaume", i18n.hello_to_firstname("Guillaume"));
		Assert.assertEquals("Bonjour Guillaume Giamarchi", i18n.hello_to_firstname_lastname("Guillaume", "Giamarchi"));
		Assert.assertEquals("bye Guillaume", i18n.bye_to_firstname(null, null, "Guillaume"));
		Assert.assertEquals("bye Guillaume Giamarchi", i18n.bye_to_firstname_lastname(null, null, "Guillaume", null, "Giamarchi"));

		
		i18n.setLocaleProvider(new LocaleProvider() {
			@Override
			public Locale get() {
				return null;
			}
		});

		Assert.assertEquals("Bonjour", i18n.hello());
		
		
		i18n.setLocaleProvider(new LocaleProvider() {
			@Override
			public Locale get() {
				return new Locale("es");
			}
		});

		Assert.assertEquals("Hola", i18n.hello());

		
		i18n.setLocaleProvider(new LocaleProvider() {
			@Override
			public Locale get() {
				return new Locale("en", "EN");
			}
		});

		Assert.assertEquals("Hello", i18n.hello());

		
		i18n.setLocaleProvider(new LocaleProvider() {
			@Override
			public Locale get() {
				return new Locale("en", "US");
			}
		});

		Assert.assertEquals("Bonjour", i18n.hello());

	}
	

}
