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
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.hamcrest.Matchers.is;

import static org.hamcrest.Matchers.nullValue;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.relationshipType;

/**
 * Tests for {@link RelationshipTypeDeployHandler}
 */
public class RelationshipTypeDeployHandlerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private MetadataDeployService deployService;

	@Test
	public void integration() {
		// Check installing new
		deployService.installObject(relationshipType("New AtoB", "New BtoA", "New desc", "obj-uuid"));

		RelationshipType created = Context.getPersonService().getRelationshipTypeByUuid("obj-uuid");
		Assert.assertThat(created.getaIsToB(), is("New AtoB"));
		Assert.assertThat(created.getbIsToA(), is("New BtoA"));
		Assert.assertThat(created.getDescription(), is("New desc"));

		// Check updating existing
		deployService.installObject(relationshipType("Updated AtoB", "Updated BtoA", "Updated desc", "obj-uuid"));

		RelationshipType updated = Context.getPersonService().getRelationshipTypeByUuid("obj-uuid");
		Assert.assertThat(updated.getId(), is(created.getId()));
		Assert.assertThat(updated.getaIsToB(), is("Updated AtoB"));
		Assert.assertThat(updated.getbIsToA(), is("Updated BtoA"));
		Assert.assertThat(updated.getDescription(), is("Updated desc"));

		// Check uninstall retires
		deployService.uninstallObject(deployService.fetchObject(RelationshipType.class, "obj-uuid"), "Testing");

		Assert.assertThat(Context.getPersonService().getRelationshipTypeByUuid("obj-uuid").isRetired(), is(true));

		// Check re-install unretires
		deployService.installObject(relationshipType("Unretired AtoB", "Unretired BtoA", "Unretired desc", "obj-uuid"));

		RelationshipType unretired = Context.getPersonService().getRelationshipTypeByUuid("obj-uuid");
		Assert.assertThat(unretired.getaIsToB(), is("Unretired AtoB"));
		Assert.assertThat(unretired.getbIsToA(), is("Unretired BtoA"));
		Assert.assertThat(unretired.getDescription(), is("Unretired desc"));
		Assert.assertThat(unretired.isRetired(), is(false));
		Assert.assertThat(unretired.getDateRetired(), nullValue());
		Assert.assertThat(unretired.getRetiredBy(), nullValue());
		Assert.assertThat(unretired.getRetireReason(), nullValue());

		// Check everything can be persisted
		Context.flushSession();
	}
}