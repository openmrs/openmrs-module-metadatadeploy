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
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.form;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.formResource;

/**
 * Tests for {@link FormResourceDeployHandler}
 */
public class FormResourceDeployHandlerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private MetadataDeployService deployService;

	/**
	 * Tests use of handler for installation
	 */
	@Test
	public void integration() {
		deployService.installObject(encounterType("Test Encounter", "Testing", "enc-type1-uuid"));
		Form form1 = deployService.installObject(form("Test Form #1", "Testing", "enc-type1-uuid", "1.0", "form1-uuid"));

		// Check installing new
		deployService.installObject(formResource("resource1", "form1-uuid", FreeTextDatatype.class, null, "New value"));

		FormResource created = Context.getFormService().getFormResource(form1, "resource1");
		Assert.assertThat(created.getName(), is("resource1"));
		Assert.assertThat(created.getForm(), is(form1));
		Assert.assertThat(created.getDatatypeClassname(), is(FreeTextDatatype.class.getName()));
		Assert.assertThat(created.getDatatypeConfig(), nullValue());
		Assert.assertThat(created.getValue(), is((Object) "New value"));

		// Check updating existing
		deployService.installObject(formResource("resource1", "form1-uuid", FreeTextDatatype.class, null, "Updated value"));

		FormResource updated = Context.getFormService().getFormResource(form1, "resource1");
		Assert.assertThat(updated.getName(), is("resource1"));
		Assert.assertThat(updated.getForm(), is(form1));
		Assert.assertThat(updated.getDatatypeClassname(), is(FreeTextDatatype.class.getName()));
		Assert.assertThat(updated.getDatatypeConfig(), nullValue());
		Assert.assertThat(updated.getValue(), is((Object) "Updated value"));

		// Check uninstall purges
		deployService.uninstallObject(deployService.fetchObject(FormResource.class, updated.getUuid()), "Testing");

		Assert.assertThat(Context.getFormService().getFormResource(form1, "resource1"), nullValue());

		// Check re-install re-creates
		deployService.installObject(formResource("resource1", "form1-uuid", FreeTextDatatype.class, null, "Unpurged value"));

		FormResource unpurged = Context.getFormService().getFormResource(form1, "resource1");
		Assert.assertThat(unpurged.getName(), is("resource1"));
		Assert.assertThat(unpurged.getForm(), is(form1));
		Assert.assertThat(unpurged.getDatatypeClassname(), is(FreeTextDatatype.class.getName()));
		Assert.assertThat(unpurged.getDatatypeConfig(), nullValue());
		Assert.assertThat(unpurged.getValue(), is((Object) "Unpurged value"));

		// Check everything can be persisted
		Context.flushSession();
	}
}