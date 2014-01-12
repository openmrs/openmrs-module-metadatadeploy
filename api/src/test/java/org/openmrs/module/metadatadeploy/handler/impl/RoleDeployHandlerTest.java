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
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;
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
		Privilege privilege1 = deployService.installObject(privilege("Privilege1", "Testing"));
		Privilege privilege2 = deployService.installObject(privilege("Privilege2", "Testing"));
		Privilege privilege3 = deployService.installObject(privilege("Privilege3", "Testing"));
		Privilege privilege4 = deployService.installObject(privilege("Privilege4", "Testing"));

		// Check installing new (Role2 inherits from Role1)
		Role role1 = deployService.installObject(role("Role1", "New desc", null, idSet("Privilege1")));
		Role role2 = deployService.installObject(role("Role2", "New desc", idSet("Role1"), idSet("Privilege2", "Privilege3")));
		Role role3 = deployService.installObject(role("Role3", "New desc", idSet("Role1", "Role2"), idSet("Privilege4")));

		// Check everything can be persisted
		Context.flushSession();

		Role created = Context.getUserService().getRole("Role3");
		Assert.assertThat(created.getDescription(), is("New desc"));
		Assert.assertThat(created.getInheritedRoles(), containsInAnyOrder(role1, role2));
		Assert.assertThat(created.getPrivileges(), containsInAnyOrder(privilege4));

		// Check updating existing
		deployService.installObject(role("Role3", "Updated desc", idSet("Role2"), null)); // No longer inherits Role1

		// Check everything can be persisted
		Context.flushSession();

		Role updated = Context.getUserService().getRole("Role3");
		Assert.assertThat(updated.getDescription(), is("Updated desc"));

		// Check uninstall removes
		deployService.uninstallObject(deployService.fetchObject(Role.class, "Role3"), "Testing");

		Assert.assertThat(Context.getUserService().getRole("Role3"), nullValue());

		// Check re-install unretires
		deployService.installObject(role("Role3", "Unretired desc", null, null));

		Role unretired = Context.getUserService().getRole("Role3");
		Assert.assertThat(unretired.getDescription(), is("Unretired desc"));
		Assert.assertThat(unretired.isRetired(), is(false));
		Assert.assertThat(unretired.getDateRetired(), nullValue());
		Assert.assertThat(unretired.getRetiredBy(), nullValue());
		Assert.assertThat(unretired.getRetireReason(), nullValue());

		// Check everything can be persisted
		Context.flushSession();
	}
}