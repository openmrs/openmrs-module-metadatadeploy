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

package org.openmrs.module.metadatadeploy.api;

import org.openmrs.OpenmrsObject;
import org.openmrs.api.APIException;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Service for metadata deployment
 */
@Transactional
public interface MetadataDeployService {

	/**
	 * Installs a collection of bundles
	 * @param bundles the bundles
	 */
	public void installBundles(Collection<MetadataBundle> bundles);

	/**
	 * Installs a MDS package if it has not been installed yet or the installed version is out of date
	 * @param filename the package filename
	 * @param loader the class loader to use for loading the package
	 * @param groupUuid the package group UUID
	 * @return whether package was installed
	 */
	public boolean installPackage(String filename, ClassLoader loader, String groupUuid) throws APIException;

	/**
	 * Installs the incoming object
	 * @param incoming the incoming object
	 * @return true if an existing object was overwritten
	 */
	boolean installObject(OpenmrsObject incoming);

	/**
	 * Uninstalls the given object
	 * @param outgoing the outgoing object
	 * @param reason the reason for uninstallation
	 */
	void uninstallObject(OpenmrsObject outgoing, String reason);

	/**
	 * Fetches an existing object if it exists
	 * @param clazz the object's class
	 * @param uuid the object's UUID
	 */
	@Transactional(readOnly = true)
	<T extends OpenmrsObject> T fetchObject(Class<T> clazz, String uuid);
}