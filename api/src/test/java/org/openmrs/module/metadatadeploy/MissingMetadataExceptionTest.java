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

package org.openmrs.module.metadatadeploy;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.VisitType;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link MissingMetadataException}
 */
public class MissingMetadataExceptionTest extends BaseModuleContextSensitiveTest {

	@Test
	public void integration() {
		try {
			MetadataUtils.getVisitType("invalid-uuid");
		}
		catch (MissingMetadataException ex) {
			Assert.assertThat(ex.getObjectClass().equals(VisitType.class), is(true));
			Assert.assertThat(ex.getObjectIdentifier(), is("invalid-uuid"));
		}
	}
}