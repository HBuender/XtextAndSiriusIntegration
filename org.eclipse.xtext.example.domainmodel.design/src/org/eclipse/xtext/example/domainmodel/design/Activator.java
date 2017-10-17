package org.eclipse.xtext.example.domainmodel.design;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.sirius.business.api.componentization.ViewpointRegistry;
import org.eclipse.sirius.viewpoint.description.Viewpoint;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.example.domainmodel.DomainmodelRuntimeModule;
import org.eclipse.xtext.example.domainmodel.ui.DomainmodelUiModule;
import org.eclipse.xtext.ui.shared.SharedStateModule;
import org.eclipse.xtext.util.Modules2;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String ORG_ECLIPSE_XTEXT_EXAMPLE_DOMAINMODEL_UI_DOMAINMODEL = "org.eclipse.xtext.example.domainmodel.ui.Domainmodel.Domainmodel";

	private static Activator INSTANCE;

	private static final Logger logger = Logger.getLogger(Activator.class);

	private static Activator plugin;

	public static final String PLUGIN_ID = "org.eclipse.xtext.example.domainmodel.design";

	private static Set<Viewpoint> viewpoints;

	public static Activator getDefault() {
		return plugin;
	}

	public static Activator getInstance() {
		return INSTANCE;
	}

	private Map<String, Injector> injectors = Collections
			.synchronizedMap(Maps.<String, Injector>newHashMapWithExpectedSize(1));

	public Activator() {
	}

	protected Injector createInjector(String language) {
		try {
			Module runtimeModule = getRuntimeModule(language);
			Module sharedStateModule = getSharedStateModule();
			Module uiModule = getUiModule(language);
			Module mergedModule = Modules2.mixin(runtimeModule, sharedStateModule, uiModule);
			return Guice.createInjector(mergedModule);
		} catch (Exception e) {
			logger.error("Failed to create injector for " + language);
			logger.error(e.getMessage(), e);
			throw new RuntimeException("Failed to create injector for " + language, e);
		}
	}

	public Injector getInjector(String language) {
		synchronized (injectors) {
			Injector injector = injectors.get(language);
			if (injector == null) {
				injectors.put(language, injector = createInjector(language));
			}
			return injector;
		}
	}

	protected Module getRuntimeModule(String grammar) {
		if (ORG_ECLIPSE_XTEXT_EXAMPLE_DOMAINMODEL_UI_DOMAINMODEL.equals(grammar)) {
			return new DomainmodelRuntimeModule();
		}

		throw new IllegalArgumentException(grammar);
	}

	protected Module getSharedStateModule() {
		return new SharedStateModule();
	}

	protected Module getUiModule(String grammar) {
		if (ORG_ECLIPSE_XTEXT_EXAMPLE_DOMAINMODEL_UI_DOMAINMODEL.equals(grammar)) {
			return (Module) new DomainmodelUiModule(this);
		}

		throw new IllegalArgumentException(grammar);
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		viewpoints = new HashSet<Viewpoint>();
		viewpoints.addAll(
				ViewpointRegistry.getInstance().registerFromPlugin(PLUGIN_ID + "/description/domainmodel.odesign"));
		INSTANCE = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		injectors.clear();
		INSTANCE = null;
		if (viewpoints != null) {
			for (final Viewpoint viewpoint : viewpoints) {
				ViewpointRegistry.getInstance().disposeFromPlugin(viewpoint);
			}
			viewpoints.clear();
			viewpoints = null;
		}
		super.stop(context);
	}

}
