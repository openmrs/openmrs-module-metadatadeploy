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

import org.hibernate.FlushMode;

import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.idSet;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.privilege;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.role;

import java.lang.reflect.Method;

/**
 * Tests for {@link RoleDeployHandler}
 */
public class RoleDeployHandlerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private MetadataDeployService deployService;

	@Autowired
	private DbSessionFactory sessionFactory;

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
		Role role1 = deployService.installObject(role("Role1", "New desc", null, idSet("Privilege1"), "0689d5d0-e39b-11e4-b571-0800200c9a66"));
		Role role2 = deployService.installObject(role("Role2", "New desc", idSet("Role1"), idSet("Privilege2", "Privilege3"), "20b709a0-e39b-11e4-b571-0800200c9a66"));
		Role role3 = deployService.installObject(role("Role3", "New desc", idSet("Role1", "Role2"), idSet("Privilege4"), "29f97d90-e39b-11e4-b571-0800200c9a66"));

		// Check everything can be persisted
		Context.flushSession();

		Role created = Context.getUserService().getRole("Role3");
		Assert.assertThat(created.getDescription(), is("New desc"));
		Assert.assertThat(created.getInheritedRoles(), containsInAnyOrder(role1, role2));
		Assert.assertThat(created.getPrivileges(), containsInAnyOrder(privilege4));
        Assert.assertThat(created.getUuid(), is("29f97d90-e39b-11e4-b571-0800200c9a66"));

		// Check updating existing
		Role role3b = deployService.installObject(role("Role3", "Updated desc", idSet("Role2"), null)); // No longer inherits Role1

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

	/**
	 * We previously encountered a problem where the session couldn't be flushed at certain stages during installation
	 * and re-installation of various roles and privileges. It seems like these objects can be cached via the UUID, and
	 * once we stopped needlessly overwriting UUIDs the problem was fixed.
	 */
	@Test
	public void integration_shouldWorkWithoutFlushes() {
		getCurrentSession().setFlushMode(FlushMode.MANUAL);

		deployService.installObject(privilege("Privilege1", "Testing"));
		deployService.installObject(role("Role1", "Testing", null, idSet("Privilege1")));
		deployService.installObject(role("Role2", "Testing", idSet("Role1"), null));

		deployService.installObject(privilege("Privilege1", "Testing"));

		deployService.installObject(role("Role1", "Testing", null, idSet("Privilege1")));

		Context.flushSession();
		getCurrentSession().setFlushMode(FlushMode.AUTO);
	}

	/**
	 * Replicates DPLY-1: Overwriting of existing roles can lose inherited roles
	 */
	@Test
	public void integration_shouldNotLoseInheritedRoles() throws Exception {
		deployService.installObject(role("Role1", "Testing", null, null));
		deployService.installObject(role("Role2", "Testing", idSet("Role1"), null));

		Context.flushSession();
		Context.clearSession();

		//TestUtil.printOutTableContents(getConnection(), "role_role");

		deployService.installObject(role("Role1", "Testing", null, null));
		deployService.installObject(role("Role2", "Testing", idSet("Role1"), null));

		Context.flushSession();
		Context.clearSession();

		//TestUtil.printOutTableContents(getConnection(), "role_role");

		Role role1 = MetadataUtils.existing(Role.class, "Role1");
		Role role2 = MetadataUtils.existing(Role.class, "Role2");

		Assert.assertThat(role2.getInheritedRoles(), contains(role1));
	}
	
	/**
	 * Gets the current hibernate session while taking care of the hibernate 3 and 4 differences.
	 * 
	 * @return the current hibernate session.
	 */
	private org.openmrs.api.db.hibernate.DbSession getCurrentSession() {
		try {
			return sessionFactory.getCurrentSession();
		}
		catch (NoSuchMethodError ex) {
			try {
				Method method = sessionFactory.getClass().getMethod("getCurrentSession", null);
				return (org.openmrs.api.db.hibernate.DbSession)method.invoke(sessionFactory, null);
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to get the current hibernate session", e);
			}
		}
	}
}