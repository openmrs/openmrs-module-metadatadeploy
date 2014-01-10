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

import org.openmrs.Form;
import org.openmrs.annotation.Handler;
import org.openmrs.api.FormService;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Deployment handler for forms
 */
@Handler(supports = { Form.class })
public class FormDeployHandler extends AbstractObjectDeployHandler<Form> {

	@Autowired
	@Qualifier("formService")
	private FormService formService;

	/**
	 * @see ObjectDeployHandler#getIdentifier(org.openmrs.OpenmrsObject)
	 */
	@Override
	public String getIdentifier(Form obj) {
		return obj.getUuid();
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#fetch(String)
	 */
	@Override
	public Form fetch(String uuid) {
		return formService.getFormByUuid(uuid);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#save(org.openmrs.OpenmrsObject)
	 */
	@Override
	public Form save(Form obj) {
		return formService.saveForm(obj);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#findAlternateMatch(org.openmrs.OpenmrsObject)
	 */
	@Override
	public Form findAlternateMatch(Form incoming) {
		return formService.getForm(incoming.getName(), incoming.getVersion());
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#remove(org.openmrs.OpenmrsObject, String)
	 * @param obj the object to remove
	 */
	@Override
	public void remove(Form obj, String reason) {
		formService.retireForm(obj, reason);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#getMergeExcludedFields(org.openmrs.OpenmrsObject, org.openmrs.OpenmrsObject)
	 */
	@Override
	public String[] getMergeExcludedFields(Form incoming, Form existing) {
		return new String[] { "formId" };
	}
}