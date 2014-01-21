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
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.locationTag;

/**
 * Tests for {@link LocationTagDeployHandler}
 */
public class LocationTagDeployHandlerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private MetadataDeployService deployService;

	@Autowired
	private LocationService locationService;

	@Test
	public void integration() {
		// Check installing new
		deployService.installObject(locationTag("New name", "New desc", "obj-uuid"));

		LocationTag created = locationService.getLocationTagByUuid("obj-uuid");
		Assert.assertThat(created.getName(), is("New name"));
		Assert.assertThat(created.getDescription(), is("New desc"));

		// Check updating existing
		deployService.installObject(locationTag("Updated name", "Updated desc", "obj-uuid"));

		LocationTag updated = locationService.getLocationTagByUuid("obj-uuid");
		Assert.assertThat(updated.getId(), is(created.getId()));
		Assert.assertThat(updated.getName(), is("Updated name"));
		Assert.assertThat(updated.getDescription(), is("Updated desc"));

		// Check uninstall purges
		deployService.uninstallObject(deployService.fetchObject(LocationTag.class, "obj-uuid"), "Testing");

		Assert.assertThat(locationService.getLocationTagByUuid("obj-uuid"), nullValue());

		// Check re-install re-creates
		deployService.installObject(locationTag("Unpurged name", "Unpurged desc", "obj-uuid"));

		LocationTag unretired = locationService.getLocationTagByUuid("obj-uuid");
		Assert.assertThat(unretired.getName(), is("Unpurged name"));
		Assert.assertThat(unretired.getDescription(), is("Unpurged desc"));

		// Check everything can be persisted
		Context.flushSession();
	}
}