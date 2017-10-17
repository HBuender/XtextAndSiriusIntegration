package org.eclipse.xtext.example.domainmodel.ui.filter

import org.eclipse.core.resources.IFile
import org.eclipse.core.resources.IProject
import org.eclipse.jface.viewers.Viewer
import org.eclipse.jface.viewers.ViewerFilter
import org.eclipse.xtext.xbase.resource.BatchLinkableResource

/**
 * Hides everything except the aird files and their folder structure 
 */
class DomainModelExplorerExtendedFilter extends ViewerFilter {

	override select(Viewer viewer, Object parentElement, Object it) {
		switch it {
			IFile: fileExtension == "aird"
			BatchLinkableResource: return false
			IProject: open
			default: true
		}
	}

}
