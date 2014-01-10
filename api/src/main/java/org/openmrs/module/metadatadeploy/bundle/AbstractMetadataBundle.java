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

import org.openmrs.OpenmrsObject;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.source.ObjectSource;
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

		deployService.installPackage(pkg.getFilename(), loader, pkg.getGroupUuid());
	}

	/**
	 * Installs the given object
	 * @param incoming the incoming object
	 * @return the installed object
	 */
	protected OpenmrsObject install(OpenmrsObject incoming) {
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
	protected void uninstall(OpenmrsObject outgoing, String reason) {
		if (outgoing != null) {
			deployService.uninstallObject(outgoing, reason);
		}
	}

	/**
	 * Fetches an existing object if it exists
	 * @param clazz the object's class
	 * @param uuid the object's UUID
	 */
	protected <T extends OpenmrsObject> T existing(Class<T> clazz, String uuid) {
		return deployService.fetchObject(clazz, uuid);
	}
}