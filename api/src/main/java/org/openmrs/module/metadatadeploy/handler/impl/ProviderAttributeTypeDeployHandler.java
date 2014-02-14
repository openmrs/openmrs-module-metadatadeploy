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

import org.openmrs.ProviderAttributeType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ProviderService;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Deployment handler for provider attribute types
 */
@Handler(supports = { ProviderAttributeType.class })
public class ProviderAttributeTypeDeployHandler extends AbstractObjectDeployHandler<ProviderAttributeType> {

	@Autowired
	@Qualifier("providerService")
	private ProviderService providerService;

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#fetch(String)
	 */
	@Override
	public ProviderAttributeType fetch(String identifier) {
		return providerService.getProviderAttributeTypeByUuid(identifier);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#save(org.openmrs.OpenmrsObject)
	 */
	@Override
	public ProviderAttributeType save(ProviderAttributeType obj) {
		return providerService.saveProviderAttributeType(obj);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#uninstall(org.openmrs.OpenmrsObject, String)
	 * @param obj the object to uninstall
	 */
	@Override
	public void uninstall(ProviderAttributeType obj, String reason) {
		providerService.retireProviderAttributeType(obj, reason);
	}
}