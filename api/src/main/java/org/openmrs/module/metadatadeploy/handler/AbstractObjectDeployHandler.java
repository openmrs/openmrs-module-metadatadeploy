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
import org.openmrs.module.metadatadeploy.ObjectUtils;

/**
 * Abstract base class for object deploy handlers
 */
public abstract class AbstractObjectDeployHandler<T extends OpenmrsObject> implements ObjectDeployHandler<T> {

	/**
	 * @see ObjectDeployHandler#getIdentifier(org.openmrs.OpenmrsObject)
	 */
	@Override
	public String getIdentifier(T obj) {
		return obj.getUuid();
	}

	/**
	 * @see ObjectDeployHandler#findAlternateMatch(org.openmrs.OpenmrsObject)
	 */
	@Override
	public T findAlternateMatch(T obj) {
		return null;
	}

	/**
	 * @see ObjectDeployHandler#overwrite(org.openmrs.OpenmrsObject, org.openmrs.OpenmrsObject)
	 */
	@Override
	public void overwrite(T incoming, T existing) {
		// If object uses id, keep this to be re-instated after copy
		boolean usesId = ObjectUtils.usesId(incoming);
		Integer existingId = usesId ? existing.getId() : null;

		// Do per-field copy of incoming to existing
		ObjectUtils.overwrite(incoming, existing, null);

		if (usesId) {
			existing.setId(existingId);
		}
	}
}