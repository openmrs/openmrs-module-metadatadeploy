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

import org.openmrs.ConceptSource;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Deployment handler for concept sources
 */
@Handler(supports = { ConceptSource.class })
public class ConceptSourceDeployHandler extends AbstractObjectDeployHandler<ConceptSource> {

	@Autowired
	@Qualifier("conceptService")
	private ConceptService conceptService;

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#fetch(String)
	 */
	@Override
	public ConceptSource fetch(String identifier) {
		return conceptService.getConceptSourceByUuid(identifier);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#findAlternateMatch(org.openmrs.OpenmrsObject)
	 */
	@Override
	public ConceptSource findAlternateMatch(ConceptSource obj) {
		return conceptService.getConceptSourceByName(obj.getName());
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#save(org.openmrs.OpenmrsObject)
	 */
	@Override
	public ConceptSource save(ConceptSource obj) {
		return conceptService.saveConceptSource(obj);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#uninstall(org.openmrs.OpenmrsObject, String)
	 */
	@Override
	public void uninstall(ConceptSource obj, String reason) {
		conceptService.retireConceptSource(obj, reason);
	}
}