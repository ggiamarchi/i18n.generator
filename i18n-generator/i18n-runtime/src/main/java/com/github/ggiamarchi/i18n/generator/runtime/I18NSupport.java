package com.github.ggiamarchi.i18n.generator.runtime;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class I18NSupport {

	private LocaleProvider localeProvider;
	
	public void setLocaleProvider(LocaleProvider localeProvider) {
		this.localeProvider = localeProvider;
	}

	public I18NSupport() {
		
	}
	
	public I18NSupport(final Locale locale) {
		localeProvider = new LocaleProvider() {
			@Override
			public Locale get() {
				return locale;
			}
		};
	}
	
	private ResourceBundle getLocalizedBundle() {
		if (localeProvider == null) {
			return ResourceBundle.getBundle(getBundleName());
		}
		Locale locale = localeProvider.get();
		if (locale == null) {
			return ResourceBundle.getBundle(getBundleName());
		}
		return ResourceBundle.getBundle(getBundleName(), locale);
	}

	protected final String getMessage(String key, String... valeurs) {
		return MessageFormat.format(getLocalizedBundle().getString(key), (Object[]) valeurs);
	}
	
	protected abstract String getBundleName();
	
}
