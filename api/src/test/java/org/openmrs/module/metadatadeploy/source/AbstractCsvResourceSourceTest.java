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

package org.openmrs.module.metadatadeploy.source;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;

import java.io.IOException;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link AbstractCsvResourceSource}
 */
public class AbstractCsvResourceSourceTest {

	@Test
	public void integration() throws Exception {
		AbstractCsvResourceSource<Location> csvSource = new TestCsvSource();

		Location location1 = csvSource.fetchNext();
		Assert.assertThat(location1.getName(), is("Location #1"));
		Assert.assertThat(location1.getDescription(), is("Desc1"));

		Location location2 = csvSource.fetchNext();
		Assert.assertThat(location2.getName(), is("Location #2"));
		Assert.assertThat(location2.getDescription(), is("Desc2"));

		Location location3 = csvSource.fetchNext();
		Assert.assertThat(location3.getName(), is("Location #3"));
		Assert.assertThat(location3.getDescription(), is("Desc3"));

		Assert.assertThat(csvSource.fetchNext(), nullValue());
	}

	/**
	 * Implementation for testing
	 */
	protected class TestCsvSource extends AbstractCsvResourceSource<Location> {

		public TestCsvSource() throws IOException {
			super("test-location-source.csv", true);
		}

		@Override
		protected Location parseLine(String[] line) {
			Location location = new Location();
			location.setName(line[0]);
			location.setDescription(line[1]);
			location.setUuid(line[2]);
			return location;
		}
	}
}