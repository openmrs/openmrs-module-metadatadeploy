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
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.privilege;

/**
 * Tests for {@link PrivilegeDeployHandler}
 */
public class PrivilegeDeployHandlerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private MetadataDeployService deployService;

	/**
	 * Tests use of handler for installation
	 */
	@Test
	public void integration() {
		// Check installing new
		deployService.installObject(privilege("Privilege", "New desc", "16c908e0-e39a-11e4-b571-0800200c9a66"));

		Context.flushSession(); // Make sure it gets persisted before we try evicting it later

		Privilege created = Context.getUserService().getPrivilege("Privilege");
		Assert.assertThat(created.getDescription(), is("New desc"));
        Assert.assertThat(created.getUuid(), is("16c908e0-e39a-11e4-b571-0800200c9a66"));

		// Check updating existing
		deployService.installObject(privilege("Privilege", "Updated desc", "16c908e0-e39a-11e4-b571-0800200c9a66"));

		Privilege updated = Context.getUserService().getPrivilege("Privilege");
		Assert.assertThat(updated.getDescription(), is("Updated desc"));
        // note that we aren't allowed to overwrite uuid

		// Check uninstall removes
		deployService.uninstallObject(deployService.fetchObject(Privilege.class, "Privilege"), "Testing");

		Assert.assertThat(Context.getUserService().getPrivilege("Privilege"), nullValue());

		// Check re-install unretires
		deployService.installObject(privilege("Privilege", "Unretired desc", "16c908e0-e39a-11e4-b571-0800200c9a66"));

		Privilege unretired = Context.getUserService().getPrivilege("Privilege");
		Assert.assertThat(unretired.getDescription(), is("Unretired desc"));
		Assert.assertThat(unretired.isRetired(), is(false));
		Assert.assertThat(unretired.getDateRetired(), nullValue());
		Assert.assertThat(unretired.getRetiredBy(), nullValue());
		Assert.assertThat(unretired.getRetireReason(), nullValue());

		// Check everything can be persisted
		Context.flushSession();
	}
}