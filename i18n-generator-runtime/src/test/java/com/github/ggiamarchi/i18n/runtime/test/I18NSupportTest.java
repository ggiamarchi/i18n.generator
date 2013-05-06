package com.github.ggiamarchi.i18n.runtime.test;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.github.ggiamarchi.i18n.generator.runtime.I18NSupport;
import com.github.ggiamarchi.i18n.generator.runtime.LocaleProvider;

public class I18NSupportTest {

	class I18NSupportTestImplementation extends I18NSupport {

	    public String bye() {
	        return getMessage("bye");
	    }

	    public String bye_to_firstname(String arg0, String arg1, String arg2) {
	        return getMessage("bye.to.firstname", arg0, arg1, arg2);
	    }

	    public String bye_to_firstname_lastname(String arg0, String arg1, String arg2, String arg3, String arg4) {
	        return getMessage("bye.to.firstname.lastname", arg0, arg1, arg2, arg3, arg4);
	    }

	    public String hello() {
	        return getMessage("hello");
	    }

	    public String hello_to_firstname(String arg0) {
	        return getMessage("hello.to.firstname", arg0);
	    }

	    public String hello_to_firstname_lastname(String arg0, String arg1) {
	        return getMessage("hello.to.firstname.lastname", arg0, arg1);
	    }

	    private static final String BUNDLE_NAME = "com.github.ggiamarchi.i18n.runtime.test.messages";

	    @Override
	    protected String getBundleName() {
	        return BUNDLE_NAME;
	    }

	    public I18NSupportTestImplementation() {

	    }

	    public I18NSupportTestImplementation(Locale locale) {
	        super(locale);
	    }

	}
	
	
	@Test
	public void testGenratedI18N() throws Exception {
		
		I18NSupportTestImplementation i18n = new I18NSupportTestImplementation();
		
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
