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
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.GlobalProperty;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.SerializingCustomDatatype;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.handler.impl.ProgramDeployHandler;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.*;

/**
 * Tests for {@link MetadataDeployServiceImpl}
 */
public class MetadataDeployServiceImplTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private MetadataDeployService deployService;

	/**
	 * @see MetadataDeployServiceImpl#installObject(org.openmrs.OpenmrsObject)
	 */
	@Test
	public void installObject_shouldInstallEncounterType() {
		deployService.installObject(encounterType("Test Encounter", "Testing", "obj1-uuid"));

		EncounterType created = Context.getEncounterService().getEncounterTypeByUuid("obj1-uuid");
		Assert.assertThat(created.getName(), is("Test Encounter"));
		Assert.assertThat(created.getDescription(), is("Testing"));

		// Check updating existing
		deployService.installObject(encounterType("New name", "New desc", "obj1-uuid"));

		EncounterType updated = Context.getEncounterService().getEncounterTypeByUuid("obj1-uuid");
		Assert.assertThat(updated.getName(), is("New name"));
		Assert.assertThat(updated.getDescription(), is("New desc"));

		// Retire object
		Context.getEncounterService().retireEncounterType(updated, "Testing");

		// Check that re-install unretires
		deployService.installObject(encounterType("Unretired name", "Unretired desc", "obj1-uuid"));

		EncounterType unretired = Context.getEncounterService().getEncounterTypeByUuid("obj1-uuid");
		Assert.assertThat(unretired.getName(), is("Unretired name"));
		Assert.assertThat(unretired.getDescription(), is("Unretired desc"));
		Assert.assertThat(unretired.isRetired(), is(false));
		Assert.assertThat(unretired.getDateRetired(), is(nullValue()));
		Assert.assertThat(unretired.getRetiredBy(), is(nullValue()));
		Assert.assertThat(unretired.getRetireReason(), is(nullValue()));
	}

	/**
	 * @see MetadataDeployServiceImpl#installObject(org.openmrs.OpenmrsObject)
	 */
	@Test
	public void installObject_shouldInstallForm() throws Exception {
		// Check creating new
		deployService.installObject(encounterType("Test Encounter", "Testing", "enc-type1-uuid"));
		deployService.installObject(form("Test Form #1", "Testing", "enc-type1-uuid", "1.0", "form-uuid"));

		Form created = Context.getFormService().getFormByUuid("form-uuid");
		Assert.assertThat(created.getName(), is("Test Form #1"));
		Assert.assertThat(created.getDescription(), is("Testing"));
		Assert.assertThat(created.getEncounterType(), is(Context.getEncounterService().getEncounterTypeByUuid("enc-type1-uuid")));
		Assert.assertThat(created.getVersion(), is("1.0"));

		// Check updating existing
		deployService.installObject(encounterType("Other Encounter", "Testing", "enc-type2-uuid"));
		deployService.installObject(form("New name", "New desc", "enc-type2-uuid", "2.0", "form-uuid"));

		Form updated = Context.getFormService().getFormByUuid("form-uuid");
		Assert.assertThat(updated.getName(), is("New name"));
		Assert.assertThat(updated.getDescription(), is("New desc"));
		Assert.assertThat(updated.getEncounterType(), is(Context.getEncounterService().getEncounterTypeByUuid("enc-type2-uuid")));
		Assert.assertThat(updated.getVersion(), is("2.0"));
	}

	/**
	 * @see MetadataDeployServiceImpl#installObject(org.openmrs.OpenmrsObject)
	 */
	@Test
	public void installObject_shouldInstallGlobalProperty() throws Exception {
		// Check creating new
		deployService.installObject(globalProperty("test.property", "Testing", "Value"));

		GlobalProperty created = Context.getAdministrationService().getGlobalPropertyObject("test.property");
		Assert.assertThat(created.getDescription(), is("Testing"));
		Assert.assertThat(created.getValue(), is((Object) "Value"));

		// Check updating existing
		deployService.installObject(globalProperty("test.property", "New desc", "New value"));

		GlobalProperty updated = Context.getAdministrationService().getGlobalPropertyObject("test.property");
		Assert.assertThat(updated.getDescription(), is("New desc"));
		Assert.assertThat(updated.getValue(), is((Object) "New value"));

		// Check updating existing with null value should retain existing value
		deployService.installObject(globalProperty("test.property", "Other desc", null));

		updated = Context.getAdministrationService().getGlobalPropertyObject("test.property");
		Assert.assertThat(updated.getDescription(), is("Other desc"));
		Assert.assertThat(updated.getValue(), is((Object) "New value"));

		// Check updating existing with blank value should retain existing value
		deployService.installObject(globalProperty("test.property", "Other desc", ""));

		updated = Context.getAdministrationService().getGlobalPropertyObject("test.property");
		Assert.assertThat(updated.getDescription(), is("Other desc"));
		Assert.assertThat(updated.getValue(), is((Object) "New value"));

		// Check with custom data type and null value
		deployService.installObject(globalProperty("test.property2", "Testing", TestingDatatype.class, "config", null));

		GlobalProperty custom = Context.getAdministrationService().getGlobalPropertyObject("test.property2");
		Assert.assertThat(custom.getDatatypeClassname(), is(TestingDatatype.class.getName()));
		Assert.assertThat(custom.getValue(), is(nullValue()));

		// Check with custom data type and non-null value
		deployService.installObject(encounterType("Test Encounter", "Testing", "enc-type-uuid"));

		EncounterType encType = Context.getEncounterService().getEncounterTypeByUuid("enc-type-uuid");

		deployService.installObject(globalProperty("test.property2", "Testing", TestingDatatype.class, "config", encType));

		custom = Context.getAdministrationService().getGlobalPropertyObject("test.property2");
		Assert.assertThat(custom.getDatatypeClassname(), is(TestingDatatype.class.getName()));
		Assert.assertThat(custom.getValue(), is((Object) encType));

		// Check update with custom data type and null value should retain existing value
		deployService.installObject(encounterType("Test Encounter", "Testing", "enc-type-uuid"));
		deployService.installObject(globalProperty("test.property2", "Testing", TestingDatatype.class, "config", null));

		custom = Context.getAdministrationService().getGlobalPropertyObject("test.property2");
		Assert.assertThat(custom.getDatatypeClassname(), is(TestingDatatype.class.getName()));
		Assert.assertThat(custom.getValue(), is((Object) encType));
	}

	/**
	 * @see MetadataDeployServiceImpl#installObject(org.openmrs.OpenmrsObject)
	 */
	@Test
	public void installObject_shouldInstallProgram() throws Exception {
		// Existing concepts in test data
		final String HIV_PROGRAM_UUID = "0a9afe04-088b-44ca-9291-0a8c3b5c96fa";
		final String MALARIA_PROGRAM_UUID = "f923524a-b90c-4870-a948-4125638606fd";

		// Check creating new
		deployService.installObject(program("Test Program", "Testing", HIV_PROGRAM_UUID, "obj1-uuid"));

		Program created = Context.getProgramWorkflowService().getProgramByUuid("obj1-uuid");
		Assert.assertThat(created.getName(), is("Test Program"));
		Assert.assertThat(created.getDescription(), is("Testing"));
		Assert.assertThat(created.getConcept(), is(Context.getConceptService().getConceptByUuid(HIV_PROGRAM_UUID)));

		// Check updating existing
		deployService.installObject(program("New name", "New desc", MALARIA_PROGRAM_UUID, "obj1-uuid"));

		Program updated = Context.getProgramWorkflowService().getProgramByUuid("obj1-uuid");
		Assert.assertThat(updated.getName(), is("New name"));
		Assert.assertThat(updated.getDescription(), is("New desc"));
		Assert.assertThat(updated.getConcept(), is(Context.getConceptService().getConceptByUuid(MALARIA_PROGRAM_UUID)));

		// Check update existing when name conflicts
		deployService.installObject(program("New name", "Diff desc", MALARIA_PROGRAM_UUID, "obj2-uuid"));
		updated = Context.getProgramWorkflowService().getProgramByUuid("obj2-uuid");
		Assert.assertThat(updated.getName(), is("New name"));
		Assert.assertThat(updated.getDescription(), is("Diff desc"));

		Program old = Context.getProgramWorkflowService().getProgramByUuid("obj1-uuid");
		Assert.assertThat(old, is(nullValue()));
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

	/**
	 * Custom data type class for testing based on encounter types
	 */
	public static class TestingDatatype extends SerializingCustomDatatype<EncounterType> {

		@Override
		public String serialize(EncounterType typedValue) {
			return typedValue != null ? String.valueOf(typedValue.getId()) : "";
		}

		@Override
		public EncounterType deserialize(String serializedValue) {
			return StringUtils.isNotEmpty(serializedValue) ? Context.getEncounterService().getEncounterType(Integer.valueOf(serializedValue)) : null;
		}
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