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
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterType;

/**
 * Tests for {@link EncounterTypeDeployHandler}
 */
public class EncounterTypeDeployHandlerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private MetadataDeployService deployService;

	/**
	 * Tests use of handler for installation
	 */
	@Test
	public void integration() {
		deployService.installObject(encounterType("Test Encounter", "Testing", "obj1-uuid"));

		EncounterType created = Context.getEncounterService().getEncounterTypeByUuid("obj1-uuid");
		Assert.assertThat(created.getName(), is("Test Encounter"));
		Assert.assertThat(created.getDescription(), is("Testing"));

		// Check updating existing
		deployService.installObject(encounterType("New name", "New desc", "obj1-uuid"));

		EncounterType updated = Context.getEncounterService().getEncounterTypeByUuid("obj1-uuid");
		Assert.assertThat(updated.getName(), is("New name"));
		Assert.assertThat(updated.getDescription(), is("New desc"));

		// Retire object
		Context.getEncounterService().retireEncounterType(updated, "Testing");

		// Check that re-install unretires
		deployService.installObject(encounterType("Unretired name", "Unretired desc", "obj1-uuid"));

		EncounterType unretired = Context.getEncounterService().getEncounterTypeByUuid("obj1-uuid");
		Assert.assertThat(unretired.getName(), is("Unretired name"));
		Assert.assertThat(unretired.getDescription(), is("Unretired desc"));
		Assert.assertThat(unretired.isRetired(), is(false));
		Assert.assertThat(unretired.getDateRetired(), is(nullValue()));
		Assert.assertThat(unretired.getRetiredBy(), is(nullValue()));
		Assert.assertThat(unretired.getRetireReason(), is(nullValue()));
	}
}