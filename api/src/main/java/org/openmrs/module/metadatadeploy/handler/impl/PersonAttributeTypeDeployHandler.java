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

import org.hibernate.SessionFactory;
import org.openmrs.PersonAttributeType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PersonService;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Deployment handler for person attribute types
 */
@Handler(supports = { PersonAttributeType.class })
public class PersonAttributeTypeDeployHandler extends AbstractObjectDeployHandler<PersonAttributeType> {

	@Autowired
	@Qualifier("personService")
	private PersonService personService;

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#getIdentifier(org.openmrs.OpenmrsObject)
	 */
	@Override
	public String getIdentifier(PersonAttributeType obj) {
		return obj.getUuid();
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#fetch(String)
	 */
	@Override
	public PersonAttributeType fetch(String uuid) {
		return personService.getPersonAttributeTypeByUuid(uuid);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#save(org.openmrs.OpenmrsObject)
	 */
	@Override
	public PersonAttributeType save(PersonAttributeType obj) {
		// The regular save method in the person service does some interesting stuff to check name changes.. which breaks
		// our way of replacing existing objects. Our workaround is to ask Hibernate directly to save the object
		sessionFactory.getCurrentSession().saveOrUpdate(obj);
		return obj;

		//return personService.savePersonAttributeType(obj);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#findAlternateMatch(org.openmrs.OpenmrsObject)
	 */
	@Override
	public PersonAttributeType findAlternateMatch(PersonAttributeType incoming) {
		return personService.getPersonAttributeTypeByName(incoming.getName());
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#remove(org.openmrs.OpenmrsObject, String)
	 * @param obj the object to remove
	 */
	@Override
	public void remove(PersonAttributeType obj, String reason) {
		personService.retirePersonAttributeType(obj, reason);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#getMergeExcludedFields(org.openmrs.OpenmrsObject, org.openmrs.OpenmrsObject)
	 */
	@Override
	public String[] getMergeExcludedFields(PersonAttributeType incoming, PersonAttributeType existing) {
		return new String[] { "personAttributeTypeId" };
	}
}