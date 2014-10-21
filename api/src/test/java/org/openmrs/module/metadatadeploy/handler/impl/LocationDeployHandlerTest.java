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
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.location;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.locationAttribute;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.locationAttributeType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.locationTag;

/**
 * Tests for {@link LocationDeployHandler}
 */
public class LocationDeployHandlerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private MetadataDeployService deployService;

	@Test
	public void integration() {

		// Check installing new
		deployService.installObject(location("New name", "New desc", "obj-uuid"));

		Location created = Context.getLocationService().getLocationByUuid("obj-uuid");
		Assert.assertThat(created.getName(), is("New name"));
		Assert.assertThat(created.getDescription(), is("New desc"));

		// Check updating existing
		deployService.installObject(location("Updated name", "Updated desc", "obj-uuid"));

		Location updated = Context.getLocationService().getLocationByUuid("obj-uuid");
		Assert.assertThat(updated.getId(), is(created.getId()));
		Assert.assertThat(updated.getName(), is("Updated name"));
		Assert.assertThat(updated.getDescription(), is("Updated desc"));

		// Check uninstall retires
		deployService.uninstallObject(deployService.fetchObject(Location.class, "obj-uuid"), "Testing");

		Assert.assertThat(Context.getLocationService().getLocationByUuid("obj-uuid").isRetired(), is(true));

		// Check re-install unretires
		deployService.installObject(location("Unretired name", "Unretired desc", "obj-uuid"));

		Location unretired = Context.getLocationService().getLocationByUuid("obj-uuid");
		Assert.assertThat(unretired.getName(), is("Unretired name"));
		Assert.assertThat(unretired.getDescription(), is("Unretired desc"));
		Assert.assertThat(unretired.isRetired(), is(false));
		Assert.assertThat(unretired.getDateRetired(), nullValue());
		Assert.assertThat(unretired.getRetiredBy(), nullValue());
		Assert.assertThat(unretired.getRetireReason(), nullValue());

		// Check everything can be persisted
		Context.flushSession();
	}

    @Test
    public void integrationWithParentAndTags() {

        // create some sample tags
        deployService.installObject(locationTag("Tag1", "Tag1", "tag1-uuid"));
        deployService.installObject(locationTag("Tag2", "Tag2", "tag2-uuid"));

        // create some sample parents
        Location parent1 = deployService.installObject(location("Parent1", "Parent1", "parent1-uuid"));
        Location parent2 = deployService.installObject(location("Parent2", "Parent2", "parent2-uuid"));

        // Check installing new
        deployService.installObject(location("New name", "New desc", "obj-uuid", "parent1-uuid", Arrays.asList("tag1-uuid")));

        Location created = Context.getLocationService().getLocationByUuid("obj-uuid");
        Assert.assertThat(created.getName(), is("New name"));
        Assert.assertThat(created.getDescription(), is("New desc"));
        Assert.assertThat(created.getParentLocation(), is(parent1));
        Assert.assertTrue(created.hasTag("Tag1"));
        Assert.assertFalse(created.hasTag("Tag2"));

        // Check updating existing
        deployService.installObject(location("Updated name", "Updated desc", "obj-uuid", "parent2-uuid", Arrays.asList("tag2-uuid")));

        Location updated = Context.getLocationService().getLocationByUuid("obj-uuid");
        Assert.assertThat(updated.getId(), is(created.getId()));
        Assert.assertThat(updated.getName(), is("Updated name"));
        Assert.assertThat(updated.getDescription(), is("Updated desc"));
        Assert.assertThat(created.getParentLocation(), is(parent2));
        Assert.assertFalse(created.hasTag("Tag1"));
        Assert.assertTrue(created.hasTag("Tag2"));

        // Check everything can be persisted
        Context.flushSession();

    }

    @Test
    public void integration_redeployShouldNotOverrideExistingAttributes() {

        // Set up the existing objects that we will need
        deployService.installObject(locationAttributeType("New name", "New desc", FreeTextDatatype.class, null, 0, 1, "attribute-type-uuid"));
        deployService.installObject(location("New name", "New desc", "location-uuid"));
        deployService.installObject(locationAttribute("location-uuid", "attribute-type-uuid", "test me", "attribute-uuid"));

        Context.flushSession();

        // sanity check
        Location location = Context.getLocationService().getLocationByUuid("location-uuid");
        Assert.assertThat(location.getAttributes().size(), is(1));

        // now re-create the location and check to make sure the lcoation isn't blown away
        deployService.installObject(location("New name", "New desc", "location-uuid"));
        location = Context.getLocationService().getLocationByUuid("location-uuid");
        Assert.assertThat(location.getAttributes().size(), is(1));

    }
}