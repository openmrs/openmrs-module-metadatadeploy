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

import org.openmrs.VisitType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.VisitService;
import org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Deployment handler for visit types
 */
@Handler(supports = { VisitType.class })
public class VisitTypeDeployHandler implements ObjectDeployHandler<VisitType> {

	@Autowired
	@Qualifier("visitService")
	private VisitService visitService;

	/**
	 * @see ObjectDeployHandler#getIdentifier(org.openmrs.OpenmrsObject)
	 */
	@Override
	public String getIdentifier(VisitType obj) {
		return obj.getUuid();
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#fetch(String)
	 */
	@Override
	public VisitType fetch(String uuid) {
		return visitService.getVisitTypeByUuid(uuid);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#save(org.openmrs.OpenmrsObject)
	 */
	@Override
	public VisitType save(VisitType obj) {
		return visitService.saveVisitType(obj);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#findAlternateMatch(org.openmrs.OpenmrsObject)
	 */
	@Override
	public VisitType findAlternateMatch(VisitType incoming) {
		return null;
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#remove(org.openmrs.OpenmrsObject, String)
	 * @param obj the object to remove
	 */
	@Override
	public void remove(VisitType obj, String reason) {
		visitService.retireVisitType(obj, reason);
	}
}