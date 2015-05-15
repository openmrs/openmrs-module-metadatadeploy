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
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Privilege;
import org.openmrs.Program;
import org.openmrs.Role;
import org.openmrs.VisitType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.metadatadeploy.handler.impl.ConceptDeployHandler;
import org.openmrs.module.metadatadeploy.handler.impl.ProgramDeployHandler;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.form;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.idSet;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.location;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.privilege;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.role;

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

	@Autowired
	private TestBundle6 testBundle6;

	/**
	 * @see MetadataDeployServiceImpl#installBundles(java.util.Collection)
	 */
	@Test
	public void installBundles() {
		deployService.installBundles(Arrays.<MetadataBundle>asList(testBundle3, testBundle2, testBundle1));

		Privilege privilege1 = MetadataUtils.existing(Privilege.class, "Test Privilege 1");
		Privilege privilege2 = MetadataUtils.existing(Privilege.class, "Test Privilege 2");
		Role role1 = MetadataUtils.existing(Role.class, "Test Role 1");
		Role role2 = MetadataUtils.existing(Role.class, "Test Role 2");

		Assert.assertThat(role2.getInheritedRoles(), contains(role1));

		Assert.assertThat(role2.getPrivileges(), containsInAnyOrder(privilege1, privilege2));

		Assert.assertThat(Context.getEncounterService().getEncounterTypeByUuid(uuid("enc-type-uuid")), notNullValue());
		Assert.assertThat(Context.getFormService().getFormByUuid(uuid("form1-uuid")), notNullValue());
		Assert.assertThat(Context.getFormService().getFormByUuid(uuid("form2-uuid")), notNullValue());
	}

	/**
	 * @see MetadataDeployServiceImpl#installBundles(java.util.Collection)
	 */
	@Test(expected = APIException.class)
	public void installBundles_shouldThrowAPIExceptionIfFindBrokenRequirement() {
		deployService.installBundles(Arrays.<MetadataBundle>asList(testBundle1, new TestBundle4()));
	}

	/**
	 * @see MetadataDeployServiceImpl#installBundles(java.util.Collection)
	 */
	@Test(expected = APIException.class)
	public void installBundles_shouldThrowAPIExceptionIfBundleThrowsAnyException() {
		deployService.installBundles(Arrays.<MetadataBundle>asList(testBundle6));
	}

	/**
	 * @see MetadataDeployServiceImpl#installPackage(String, ClassLoader, String)
	 */
	@Test
	public void installPackage_shouldInstallPackagesOnlyIfNecessary() throws Exception {
		// Test package contains visit type { name: "Outpatient", uuid: "3371a4d4-f66f-4454-a86d-92c7b3da990c" }
		final String TEST_PACKAGE_GROUP_UUID = "5c7fd8e7-e9a5-43a2-8ba5-c7694fc8db4a";
		final String TEST_PACKAGE_FILENAME = "test-package-1.zip";

		// Check data isn't there
		Assert.assertThat(MetadataUtils.possible(VisitType.class, "3371a4d4-f66f-4454-a86d-92c7b3da990c"), nullValue());

		ClassLoader classLoader = getClass().getClassLoader();

		// Simulate first time startup
		Assert.assertThat(deployService.installPackage(TEST_PACKAGE_FILENAME, classLoader, TEST_PACKAGE_GROUP_UUID), is(true));
		Assert.assertThat(MetadataUtils.possible(VisitType.class, "3371a4d4-f66f-4454-a86d-92c7b3da990c"), notNullValue());

		// Simulate starting a second time
		Assert.assertThat(deployService.installPackage(TEST_PACKAGE_FILENAME, classLoader, TEST_PACKAGE_GROUP_UUID), is(false));
		Assert.assertThat(MetadataUtils.possible(VisitType.class, "3371a4d4-f66f-4454-a86d-92c7b3da990c"), notNullValue());
	}

	/**
	 * @see MetadataDeployServiceImpl#installPackage(String, ClassLoader, String)
	 */
	@Test(expected = APIException.class)
	public void installPackage_shouldThrowAPIExceptionForInvalidFilename() throws Exception {
		final String TEST_PACKAGE_GROUP_UUID = "5c7fd8e7-e9a5-43a2-8ba5-c7694fc8db4a";

		deployService.installPackage("xxx.zip", getClass().getClassLoader(), TEST_PACKAGE_GROUP_UUID);
	}

	/**
	 * @see MetadataDeployServiceImpl#installPackage(String, ClassLoader, String)
	 */
	@Test(expected = APIException.class)
	public void installPackage_shouldThrowAPIExceptionForNonExistentPackage() throws Exception {
		final String TEST_PACKAGE_GROUP_UUID = "5c7fd8e7-e9a5-43a2-8ba5-c7694fc8db4a";

		deployService.installPackage("xxx-1.zip", getClass().getClassLoader(), TEST_PACKAGE_GROUP_UUID);
	}

	/**
	 * @see MetadataDeployServiceImpl#installPackage(String, ClassLoader, String)
	 */
	@Test(expected = APIException.class)
	public void installPackage_shouldThrowAPIExceptionForCorruptPackage() throws Exception {
		final String TEST_CORRUPTPACKAGE_GROUP_UUID = "83E38E01-5ACA-4D64-8560-5D4587F62D4A";

		deployService.installPackage("test-corruptpackage-1.zip", getClass().getClassLoader(), TEST_CORRUPTPACKAGE_GROUP_UUID);
	}

	/**
	 * @see MetadataDeployServiceImpl#installPackage(String, ClassLoader, String)
	 */
	@Test(expected = APIException.class)
	public void installObject_shouldThrowAPIExceptionForObjectWithoutIdentifier() throws Exception {
		Location location = new Location();
		location.setName("Name");
		location.setDescription("Testing");
		location.setUuid(null);

		deployService.installObject(location);
	}

	/**
	 * @see MetadataDeployServiceImpl#fetchObject(Class, String)
	 */
	@Test
	public void fetchObject_shouldFetchObjectByIdentifier() throws Exception {
		Assert.assertThat(deployService.fetchObject(Role.class, "Anonymous"), is(Context.getUserService().getRole("Anonymous")));
		Assert.assertThat(deployService.fetchObject(Program.class, "da4a0391-ba62-4fad-ad66-1e3722d16380"), is(Context.getProgramWorkflowService().getProgram(1)));
	}

	/**
	 * @see MetadataDeployServiceImpl#saveObject(org.openmrs.OpenmrsObject)
	 */
	@Test
	public void saveObject_shouldSaveObject() throws Exception {
		Location location = new Location();
		location.setName("Test");

		deployService.saveObject(location);

		Assert.assertThat(location.getId(), notNullValue());

		// Check everything can be persisted
		Context.flushSession();
	}

	/**
	 * @see MetadataDeployServiceImpl#overwriteObject(org.openmrs.OpenmrsObject, org.openmrs.OpenmrsObject)
	 */
	@Test
	public void overwriteObject_shouldOverwriteObject() throws Exception {
		Location incoming = location("New name", "New desc", "68265F64-BD50-4E4F-BA1F-23F24E301FBC");
		Location existing = MetadataUtils.existing(Location.class, "9356400c-a5a2-4532-8f2b-2361b3446eb8"); // Xanadu

		deployService.overwriteObject(incoming, existing);

		Assert.assertThat(existing.getName(), is("New name"));
		Assert.assertThat(existing.getDescription(), is("New desc"));
		Assert.assertThat(existing.getUuid(), is("68265F64-BD50-4E4F-BA1F-23F24E301FBC"));

		// Check everything can be persisted
		Context.flushSession();
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
	@Test
	public void getHandler_shouldReturnHandlerForSuperclassOfProxy() throws Exception {
		Class<? extends Concept> clazz = Context.getConceptService().getConcept(21).getAnswers().iterator().next().getAnswerConcept().getClass();
		MetadataDeployServiceImpl impl = getProxyTarget(deployService);

		// Verify that this test is actually going to test what it's supposed to be testing
		Assert.assertTrue(clazz.getSimpleName().contains("_$$"));

		Assert.assertThat(impl.getHandler(clazz), instanceOf(ConceptDeployHandler.class));
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
			install(privilege("Test Privilege 1", "Testing", "737b6460-e39b-11e4-b571-0800200c9a66"));

			install(role("Test Role 1", "Testing", null, idSet("Test Privilege 1"), "fc532160-e39b-11e4-b571-0800200c9a66"));
			install(role("Test Role 2", "Inherits from role 1", idSet("Test Role 1"), null, "0f56fd40-e39c-11e4-b571-0800200c9a66"));

			install(encounterType("Test Encounter", "Testing", uuid("enc-type-uuid")));
		}
	}

	@Component
	@Requires({ TestBundle1.class })
	public static class TestBundle2 extends AbstractMetadataBundle {
		@Override
		public void install() {
			install(privilege("Test Privilege 1", "New description", "737b6460-e39b-11e4-b571-0800200c9a66"));
			install(privilege("Test Privilege 2", "Testing", "7c764800-e39b-11e4-b571-0800200c9a66"));

			install(role("Test Role 2", "Inherits from role 1", idSet("Test Role 1"), idSet("Test Privilege 1", "Test Privilege 2"), "0f56fd40-e39c-11e4-b571-0800200c9a66"));

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
	 * Throws an NPE on install
	 */
	@Component
	public static class TestBundle6 extends AbstractMetadataBundle {
		@Override
		public void install() {
			throw new NullPointerException();
		}
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