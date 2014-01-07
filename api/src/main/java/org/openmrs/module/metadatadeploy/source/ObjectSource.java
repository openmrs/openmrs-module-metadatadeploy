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

package org.openmrs.module.metadatadeploy.source;

import org.openmrs.OpenmrsObject;

/**
 * Interface for a source of OpenMRS objects
 */
public interface ObjectSource<T extends OpenmrsObject> {

	/**
	 * Fetches the next object in the source or returns null if there are no more objects
	 * @return the next object or null
	 * @throws Exception if an error occurs
	 */
	T fetchNext() throws Exception;
}