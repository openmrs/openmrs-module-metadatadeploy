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
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.program;

/**
 * Tests for {@link ProgramDeployHandler}
 */
public class ProgramDeployHandlerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private MetadataDeployService deployService;

	/**
	 * Tests use of handler for installation
	 */
	@Test
	public void integration() {
		// Existing concepts in test data
		final String HIV_PROGRAM_UUID = "0a9afe04-088b-44ca-9291-0a8c3b5c96fa";
		final String MALARIA_PROGRAM_UUID = "f923524a-b90c-4870-a948-4125638606fd";

		// Check creating new
		deployService.installObject(program("Test Program", "Testing", HIV_PROGRAM_UUID, "obj1-uuid"));

		Program created = Context.getProgramWorkflowService().getProgramByUuid("obj1-uuid");
		Assert.assertThat(created.getName(), is("Test Program"));
		Assert.assertThat(created.getDescription(), is("Testing"));
		Assert.assertThat(created.getConcept(), is(Context.getConceptService().getConceptByUuid(HIV_PROGRAM_UUID)));

		// Check updating existing
		deployService.installObject(program("New name", "New desc", MALARIA_PROGRAM_UUID, "obj1-uuid"));

		Program updated = Context.getProgramWorkflowService().getProgramByUuid("obj1-uuid");
		Assert.assertThat(updated.getName(), is("New name"));
		Assert.assertThat(updated.getDescription(), is("New desc"));
		Assert.assertThat(updated.getConcept(), is(Context.getConceptService().getConceptByUuid(MALARIA_PROGRAM_UUID)));

		// Check update existing when name conflicts
		deployService.installObject(program("New name", "Diff desc", MALARIA_PROGRAM_UUID, "obj2-uuid"));
		updated = Context.getProgramWorkflowService().getProgramByUuid("obj2-uuid");
		Assert.assertThat(updated.getName(), is("New name"));
		Assert.assertThat(updated.getDescription(), is("Diff desc"));

		Program old = Context.getProgramWorkflowService().getProgramByUuid("obj1-uuid");
		Assert.assertThat(old, is(nullValue()));
	}
}