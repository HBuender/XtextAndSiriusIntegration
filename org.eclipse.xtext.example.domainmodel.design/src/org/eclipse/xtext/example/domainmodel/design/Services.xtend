package org.eclipse.xtext.example.domainmodel.design

import org.eclipse.emf.ecore.EObject
import org.eclipse.jface.viewers.ILabelProvider
import org.eclipse.sirius.diagram.DNodeContainer
import org.eclipse.sirius.diagram.DSemanticDiagram
import org.eclipse.xtext.common.types.JvmIdentifiableElement
import org.eclipse.xtext.example.domainmodel.domainmodel.DomainModel
import org.eclipse.xtext.example.domainmodel.domainmodel.Entity
import org.eclipse.xtext.example.domainmodel.domainmodel.Operation
import org.eclipse.xtext.example.domainmodel.domainmodel.Property
import org.eclipse.xtext.example.domainmodel.ui.access.Access
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations

import static extension org.eclipse.xtext.EcoreUtil2.*
import java.util.List

/** 
 * The services class used by VSM.
 */
class Services {

	extension ILabelProvider labelProvider

	extension IJvmModelAssociations jvmModelAssociations

	new() {
		val access = Access.instance
		this.labelProvider = access.getILabelProvider.get
		this.jvmModelAssociations = access.jvmModelAssociations.get
	}

	def List<Property> getProperties(Entity it) {
		val retList = newArrayList()
		if (superType !== null) {
			if (superType.type.toEObject(Entity) !== null) {
				retList += superType.type.toEObject(Entity).properties
			}
		}
		retList += features.filter(Property).toList
		return retList
	}

	def getOperations(Entity it) {
		features.filter(Operation).toList
	}

	def getContainedEntities(Entity it) {
		val referencedEntities = it.properties.filter[property|property.targetEntity != it].map [property|
			property.targetEntity
		].filterNull.toList
		val referencingEntities = (getContainerOfType(DomainModel).eAllOfType(Entity).filter [entity|
			entity.properties.filter [ property |
				property.targetEntity == it
			].size > 0
		])
		return (referencedEntities+referencingEntities).toList
	}

	def getAggregates(DomainModel it) {
		eAllOfType(Entity).filter[aggregate].toList
	}

	def getSize(Entity it) {
		name.length * 3
	}

	def getPropertyName(Property it) {
		text
	}

	def getOperationName(Operation it) {
		text
	}

	def getSourceEntity(Property it) {
		return getContainerOfType(Entity)
	}

	def getTargetEntity(Property it) {
		type.type.toEObject(Entity)
	}

	def private <T extends EObject> toEObject(JvmIdentifiableElement it, Class<T> type) {
		jvmModelAssociations.getSourceElements(it).filter(type).head
	}

	def getAllProperties(DomainModel it) {
		eAllOfType(Property)
	}

	def isEmptyPropertySection(Entity it) {
		!(properties.size < 1)
	}

	def isEmptyOperationSection(Entity it) {
		!(operations.size < 1)
	}

	def hasNoStringProperty(EObject it) {
		val elementContainer = getContainerOfType(DNodeContainer)
		val entity = elementContainer.target
		if (entity instanceof Entity) {
			return entity.properties.findFirst[type.type.getQualifiedName(".").equals("java.lang.String")] !== null
		}
		return true
	}

	def isUnlinked(EObject it) {
		val elementContainer = getContainerOfType(DNodeContainer)
		val diagramContainer = getContainerOfType(DSemanticDiagram)
		if (elementContainer === null || diagramContainer === null) {
			return true
		}

		val entity = elementContainer.target
		if (entity instanceof Entity) {
			if (entity.properties.findFirst[type.type.toEObject(Entity) !== null] !== null) {
				return true
			}
			val rs = entity.eResource.resourceSet
			return entity.getContainerOfType(DomainModel).eAllOfType(Property).findFirst [
				type.type.toEObject(Entity) == entity
			] !== null

		}
		return true

	}

	def getReferencingProperties(DomainModel it) {
		eAllOfType(Property).filter(prop|prop.type.type.toEObject(Entity) !== null).toList
	}

	def findExtends(Entity it) {
		getContainerOfType(DomainModel).eAllOfType(Entity).findFirst [localEntity|
			localEntity.name == superType.simpleName
		]
	}

	def findRelated(Entity it) {
		emptyList
	}

	def highNumberOfProperties(Entity it) {
		properties.size > 10
	}

	def getName(EObject it) {
		labelProvider.getText(it)
	}

	def isFromDifferentResource(EObject it, EObject view) {
		view.getContainerOfType(DSemanticDiagram).target != getContainerOfType(DomainModel)
	}

}
