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

import org.openmrs.OpenmrsMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of a synchronization describing the objects that were created, updated and retired
 */
public class SyncResult<T extends OpenmrsMetadata> {

	protected List<T> created = new ArrayList<T>();
	protected List<T> updated = new ArrayList<T>();
	protected List<T> retired = new ArrayList<T>();

	/**
	 * Gets the created objects
	 * @return the objects
	 */
	public List<T> getCreated() {
		return created;
	}

	/**
	 * Gets the updated objects
	 * @return the objects
	 */
	public List<T> getUpdated() {
		return updated;
	}

	/**
	 * Gets the retired objects
	 * @return the objects
	 */
	public List<T> getRetired() {
		return retired;
	}
}