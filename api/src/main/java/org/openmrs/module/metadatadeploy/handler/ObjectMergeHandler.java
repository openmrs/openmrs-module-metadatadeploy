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
 * Some metadata objects are merged rather than overwritten. For example an existing global property should retain
 * it's value if the incoming object doesn't have a value
 */
public interface ObjectMergeHandler<T extends OpenmrsObject> {

	/**
	 * Merges properties of an existing object into the incoming object
	 * @param existing the existing object
	 * @param incoming the incoming object
	 */
	void merge(T existing, T incoming);
}