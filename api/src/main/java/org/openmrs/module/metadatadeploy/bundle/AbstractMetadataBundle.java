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

import org.openmrs.OpenmrsMetadata;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.metadatadeploy.MissingMetadataException;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.source.ObjectSource;
import org.openmrs.module.metadatadeploy.sync.MetadataSynchronizationRunner;
import org.openmrs.module.metadatadeploy.sync.ObjectSynchronization;
import org.openmrs.module.metadatadeploy.sync.SyncResult;
import org.openmrs.module.metadatasharing.ImportMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Abstract base class for metadata bundle components
 */
public abstract class AbstractMetadataBundle implements MetadataBundle {

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
}