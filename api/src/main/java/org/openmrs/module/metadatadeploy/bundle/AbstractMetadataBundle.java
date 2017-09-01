/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.metadatadeploy.bundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.LocationAttributeType;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.OpenmrsObject;
import org.openmrs.Privilege;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.Role;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.MissingMetadataException;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.descriptor.EncounterTypeDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationAttributeDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationAttributeTypeDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationTagDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.PatientIdentifierTypeDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.PersonAttributeTypeDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.PrivilegeDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.ProgramWorkflowStateDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.RoleDescriptor;
import org.openmrs.module.metadatadeploy.source.ObjectSource;
import org.openmrs.module.metadatadeploy.sync.MetadataSynchronizationRunner;
import org.openmrs.module.metadatadeploy.sync.ObjectSynchronization;
import org.openmrs.module.metadatadeploy.sync.SyncResult;
import org.openmrs.module.metadatasharing.ImportMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.location;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.locationAttribute;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.packageFile;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.programWorkflowState;

/**
 * Abstract base class for metadata bundle components
 */
public abstract class AbstractMetadataBundle implements MetadataBundle {

	public static final String SYSTEM_PROPERTY_SKIP_METADATA_SHARING_PACKAGE_REFRESH = "skipMetadataSharingPackageRefresh";

	protected Log log = LogFactory.getLog(getClass());

	@Autowired
	protected PlatformTransactionManager platformTransactionManager;

	@Autowired
	@Qualifier("adminService")
	protected AdministrationService administrationService;

	@Autowired
	protected MetadataDeployService deployService;

	/**
	 * Installs the given metadata package
	 * @param pkg the incoming package
	 * @return the installed object
	 */
	protected void install(PackageDescriptor pkg) {
		ClassLoader loader = pkg.getClassLoader() != null ? pkg.getClassLoader() : this.getClass().getClassLoader();
        ImportMode importMode = pkg.getImportMode() != null ? pkg.getImportMode() : ImportMode.MIRROR;
		deployService.installPackage(pkg.getFilename(), loader, pkg.getGroupUuid(), importMode);
	}

	/**
	 * Installs the given object
	 * @param incoming the incoming object
	 * @return the installed object
	 */
	protected <T extends OpenmrsObject> T install(T incoming) {
		return deployService.installObject(incoming);
	}

	/**
	 * Installs all objects from the given source
	 * @param source the object source
	 * @return the installed objects
	 */
	protected <T extends OpenmrsObject> List<T> install(ObjectSource<T> source) {
		return deployService.installFromSource(source);
	}

	/**
	 * Uninstalls the given object. The object can be null in which case the method does nothing.
	 * @param outgoing the outgoing object
	 * @param reason the reason for uninstallation
	 */
	protected <T extends OpenmrsObject> void uninstall(T outgoing, String reason) {
		// We allow passing in of null values such as return value from existing(...)
		if (outgoing != null) {
			deployService.uninstallObject(outgoing, reason);
		}
	}

	/**
	 * Performs the given synchronization operation
	 * @param source the object source
	 * @param sync the synchronization operation
	 * @return the synchronization result
	 */
	protected <T extends OpenmrsMetadata> SyncResult<T> sync(ObjectSource<T> source, ObjectSynchronization<T> sync) {
		MetadataSynchronizationRunner<T> runner = new MetadataSynchronizationRunner<T>(source, sync);
		return runner.run();
	}

	/**
	 * Fetches a possibly existing object (non fail-fast)
	 * @param clazz the object's class
	 * @param identifier the object's identifier
	 * @return the object or null
	 */
	protected <T extends OpenmrsObject> T possible(Class<T> clazz, String identifier) {
		return deployService.fetchObject(clazz, identifier);
	}

	/**
	 * Fetches an existing object (fail-fast)
	 * @param clazz the object's class
	 * @param identifier the object's identifier
	 * @return the object
	 * @throws org.openmrs.module.metadatadeploy.MissingMetadataException if object doesn't exist
	 */
	protected <T extends OpenmrsObject> T existing(Class<T> clazz, String identifier) {
		T obj = deployService.fetchObject(clazz, identifier);
		if (obj == null) {
			throw new MissingMetadataException(clazz, identifier);
		}
		return obj;
	}

	/**
	 * Some helpful utility methods
	 */

	/**
	 * Setting multiple GPs is much faster in a single transaction
	 */
	protected void setGlobalProperties(final Map<String, String> properties) {
		TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				for (Map.Entry<String, String> entry : properties.entrySet()) {
					setGlobalProperty(entry.getKey(), entry.getValue());
				}
			}
		});
	}

	/**
	 * Update the global property with the given name to the given value, creating it if it doesn't exist
	 */
	protected void setGlobalProperty(String propertyName, String propertyValue) {
		AdministrationService administrationService = Context.getAdministrationService();
		GlobalProperty gp = administrationService.getGlobalPropertyObject(propertyName);
		if (gp == null) {
			gp = new GlobalProperty(propertyName);
		}
		gp.setPropertyValue(propertyValue);
		administrationService.saveGlobalProperty(gp);
	}

	/**
	 * Utility method that verifies that a particular concept mapping is set up correctly
	 * @param conceptCode
	 * @param conceptSource
	 */
	protected void verifyConceptPresent(String conceptCode, String conceptSource) {
		if (Context.getConceptService().getConceptByMapping(conceptCode, conceptSource) == null) {
			throw new RuntimeException("No concept tagged with code " + conceptCode + " from source " + conceptSource);
		}
	}

	/**
	 * Utility method to install an MDS package if it hasn't been disabled by a system property
	 * @param filename
	 * @param groupUuid
	 * @return true if the package was installed
	 */
	protected boolean installMetadataSharingPackage(String filename, String groupUuid) {
		String systemProperty = System.getProperty(SYSTEM_PROPERTY_SKIP_METADATA_SHARING_PACKAGE_REFRESH, "false");
		if (Boolean.parseBoolean(systemProperty)) {
			log.warn("Skipping refresh of MDS package: " + filename);
			return false;
		}
		log.warn("Installing Metadata Sharing package: " + filename);
		install(packageFile(filename, null, groupUuid));
		return true;
	}

	protected void install(EncounterTypeDescriptor d) {
		install(encounterType(d.name(), d.description(), d.uuid()));
	}

	protected void install(LocationAttributeTypeDescriptor d) {
		LocationAttributeType type = CoreConstructors.locationAttributeType(d.name(), d.description(), d.datatype(), d.datatypeConfig(), d.minOccurs(), d.maxOccurs(), d.uuid());
		install(type);
	}

	protected void install(LocationDescriptor location) {

		// First install the location and it's tags
		String parentUuid = location.parent() == null ? null : location.parent().uuid();
		List<String> tagUuids = new ArrayList<String>();
		if (location.tags() != null) {
			for (LocationTagDescriptor tagDescriptor : location.tags()) {
				tagUuids.add(tagDescriptor.uuid());
			}
		}
		install(location(location.name(), location.description(), location.uuid(), parentUuid, tagUuids));

		// Then, install the location attribute(s) if applicable
		if (location.attributes() != null) {
			for (LocationAttributeDescriptor lad : location.attributes()) {
				if (!lad.location().uuid().equals(location.uuid())) {
					throw new IllegalStateException("Location Attribute with uuid " + lad.uuid() + " is configured with a different location than it the Location it is associated with");
				}
				install(locationAttribute(lad.location().uuid(), lad.type().uuid(), lad.value(), lad.uuid()));
			}
		}
	}

	protected void install(LocationTagDescriptor d) {
		install(CoreConstructors.locationTag(d.name(), d.description(), d.uuid()));
	}

	protected void install(PatientIdentifierTypeDescriptor d) {
		install(CoreConstructors.patientIdentifierType(d.name(), d.description(), d.format(), d.formatDescription(), d.validator(), d.locationBehavior(), d.required(), d.uuid()));
	}

	protected void install(PersonAttributeTypeDescriptor d) {
		install(CoreConstructors.personAttributeType(d.name(), d.description(), d.format(), d.foreignKey(), d.searchable(), d.sortWeight(), d.uuid()));
	}


	protected void install(PrivilegeDescriptor d) {
		install(CoreConstructors.privilege(d.privilege(), d.description(), d.uuid()));
	}

	protected void install(RoleDescriptor d) {
		Role obj = new Role();
		obj.setUuid(d.uuid());
		obj.setRole(d.role());
		obj.setDescription(d.description());
		if (d.inherited() != null) {
			Set<Role> inheritedRoles = new HashSet<Role>();
			for (RoleDescriptor rd : d.inherited()) {
				inheritedRoles.add(MetadataUtils.existing(Role.class, rd.uuid()));
			}
			obj.setInheritedRoles(inheritedRoles);
		}
		if (d.privileges() != null) {
			Set<Privilege> privileges = new HashSet<Privilege>();
			for (PrivilegeDescriptor pd : d.privileges()) {
				privileges.add(MetadataUtils.existing(Privilege.class, pd.privilege()));
			}
			obj.setPrivileges(privileges);
		}
		install(obj);
	}

	/**
	 * Utility method to install a program metadata in an openmrs insatnce
	 * @param d
     */
	protected void install(ProgramDescriptor d) {

		// create any workflows and states

		Set<ProgramWorkflow> workflows = new HashSet<ProgramWorkflow>();;

		if (d.workflows() != null && d.workflows().size() > 0) {

			for (ProgramWorkflowDescriptor workflow : d.workflows()) {

				Set<ProgramWorkflowState> states = new HashSet<ProgramWorkflowState>();

				if (workflow.states() != null && workflow.states().size() > 0) {

					for (ProgramWorkflowStateDescriptor state : workflow.states()) {
						states.add(programWorkflowState(state.conceptUuid(), state.initial(), state.terminal(), state.uuid()));
					}
				}

				workflows.add(CoreConstructors.programWorkflow(workflow.conceptUuid(), workflow.uuid(), states));
			}
		}


		// then install the program
		install(CoreConstructors.program(d.name(), d.description(), d.conceptUuid(), d.outcomesConceptUuid(), d.uuid(), workflows));


	}
}