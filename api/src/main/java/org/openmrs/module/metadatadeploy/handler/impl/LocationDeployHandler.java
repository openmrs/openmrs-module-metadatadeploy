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

import org.openmrs.Location;
import org.openmrs.annotation.Handler;
import org.openmrs.api.LocationService;
import org.openmrs.module.metadatadeploy.ObjectUtils;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collections;

/**
 * Deployment handler for locations
 */
@Handler(supports = { Location.class })
public class LocationDeployHandler extends AbstractObjectDeployHandler<Location> {

	@Autowired
	@Qualifier("locationService")
	private LocationService locationService;

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#fetch(String)
	 */
	@Override
	public Location fetch(String uuid) {
		return locationService.getLocationByUuid(uuid);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#save(org.openmrs.OpenmrsObject)
	 */
	@Override
	public Location save(Location obj) {
		return locationService.saveLocation(obj);
	}
    
    @Override
    public void overwrite(Location incoming, Location existing) {
        Integer existingId = existing.getId();
        // since location attributes are defined separately from locations, we want to preserve them when overwrites
        ObjectUtils.overwrite(incoming, existing, Collections.singleton("attributes"));
        existing.setId(existingId);
    }

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#uninstall(org.openmrs.OpenmrsObject, String)
	 */
	@Override
	public void uninstall(Location obj, String reason) {
		locationService.retireLocation(obj, reason);
	}
}
