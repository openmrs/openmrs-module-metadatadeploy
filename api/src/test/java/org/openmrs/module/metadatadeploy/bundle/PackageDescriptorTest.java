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

import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link PackageDescriptor}
 */
public class PackageDescriptorTest {

	@Test
	public void integration() {
		final String TEST_PACKAGE_GROUP_UUID = "5c7fd8e7-e9a5-43a2-8ba5-c7694fc8db4a";
		final ClassLoader loader = getClass().getClassLoader();
		final String TEST_PACKAGE_FILENAME = "test-package-1.zip";

		PackageDescriptor pkg1 = new PackageDescriptor(TEST_PACKAGE_FILENAME, loader, TEST_PACKAGE_GROUP_UUID);

		Assert.assertThat(pkg1.getFilename(), is(TEST_PACKAGE_FILENAME));
		Assert.assertThat(pkg1.getClassLoader(), is(loader));
		Assert.assertThat(pkg1.getGroupUuid(), is(TEST_PACKAGE_GROUP_UUID));
	}
}