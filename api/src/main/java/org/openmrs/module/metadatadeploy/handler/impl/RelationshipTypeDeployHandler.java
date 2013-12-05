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

import org.openmrs.RelationshipType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PersonService;
import org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Deployment handler for person relationship type
 */
@Handler(supports = { RelationshipType.class })
public class RelationshipTypeDeployHandler implements ObjectDeployHandler<RelationshipType> {

	@Autowired
	@Qualifier("personService")
	private PersonService personService;


	/**
	 * @see ObjectDeployHandler#getIdentifier(org.openmrs.OpenmrsObject)
	 */
	@Override
	public String getIdentifier(RelationshipType obj) {
		return obj.getUuid();
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#fetch(String)
	 */
	@Override
	public RelationshipType fetch(String uuid) {
		return personService.getRelationshipTypeByUuid(uuid);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#findAlternateMatch(org.openmrs.OpenmrsObject)
	 */
	@Override
	public RelationshipType findAlternateMatch(RelationshipType incoming) {
		return null;
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#save(org.openmrs.OpenmrsObject)
	 */
	@Override
	public RelationshipType save(RelationshipType obj) {
		return personService.saveRelationshipType(obj);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#remove(org.openmrs.OpenmrsObject, String)
	 * @param obj the object to remove
	 */
	@Override
	public void remove(RelationshipType obj, String reason) {
		personService.retireRelationshipType(obj, reason);
	}
}