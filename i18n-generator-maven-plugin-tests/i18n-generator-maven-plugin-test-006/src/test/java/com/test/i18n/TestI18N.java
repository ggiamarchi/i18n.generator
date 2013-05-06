package com.test.i18n;

import java.util.Locale;

import messages.i18n.impl.HiImpl;

import org.junit.Assert;
import org.junit.Test;

import com.github.ggiamarchi.i18n.generator.runtime.LocaleProvider;

public class TestI18N {

	@Test
	public void testGenratedI18N() throws Exception {
		
		HiImpl hi = new HiImpl();
		ByeImpl bye = new ByeImpl();
		
		Assert.assertEquals("Bonjour", hi.hello());
		Assert.assertEquals("Bonjour Guillaume", hi.hello_to_firstname("Guillaume"));
		Assert.assertEquals("Bonjour Guillaume Giamarchi", hi.hello_to_firstname_lastname("Guillaume", "Giamarchi"));
		Assert.assertEquals("bye Guillaume", bye.bye_to_firstname(null, null, "Guillaume"));
		Assert.assertEquals("bye Guillaume Giamarchi", bye.bye_to_firstname_lastname(null, null, "Guillaume", null, "Giamarchi"));

		
		hi.setLocaleProvider(new LocaleProvider() {
			@Override
			public Locale get() {
				return null;
			}
		});

		Assert.assertEquals("Bonjour", hi.hello());
		
		
		hi.setLocaleProvider(new LocaleProvider() {
			@Override
			public Locale get() {
				return new Locale("en", "EN");
			}
		});

		Assert.assertEquals("Hello", hi.hello());

		
		hi.setLocaleProvider(new LocaleProvider() {
			@Override
			public Locale get() {
				return new Locale("en", "US");
			}
		});

		Assert.assertEquals("Bonjour", hi.hello());

	}
	

}
