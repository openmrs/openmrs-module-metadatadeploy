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

package org.openmrs.module.metadatadeploy.sync;

import org.openmrs.OpenmrsObject;

import java.util.List;

/**
 * Interface for all object synchronization operations
 */
public interface ObjectSynchronization<T extends OpenmrsObject> {

	/**
	 * Fetches all existing objects
	 * @return the existing objects
	 */
	List<T> fetchAllExisting();

	/**
	 * Fetches an existing object by its id
	 * @param id the object id
	 * @return the existing object
	 */
	T fetchExistingById(int id);

	/**
	 * Gets the synchronization key of the given object
	 * @param obj the object
	 * @return the synchronization key
	 */
	Object getObjectSyncKey(T obj);

	/**
	 * Compares two objects and returns true if an update is required because there are differences
	 * @param incoming the incoming object
	 * @param existing the existing object
	 * @return true is there are differences
	 */
	boolean updateRequired(T incoming, T existing);
}