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

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.SerializingCustomDatatype;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.globalProperty;

/**
 * Tests for {@link GlobalPropertyDeployHandler}
 */
public class GlobalPropertyDeployHandlerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private MetadataDeployService deployService;

	/**
	 * Tests use of handler for installation
	 */
	@Test
	public void integration() {
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
}