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
 * Interface for all synchronization operations
 */
public interface ObjectSynchronization<T extends OpenmrsObject> {

	/**
	 * Runs the complete synchronization operation
	 */
	void run();

	/**
	 * Gets the created objects
	 * @return the objects
	 */
	List<T> getCreatedObjects();

	/**
	 * Gets the updated objects
	 * @return the objects
	 */
	List<T> getUpdatedObjects();

	/**
	 * Gets the removed objects
	 * @return the objects
	 */
	List<T> getRemovedObjects();
}
