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
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.program;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.programWorkflow;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.programWorkflowState;

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
		final String HIV_PROGRAM_CONCEPT_UUID = "0a9afe04-088b-44ca-9291-0a8c3b5c96fa";
		final String MALARIA_PROGRAM_CONCEPT_UUID = "f923524a-b90c-4870-a948-4125638606fd";
		final String CIVIL_STATUS_UUID = "89ca642a-dab6-4f20-b712-e12ca4fc6d36";  // not a likely real program outcome, but an example for testing!

		// Check installing new
		deployService.installObject(program("Test Program", "Testing", HIV_PROGRAM_CONCEPT_UUID, null, "obj1-uuid"));

		Program created = Context.getProgramWorkflowService().getProgramByUuid("obj1-uuid");
		assertThat(created.getName(), is("Test Program"));
		assertThat(created.getDescription(), is("Testing"));
		assertThat(created.getConcept(), is(Context.getConceptService().getConceptByUuid(HIV_PROGRAM_CONCEPT_UUID)));
		Assert.assertNull(created.getOutcomesConcept());

		// Check updating existing
		deployService.installObject(program("New name", "New desc", MALARIA_PROGRAM_CONCEPT_UUID, CIVIL_STATUS_UUID,"obj1-uuid"));

		Program updated = Context.getProgramWorkflowService().getProgramByUuid("obj1-uuid");
		assertThat(updated.getId(), is(created.getId()));
		assertThat(updated.getName(), is("New name"));
		assertThat(updated.getDescription(), is("New desc"));
		assertThat(updated.getConcept(), is(Context.getConceptService().getConceptByUuid(MALARIA_PROGRAM_CONCEPT_UUID)));
		assertThat(updated.getOutcomesConcept(), is(Context.getConceptService().getConceptByUuid(CIVIL_STATUS_UUID)));

		// Check update existing when name conflicts
		deployService.installObject(program("New name", "Diff desc", MALARIA_PROGRAM_CONCEPT_UUID, null,"obj2-uuid"));
		updated = Context.getProgramWorkflowService().getProgramByUuid("obj2-uuid");
		assertThat(updated.getName(), is("New name"));
		assertThat(updated.getDescription(), is("Diff desc"));

		Program old = Context.getProgramWorkflowService().getProgramByUuid("obj1-uuid");
		assertThat(old, is(nullValue()));

/*		// Add some workflows and states to check our custom retire works
		ProgramWorkflowState state = new ProgramWorkflowState();
		state.setName("State");
		state.setConcept(MetadataUtils.existing(Concept.class, "e10ffe54-5184-4efe-8960-cd565ec1cdf8"));
		state.setInitial(true);
		state.setTerminal(false)*/;

		// now install with a workflow and a state
		deployService.installObject(program("New name", "Diff desc", MALARIA_PROGRAM_CONCEPT_UUID, null,"obj2-uuid",
				Collections.singleton(programWorkflow(CIVIL_STATUS_UUID, "obj-workflow-uuid" ,
				Collections.singleton(programWorkflowState(CIVIL_STATUS_UUID, true, false, "obj-state-uuid"))))));
		updated = Context.getProgramWorkflowService().getProgramByUuid("obj2-uuid");
		assertThat(updated.getWorkflows().size(), is(1));

		ProgramWorkflow workflow = updated.getAllWorkflows().iterator().next();
		assertThat(workflow.getConcept().getUuid(), is(CIVIL_STATUS_UUID));
		assertThat(workflow.getUuid(), is("obj-workflow-uuid"));
		assertThat(workflow.getStates().size(), is(1));

		ProgramWorkflowState state = workflow.getStates().iterator().next();
		assertThat(state.getConcept().getUuid(), is(CIVIL_STATUS_UUID));
		assertThat(state.getUuid(), is("obj-state-uuid"));

		// Check uninstall retires
		deployService.uninstallObject(deployService.fetchObject(Program.class, "obj2-uuid"), "Testing");

		assertThat(Context.getProgramWorkflowService().getProgramByUuid("obj2-uuid").isRetired(), is(true));
		assertThat(Context.getProgramWorkflowService().getWorkflowByUuid("obj-workflow-uuid").isRetired(), is(true));

		// Check re-install unretires
		deployService.installObject(program("Unretired name", "Unretired desc", MALARIA_PROGRAM_CONCEPT_UUID, null,"obj2-uuid"));

		// TODO: should uninstall workflow

		Program unretired = Context.getProgramWorkflowService().getProgramByUuid("obj2-uuid");
		assertThat(unretired.getName(), is("Unretired name"));
		assertThat(unretired.getDescription(), is("Unretired desc"));
		assertThat(unretired.isRetired(), is(false));
		assertThat(unretired.getDateRetired(), nullValue());
		assertThat(unretired.getRetiredBy(), nullValue());
		assertThat(unretired.getRetireReason(), nullValue());

		// Check everything can be persisted
		Context.flushSession();
	}
}