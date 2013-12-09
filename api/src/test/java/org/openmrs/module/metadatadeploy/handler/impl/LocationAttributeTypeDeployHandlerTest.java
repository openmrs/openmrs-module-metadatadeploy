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
import org.openmrs.LocationAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.locationAttributeType;

/**
 * Tests for {@link LocationAttributeTypeDeployHandler}
 */
public class LocationAttributeTypeDeployHandlerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private MetadataDeployService deployService;

	@Test
	public void integration() {
		// Check installing new
		deployService.installObject(locationAttributeType("New name", "New desc", FreeTextDatatype.class, null, 0, 1, "obj-uuid"));

		LocationAttributeType created = Context.getLocationService().getLocationAttributeTypeByUuid("obj-uuid");
		Assert.assertThat(created.getName(), is("New name"));
		Assert.assertThat(created.getDescription(), is("New desc"));

		// Check updating existing
		deployService.installObject(locationAttributeType("Updated name", "Updated desc", FreeTextDatatype.class, null, 0, 1, "obj-uuid"));

		LocationAttributeType updated = Context.getLocationService().getLocationAttributeTypeByUuid("obj-uuid");
		Assert.assertThat(updated.getName(), is("Updated name"));
		Assert.assertThat(updated.getDescription(), is("Updated desc"));

		// Check uninstall retires
		deployService.uninstallObject(deployService.fetchObject(LocationAttributeType.class, "obj-uuid"), "Testing");

		Assert.assertThat(Context.getLocationService().getLocationAttributeTypeByUuid("obj-uuid").isRetired(), is(true));

		// Check re-install unretires
		deployService.installObject(locationAttributeType("Unretired name", "Unretired desc", FreeTextDatatype.class, null, 0, 1, "obj-uuid"));

		LocationAttributeType unretired = Context.getLocationService().getLocationAttributeTypeByUuid("obj-uuid");
		Assert.assertThat(unretired.getName(), is("Unretired name"));
		Assert.assertThat(unretired.getDescription(), is("Unretired desc"));
		Assert.assertThat(unretired.isRetired(), is(false));
		Assert.assertThat(unretired.getDateRetired(), nullValue());
		Assert.assertThat(unretired.getRetiredBy(), nullValue());
		Assert.assertThat(unretired.getRetireReason(), nullValue());
	}
}