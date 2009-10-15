
/*
 * generated by Xtext
 */
package org.eclipse.b3.internal;

import org.eclipse.xtext.ui.common.service.UIPluginModule;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.Map;
import java.util.HashMap;

/**
 * Generated
 */
public class BeeLangActivator extends AbstractUIPlugin {

	private Map<String,Injector> injectors = new HashMap<String,Injector>();
	private static BeeLangActivator INSTANCE;

	public Injector getInjector(String languageName) {
		return injectors.get(languageName);
	}
	
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		INSTANCE = this;
		
		injectors.put("org.eclipse.b3.BeeLang", Guice.createInjector(
			new org.eclipse.b3.BeeLangUiModule(),
			createUIPluginModule()
		));
		
	}
	
	public static BeeLangActivator getInstance() {
		return INSTANCE;
	}
	
	protected UIPluginModule createUIPluginModule() {
		return new UIPluginModule(this);
	}
	
}
