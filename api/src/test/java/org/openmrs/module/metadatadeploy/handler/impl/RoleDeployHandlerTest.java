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
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.idSet;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.privilege;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.role;

/**
 * Tests for {@link RoleDeployHandler}
 */
public class RoleDeployHandlerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private MetadataDeployService deployService;

	/**
	 * Tests use of handler for installation
	 */
	@Test
	public void integration() {
		deployService.installObject(privilege("Privilege #1", "Testing"));
		deployService.installObject(privilege("Privilege #2", "Testing"));

		// Check installing new
		deployService.installObject(role("Role", "New desc", null, idSet("Privilege #1", "Privilege #2")));

		Context.flushSession(); // Make sure it gets persisted before we try evicting it later

		Role created = Context.getUserService().getRole("Role");
		Assert.assertThat(created.getDescription(), is("New desc"));

		// Check updating existing
		deployService.installObject(role("Role", "Updated desc", null, null));

		Role updated = Context.getUserService().getRole("Role");
		Assert.assertThat(updated.getDescription(), is("Updated desc"));

		// Check uninstall removes
		deployService.uninstallObject(deployService.fetchObject(Role.class, "Role"), "Testing");

		Assert.assertThat(Context.getUserService().getRole("Role"), nullValue());

		// Check re-install unretires
		deployService.installObject(role("Role", "Unretired desc", null, null));

		Role unretired = Context.getUserService().getRole("Role");
		Assert.assertThat(unretired.getDescription(), is("Unretired desc"));
		Assert.assertThat(unretired.isRetired(), is(false));
		Assert.assertThat(unretired.getDateRetired(), nullValue());
		Assert.assertThat(unretired.getRetiredBy(), nullValue());
		Assert.assertThat(unretired.getRetireReason(), nullValue());
	}
}