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

package org.openmrs.module.metadatadeploy.handler;

import org.openmrs.OpenmrsObject;

/**
 * Interface for object deployment handler components
 */
public interface ObjectDeployHandler<T extends OpenmrsObject> {

	/**
	 * Gets an object's primary identifier (usually the UUID)
	 * @param obj the object
	 * @return the identifier
	 */
	String getIdentifier(T obj);

	/**
	 * Fetches an object by primary identifier
	 * @param identifier the identifier
	 * @return the object or null
	 */
	T fetch(String identifier);

	/**
	 * Finds an alternative existing object (i.e. not on the primary identifier) which should be merged with the incoming object
	 * @param obj the incoming object
	 * @return the existing object or null
	 */
	T findAlternateMatch(T obj);

	/**
	 * Saves the given object to the database
	 * @param obj the object to save
	 * @return the saved object
	 */
	T save(T obj);

	/**
	 * Removes the given object which may be implemented as a void, retire or purge depending on the object
	 * @param obj the object to uninstall
	 * @param reason the reason for removal
	 */
	void uninstall(T obj, String reason);

	/**
	 * Overwrites the existing object with the incoming object
	 * @param incoming the incoming object
	 * @param existing the existing object
	 */
	void overwrite(T incoming, T existing);
}