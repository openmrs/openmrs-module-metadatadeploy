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

package org.openmrs.module.metadatadeploy.bundle;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link AbstractMetadataBundle}
 */
public class AbstractMetadataBundleTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private TestBundle testBundle;

	/**
	 * @see AbstractMetadataBundle#existing(Class, String)
	 */
	@Test
	public void existing_shouldFetchExistingObject() {
		// Check valid object
		Form form1 = testBundle.existing(Form.class, "d9218f76-6c39-45f4-8efa-4c5c6c199f50");
		Assert.assertThat(form1, is(notNullValue()));
		Assert.assertThat(form1.getName(), is("Basic Form"));
		Assert.assertThat(form1.getUuid(), is("d9218f76-6c39-45f4-8efa-4c5c6c199f50"));

		// Check invalid object
		Form form2 = testBundle.existing(Form.class, "xxxxxxxx");
		Assert.assertThat(form2, is(nullValue()));
	}

	/**
	 * @see AbstractMetadataBundle#uninstall(org.openmrs.OpenmrsObject, String)
	 */
	@Test
	public void uninstall_shouldRemoveObjectIfItExists() {
		// Fetch existing object
		Form form = testBundle.existing(Form.class, "d9218f76-6c39-45f4-8efa-4c5c6c199f50");
		Assert.assertThat(form, is(notNullValue()));

		// Check uninstall of existing object
		testBundle.uninstall(form, "Testing");

		Form retired = Context.getFormService().getFormByUuid("d9218f76-6c39-45f4-8efa-4c5c6c199f50");
		Assert.assertThat(retired, is(notNullValue()));
		Assert.assertThat(retired.isRetired(), is(true));
		Assert.assertThat(retired.getRetiredBy(), is(notNullValue()));
		Assert.assertThat(retired.getDateRetired(), is(notNullValue()));
		Assert.assertThat(retired.getRetireReason(), is("Testing"));

		// Check uninstall of null object (shouldn't do anything)
		testBundle.uninstall(null, "Testing");
	}

	/**
	 * Bundle for testing
	 */
	@Component
	public static class TestBundle extends AbstractMetadataBundle {
		@Override
		public void install() {
			//To change body of implemented methods use File | Settings | File Templates.
		}
	}
}