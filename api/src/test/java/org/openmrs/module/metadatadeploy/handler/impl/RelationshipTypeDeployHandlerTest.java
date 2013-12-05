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

	/**
	 * Tests use of handler for installation
	 */
	@Test
	public void intergration() {
		//check creating new
		deployService.installObject(relationshipType("Spouse", "Spouse", "a husband and a wife", "relationship-type-uuid"));

		RelationshipType created = Context.getPersonService().getRelationshipTypeByUuid("relationship-type-uuid");
		Assert.assertThat(created.getaIsToB(), is("Spouse"));
		Assert.assertThat(created.getbIsToA(), is("Spouse"));
		Assert.assertThat(created.getDescription(), is("a husband and a wife"));

		// Check updating existing
		deployService.installObject(relationshipType("Spouse new", "Spouse new", "a husband and a wife", "relationship-type-uuid"));
		RelationshipType updated = Context.getPersonService().getRelationshipTypeByUuid("relationship-type-uuid");
		Assert.assertThat(updated.getaIsToB(), is("Spouse new"));
		Assert.assertThat(updated.getbIsToA(), is("Spouse new"));
		Assert.assertThat(updated.getDescription(), is("a husband and a wife"));

		// Retire object
		Context.getPersonService().retireRelationshipType(updated, "Testing");

		// Check that re-install unretires
		deployService.installObject(relationshipType("Unretired spouse", "Unretired spouse", "a husband and a wife", "relationship-type-uuid"));

		RelationshipType unretired = Context.getPersonService().getRelationshipTypeByUuid("relationship-type-uuid");
		Assert.assertThat(unretired.getaIsToB(), is("Unretired spouse"));
		Assert.assertThat(unretired.getbIsToA(), is("Unretired spouse"));
		Assert.assertThat(unretired.getDescription(), is("a husband and a wife"));
		Assert.assertThat(unretired.isRetired(), is(false));
		Assert.assertThat(unretired.getDateRetired(), is(nullValue()));
		Assert.assertThat(unretired.getRetiredBy(), is(nullValue()));
		Assert.assertThat(unretired.getRetireReason(), is(nullValue()));
	}
}
