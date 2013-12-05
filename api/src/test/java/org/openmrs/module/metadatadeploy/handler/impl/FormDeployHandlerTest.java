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
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.form;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.relationshipType;

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
		deployService.installObject(encounterType("Test Encounter", "Testing", "enc-type1-uuid"));
		deployService.installObject(encounterType("Other Encounter", "Testing", "enc-type2-uuid"));

		// Check installing new
		deployService.installObject(form("New name", "New desc", "enc-type1-uuid", "1.0", "form-uuid"));

		Form created = Context.getFormService().getFormByUuid("form-uuid");
		Assert.assertThat(created.getName(), is("New name"));
		Assert.assertThat(created.getDescription(), is("New desc"));
		Assert.assertThat(created.getEncounterType(), is(Context.getEncounterService().getEncounterTypeByUuid("enc-type1-uuid")));
		Assert.assertThat(created.getVersion(), is("1.0"));

		// Check updating existing
		deployService.installObject(form("Updated name", "Updated desc", "enc-type2-uuid", "2.0", "form-uuid"));

		Form updated = Context.getFormService().getFormByUuid("form-uuid");
		Assert.assertThat(updated.getName(), is("Updated name"));
		Assert.assertThat(updated.getDescription(), is("Updated desc"));
		Assert.assertThat(updated.getEncounterType(), is(Context.getEncounterService().getEncounterTypeByUuid("enc-type2-uuid")));
		Assert.assertThat(updated.getVersion(), is("2.0"));

		// Check uninstall retires
		deployService.uninstallObject(deployService.fetchObject(Form.class, "form-uuid"), "Testing");

		Assert.assertThat(Context.getFormService().getFormByUuid("form-uuid").isRetired(), is(true));

		// Check re-install unretires
		deployService.installObject(form("Unretired name", "Unretired desc", "enc-type2-uuid", "2.0", "form-uuid"));

		Form unretired = Context.getFormService().getFormByUuid("form-uuid");
		Assert.assertThat(unretired.getName(), is("Unretired name"));
		Assert.assertThat(unretired.getDescription(), is("Unretired desc"));
		Assert.assertThat(unretired.isRetired(), is(false));
		Assert.assertThat(unretired.getDateRetired(), nullValue());
		Assert.assertThat(unretired.getRetiredBy(), nullValue());
		Assert.assertThat(unretired.getRetireReason(), nullValue());
	}
}