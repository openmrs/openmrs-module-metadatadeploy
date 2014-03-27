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
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.MetadataUtils;
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

		// Check installing new
		deployService.installObject(program("Test Program", "Testing", HIV_PROGRAM_UUID, "obj1-uuid"));

		Program created = Context.getProgramWorkflowService().getProgramByUuid("obj1-uuid");
		Assert.assertThat(created.getName(), is("Test Program"));
		Assert.assertThat(created.getDescription(), is("Testing"));
		Assert.assertThat(created.getConcept(), is(Context.getConceptService().getConceptByUuid(HIV_PROGRAM_UUID)));

		// Check updating existing
		deployService.installObject(program("New name", "New desc", MALARIA_PROGRAM_UUID, "obj1-uuid"));

		Program updated = Context.getProgramWorkflowService().getProgramByUuid("obj1-uuid");
		Assert.assertThat(updated.getId(), is(created.getId()));
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

		// Add some workflows and states to check our custom retire works
		ProgramWorkflowState state = new ProgramWorkflowState();
		state.setName("State");
		state.setConcept(MetadataUtils.existing(Concept.class, "e10ffe54-5184-4efe-8960-cd565ec1cdf8"));
		state.setInitial(true);
		state.setTerminal(false);
		ProgramWorkflow workflow = new ProgramWorkflow();
		workflow.setName("Workflow");
		workflow.setConcept(MetadataUtils.existing(Concept.class, "e10ffe54-5184-4efe-8960-cd565ec1cdf8"));
		workflow.addState(state);
		updated.addWorkflow(workflow);

		// Check uninstall retires
		deployService.uninstallObject(deployService.fetchObject(Program.class, "obj2-uuid"), "Testing");

		Assert.assertThat(Context.getProgramWorkflowService().getProgramByUuid("obj2-uuid").isRetired(), is(true));

		// Check re-install unretires
		deployService.installObject(program("Unretired name", "Unretired desc", MALARIA_PROGRAM_UUID, "obj2-uuid"));

		Program unretired = Context.getProgramWorkflowService().getProgramByUuid("obj2-uuid");
		Assert.assertThat(unretired.getName(), is("Unretired name"));
		Assert.assertThat(unretired.getDescription(), is("Unretired desc"));
		Assert.assertThat(unretired.isRetired(), is(false));
		Assert.assertThat(unretired.getDateRetired(), nullValue());
		Assert.assertThat(unretired.getRetiredBy(), nullValue());
		Assert.assertThat(unretired.getRetireReason(), nullValue());

		// Check everything can be persisted
		Context.flushSession();
	}
}