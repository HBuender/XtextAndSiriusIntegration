package org.eclipse.xtext.example.domainmodel.ui.filter

import org.eclipse.jface.viewers.Viewer
import org.eclipse.jface.viewers.ViewerFilter
import org.eclipse.sirius.common.ui.tools.api.navigator.GroupingItem
import org.eclipse.xtext.common.types.access.TypeResource
import org.eclipse.xtext.example.domainmodel.domainmodel.DomainModel

/**
 * Hides all elements from the Model Explorer that are not defined directly in the DSL, especially JVMTypeResources.
 */
class DomainModelExplorerFilter extends ViewerFilter {

	override select(Viewer viewer, Object parentElement, Object it) {
		switch it {
			TypeResource:
				return false
			GroupingItem:
				(children.size - children.filter(TypeResource).size) > 0
			DomainModel:
				return false
			default:
				return true
		}
	}

}
