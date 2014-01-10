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

import org.apache.commons.lang3.StringUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.annotation.Handler;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Deployment handler for global properties
 */
@Handler(supports = { GlobalProperty.class })
public class GlobalPropertyDeployHandler extends AbstractObjectDeployHandler<GlobalProperty> {

	@Autowired
	@Qualifier("adminService")
	private AdministrationService adminService;

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#getIdentifier(org.openmrs.OpenmrsObject)
	 */
	@Override
	public String getIdentifier(GlobalProperty obj) {
		return obj.getProperty();
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#fetch(String)
	 */
	@Override
	public GlobalProperty fetch(String identifier) {
		return adminService.getGlobalPropertyObject(identifier);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#save(org.openmrs.OpenmrsObject)
	 */
	@Override
	public GlobalProperty save(GlobalProperty obj) {
		return adminService.saveGlobalProperty(obj);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#findAlternateMatch(org.openmrs.OpenmrsObject)
	 */
	@Override
	public GlobalProperty findAlternateMatch(GlobalProperty incoming) {
		return adminService.getGlobalPropertyByUuid(incoming.getUuid());
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#remove(org.openmrs.OpenmrsObject, String)
	 * @param obj the object to remove
	 */
	@Override
	public void remove(GlobalProperty obj, String reason) {
		adminService.purgeGlobalProperty(obj);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#getMergeExcludedFields(org.openmrs.OpenmrsObject, org.openmrs.OpenmrsObject)
	 */
	@Override
	public String[] getMergeExcludedFields(GlobalProperty incoming, GlobalProperty existing) {
		boolean datatypeMatches = OpenmrsUtil.nullSafeEquals(existing.getDatatypeClassname(), incoming.getDatatypeClassname());

		// Global properties don't really distinguish between blank and null values since the UI doesn't let a user
		// distinguish between the two
		Object incomingValue = incoming.getValue();
		boolean incomingHasNoValue = incomingValue == null || (incomingValue instanceof String && StringUtils.isEmpty((String) incomingValue));
		boolean preserveValue = existing.getValue() != null && incomingHasNoValue && datatypeMatches;

		return preserveValue ? new String[] { "value" } : new String[] {};
	}
}