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

package org.openmrs.module.metadatadeploy.api.impl;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Privilege;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.metadatadeploy.handler.impl.ProgramDeployHandler;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.*;

/**
 * Tests for {@link MetadataDeployServiceImpl}
 */
public class MetadataDeployServiceImplTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private MetadataDeployService deployService;

	@Autowired
	private TestBundle1 testBundle1;

	@Autowired
	private TestBundle2 testBundle2;

	@Autowired
	private TestBundle3 testBundle3;

	/**
	 * @see MetadataDeployServiceImpl#installBundles(java.util.Collection)
	 */
	@Test
	public void installBundles() {
		List<MetadataBundle> bundles = new ArrayList<MetadataBundle>();
		bundles.addAll(Arrays.asList(testBundle3, testBundle2, testBundle1));

		deployService.installBundles(bundles);

		Privilege privilege1 = Context.getUserService().getPrivilege("Test Privilege 1");
		Privilege privilege2 = Context.getUserService().getPrivilege("Test Privilege 2");

		Assert.assertThat(privilege1, is(notNullValue()));
		Assert.assertThat(privilege2, is(notNullValue()));
		Assert.assertThat(Context.getUserService().getRole("Test Role 1"), is(notNullValue()));
		Assert.assertThat(Context.getUserService().getRole("Test Role 2"), is(notNullValue()));
		//Assert.assertThat(Context.getUserService().getRole("Test Role 2").getPrivileges(), contains(privilege1, privilege2));

		Assert.assertThat(Context.getEncounterService().getEncounterTypeByUuid(uuid("enc-type-uuid")), is(notNullValue()));
		Assert.assertThat(Context.getFormService().getFormByUuid(uuid("form1-uuid")), is(notNullValue()));
		Assert.assertThat(Context.getFormService().getFormByUuid(uuid("form2-uuid")), is(notNullValue()));
	}

	/**
	 * @see MetadataDeployServiceImpl#installBundles(java.util.Collection)
	 */
	@Test(expected = RuntimeException.class)
	public void installBundles_shouldThrowExceptionIfFindBrokenRequirement() {
		List<MetadataBundle> bundles = new ArrayList<MetadataBundle>();
		bundles.addAll(Arrays.asList(testBundle1, new TestBundle4()));

		deployService.installBundles(bundles);
	}

	/**
	 * @see MetadataDeployServiceImpl#installPackage(String, ClassLoader, String)
	 */
	@Test
	public void installPackage_shouldInstallPackagesOnlyIfNecessary() throws Exception {

		// Test package contains visit type { name: "Outpatient", uuid: "3371a4d4-f66f-4454-a86d-92c7b3da990c" }
		final String TEST_PACKAGE_GROUP_UUID = "5c7fd8e7-e9a5-43a2-8ba5-c7694fc8db4a";
		final String TEST_PACKAGE_FILENAME = "test-package-1.zip";

		try {
			// Check data isn't there
			MetadataUtils.getVisitType("3371a4d4-f66f-4454-a86d-92c7b3da990c");
			Assert.fail();
		}
		catch (IllegalArgumentException ex) {
		}

		ClassLoader classLoader = this.getClass().getClassLoader();

		// Simulate first time startup
		Assert.assertThat(deployService.installPackage(TEST_PACKAGE_FILENAME, classLoader, TEST_PACKAGE_GROUP_UUID), is(true));
		Assert.assertThat(MetadataUtils.getVisitType("3371a4d4-f66f-4454-a86d-92c7b3da990c"), is(notNullValue()));

		// Simulate starting a second time
		Assert.assertThat(deployService.installPackage(TEST_PACKAGE_FILENAME, classLoader, TEST_PACKAGE_GROUP_UUID), is(false));
		Assert.assertThat(MetadataUtils.getVisitType("3371a4d4-f66f-4454-a86d-92c7b3da990c"), is(notNullValue()));
	}

	/**
	 * @see MetadataDeployServiceImpl#getHandler(Class)
	 */
	@Test
	public void getHandler_shouldReturnHandlerForClass() throws Exception {
		MetadataDeployServiceImpl impl = getProxyTarget(deployService);

		Assert.assertThat(impl.getHandler(Program.class), instanceOf(ProgramDeployHandler.class));
	}

	/**
	 * @see MetadataDeployServiceImpl#getHandler(Class)
	 */
	@Test(expected = RuntimeException.class)
	public void getHandler_shouldThrowExceptionIfNoHandlerForClass() throws Exception {
		MetadataDeployServiceImpl impl = getProxyTarget(deployService);

		impl.getHandler(Patient.class);
	}

	@Component
	public static class TestBundle1 extends AbstractMetadataBundle {
		@Override
		public void install() {
			install(privilege("Test Privilege 1", "Testing"));

			install(role("Test Role 1", "Testing", null, idSet("Test Privilege 1")));
			install(role("Test Role 2", "Inherits from role 1", idSet("Test Role 1"), null));

			install(encounterType("Test Encounter", "Testing", uuid("enc-type-uuid")));
		}
	}

	@Component
	@Requires({ TestBundle1.class })
	public static class TestBundle2 extends AbstractMetadataBundle {
		@Override
		public void install() {
			install(privilege("Test Privilege 1", "New description"));
			install(privilege("Test Privilege 2", "Testing"));

			install(role("Test Role 2", "Inherits from role 1", idSet("Test Role 1"), idSet("Test Privilege 1", "Test Privilege 2")));

			install(form("Test Form #1", "Testing", uuid("enc-type-uuid"), "1", uuid("form1-uuid")));
		}
	}

	@Component
	@Requires({ TestBundle1.class })
	public static class TestBundle3 extends AbstractMetadataBundle {
		@Override
		public void install() {
			install(form("Test Form #2", "Testing", uuid("enc-type-uuid"), "1", uuid("form2-uuid")));
		}
	}

	/**
	 * Has broken requirement because TestBundle5 isn't instantiated as a component
	 */
	@Requires({ TestBundle5.class })
	public static class TestBundle4 extends AbstractMetadataBundle {
		@Override
		public void install() { }
	}

	public static class TestBundle5 extends AbstractMetadataBundle {
		@Override
		public void install() { }
	}

	/**
	 * Converts a simple identifier to a valid UUID (at least by our standards)
	 * @return the UUID
	 */
	protected static String uuid(String name) {
		return StringUtils.rightPad(name, 36, 'x');
	}

	/**
	 * Gets the target of a proxy object
	 * @param proxy the proxy
	 * @param <T> the target type
	 * @return the target of the proxy
	 * @throws Exception
	 */
	public static <T> T getProxyTarget(Object proxy) throws Exception {
		while ((AopUtils.isJdkDynamicProxy(proxy))) {
			return getProxyTarget(((Advised) proxy).getTargetSource().getTarget());
		}
		return (T) proxy;
	}
}