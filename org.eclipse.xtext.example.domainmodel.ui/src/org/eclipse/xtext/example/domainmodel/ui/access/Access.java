package org.eclipse.xtext.example.domainmodel.ui.access;

import static org.eclipse.xtext.example.domainmodel.ui.internal.DomainmodelActivator.*;
import static org.eclipse.xtext.util.Modules2.*;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.xtext.example.domainmodel.DomainmodelRuntimeModule;
import org.eclipse.xtext.example.domainmodel.ui.DomainmodelUiModule;
import org.eclipse.xtext.ui.shared.SharedStateModule;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;


public class Access extends AbstractAccess {

	private static Access INSTANCE = new Access();

	public static final String DOMAINMODEL = "org.eclipse.xtext.example.domainmodel.ui.Domainmodel";

	public static Access instance() {
		return INSTANCE;
	}

	protected Access() {
		super(DOMAINMODEL);
	}

	@Override
	protected Injector createInjector() {
		return Guice.createInjector(mixin(new DomainmodelRuntimeModule(), new SharedStateModule(), new DomainmodelUiModule(getInstance())));
	}



	public Provider<ILabelProvider> getILabelProvider() {
		return provider(ILabelProvider.class);
	}
	
	public Provider<IJvmModelAssociations> getJvmModelAssociations(){
		return provider(IJvmModelAssociations.class);
	}

}
