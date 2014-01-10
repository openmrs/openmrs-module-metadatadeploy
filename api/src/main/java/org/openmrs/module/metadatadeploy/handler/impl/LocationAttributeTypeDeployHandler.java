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

package org.openmrs.module.metadatadeploy.handler.impl;

import org.openmrs.LocationAttributeType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.LocationService;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Deployment handler for location attribute types
 */
@Handler(supports = { LocationAttributeType.class })
public class LocationAttributeTypeDeployHandler extends AbstractObjectDeployHandler<LocationAttributeType> {

	@Autowired
	@Qualifier("locationService")
	private LocationService locationService;

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#getIdentifier(org.openmrs.OpenmrsObject)
	 */
	@Override
	public String getIdentifier(LocationAttributeType obj) {
		return obj.getUuid();
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#fetch(String)
	 */
	@Override
	public LocationAttributeType fetch(String identifier) {
		return locationService.getLocationAttributeTypeByUuid(identifier);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#save(org.openmrs.OpenmrsObject)
	 */
	@Override
	public LocationAttributeType save(LocationAttributeType obj) {
		return locationService.saveLocationAttributeType(obj);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#findAlternateMatch(org.openmrs.OpenmrsObject)
	 */
	@Override
	public LocationAttributeType findAlternateMatch(LocationAttributeType incoming) {
		return null;
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#remove(org.openmrs.OpenmrsObject, String)
	 * @param obj the object to remove
	 */
	@Override
	public void remove(LocationAttributeType obj, String reason) {
		locationService.retireLocationAttributeType(obj, reason);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#getMergeExcludedFields(org.openmrs.OpenmrsObject, org.openmrs.OpenmrsObject)
	 */
	@Override
	public String[] getMergeExcludedFields(LocationAttributeType incoming, LocationAttributeType existing) {
		return new String[] { "locationAttributeTypeId" };
	}
}