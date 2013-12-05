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

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.form;

/**
 * Tests for {@link FormDeployHandler}
 */
public class FormDeployHandlerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private MetadataDeployService deployService;

	/**
	 * Tests use of handler for installation
	 */
	@Test
	public void integration() {
		// Check creating new
		deployService.installObject(encounterType("Test Encounter", "Testing", "enc-type1-uuid"));
		deployService.installObject(form("Test Form #1", "Testing", "enc-type1-uuid", "1.0", "form-uuid"));

		Form created = Context.getFormService().getFormByUuid("form-uuid");
		Assert.assertThat(created.getName(), is("Test Form #1"));
		Assert.assertThat(created.getDescription(), is("Testing"));
		Assert.assertThat(created.getEncounterType(), is(Context.getEncounterService().getEncounterTypeByUuid("enc-type1-uuid")));
		Assert.assertThat(created.getVersion(), is("1.0"));

		// Check updating existing
		deployService.installObject(encounterType("Other Encounter", "Testing", "enc-type2-uuid"));
		deployService.installObject(form("New name", "New desc", "enc-type2-uuid", "2.0", "form-uuid"));

		Form updated = Context.getFormService().getFormByUuid("form-uuid");
		Assert.assertThat(updated.getName(), is("New name"));
		Assert.assertThat(updated.getDescription(), is("New desc"));
		Assert.assertThat(updated.getEncounterType(), is(Context.getEncounterService().getEncounterTypeByUuid("enc-type2-uuid")));
		Assert.assertThat(updated.getVersion(), is("2.0"));
	}
}