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

import java.lang.reflect.Method;
import org.openmrs.PersonAttributeType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PersonService;
import org.openmrs.api.db.hibernate.DbSessionFactory;
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
	private DbSessionFactory sessionFactory;

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
		getCurrentSession().saveOrUpdate(obj);
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
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#uninstall(org.openmrs.OpenmrsObject, String)
	 * @param obj the object to uninstall
	 */
	@Override
	public void uninstall(PersonAttributeType obj, String reason) {
		personService.retirePersonAttributeType(obj, reason);
	}
	
	/**
	 * Gets the current hibernate session while taking care of the hibernate 3 and 4 differences.
	 * 
	 * @return the current hibernate session.
	 */
	private org.openmrs.api.db.hibernate.DbSession getCurrentSession() {
		try {
			return sessionFactory.getCurrentSession();
		}
		catch (NoSuchMethodError ex) {
			try {
				Method method = sessionFactory.getClass().getMethod("getCurrentSession", null);
				return (org.openmrs.api.db.hibernate.DbSession)method.invoke(sessionFactory, null);
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to get the current hibernate session", e);
			}
		}
	}
}