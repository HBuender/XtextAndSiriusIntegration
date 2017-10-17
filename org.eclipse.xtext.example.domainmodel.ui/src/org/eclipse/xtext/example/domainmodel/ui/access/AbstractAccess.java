package org.eclipse.xtext.example.domainmodel.ui.access;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;

public abstract class AbstractAccess {

	private static class InternalProvider<T> implements Provider<T> {

		private final Class<? extends T> clazz;

		private final Injector injector;

		public InternalProvider(Injector injector, Class<? extends T> clazz) {
			super();
			this.injector = injector;
			this.clazz = clazz;
		}

		public T get() {
			return injector.getInstance(clazz);
		}

	}

	public static class Runtime {

		private static Multimap<String, Module> MODULES = HashMultimap.create();

		public static void register(String key, Module... modules) {
			if (MODULES.containsKey(key)) {
				LOGGER.warn(key + " is already registered!");
			}
			MODULES.putAll(key, newArrayList(modules));
		}

	}

	private static Map<String, Injector> INJECTORS = newHashMapWithExpectedSize(4);

	private static final Logger LOGGER = Logger.getLogger(AbstractAccess.class);

	private final String key;

	protected AbstractAccess(String key) {
		this.key = key;
	}

	protected abstract Injector createInjector();

	private Injector getInjector() {
		Injector injector = INJECTORS.get(key);
		if (injector == null) {
			Collection<Module> modules = newArrayList(Runtime.MODULES.get(key));
			if (modules.isEmpty()) {
				injector = createInjector();
			} else {
				Runtime.MODULES.removeAll(key);
				injector = Guice.createInjector(modules);
			}
			INJECTORS.put(key, injector);
		}
		return injector;
	}

	protected final <T> Provider<T> provider(Class<? extends T> clazz) {
		return new InternalProvider<T>(getInjector(), clazz);
	}

}