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
import org.openmrs.ConceptSource;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.conceptSource;

/**
 * Tests for {@link ConceptSourceDeployHandler}
 */
public class ConceptSourceDeployHandlerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private MetadataDeployService deployService;

	/**
	 * Tests use of handler for installation
	 */
	@Test
	public void integration() {
		// Check installing new
		deployService.installObject(conceptSource("New name", "New desc", "New code", "obj-uuid"));

		ConceptSource created = Context.getConceptService().getConceptSourceByUuid("obj-uuid");
		Assert.assertThat(created.getName(), is("New name"));
		Assert.assertThat(created.getDescription(), is("New desc"));
		Assert.assertThat(created.getHl7Code(), is("New code"));

		// Check updating existing
		deployService.installObject(conceptSource("Updated name", "Updated desc", "Updated code", "obj-uuid"));

		ConceptSource updated = Context.getConceptService().getConceptSourceByUuid("obj-uuid");
		Assert.assertThat(updated.getId(), is(created.getId()));
		Assert.assertThat(updated.getName(), is("Updated name"));
		Assert.assertThat(updated.getDescription(), is("Updated desc"));
		Assert.assertThat(updated.getHl7Code(), is("Updated code"));

		// Check uninstall retires
		deployService.uninstallObject(deployService.fetchObject(ConceptSource.class, "obj-uuid"), "Testing");

		Assert.assertThat(Context.getConceptService().getConceptSourceByUuid("obj-uuid").isRetired(), is(true));

		// Check re-install unretires
		deployService.installObject(conceptSource("Unretired name", "Unretired desc", "Unretired code", "obj-uuid"));

		ConceptSource unretired = Context.getConceptService().getConceptSourceByUuid("obj-uuid");
		Assert.assertThat(unretired.getName(), is("Unretired name"));
		Assert.assertThat(unretired.getDescription(), is("Unretired desc"));
		Assert.assertThat(unretired.getHl7Code(), is("Unretired code"));
		Assert.assertThat(unretired.isRetired(), is(false));
		Assert.assertThat(unretired.getDateRetired(), nullValue());
		Assert.assertThat(unretired.getRetiredBy(), nullValue());
		Assert.assertThat(unretired.getRetireReason(), nullValue());

		// Check everything can be persisted
		Context.flushSession();
	}
}