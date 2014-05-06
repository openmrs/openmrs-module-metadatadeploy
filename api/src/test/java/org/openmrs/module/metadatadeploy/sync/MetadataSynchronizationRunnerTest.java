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

package org.openmrs.module.metadatadeploy.sync;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.module.metadatadeploy.source.AbstractCsvResourceSource;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link MetadataSynchronizationRunner}
 */
public class MetadataSynchronizationRunnerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private LocationService locationService;

	@Test
	public void integration() throws Exception {
		MetadataSynchronizationRunner<Location> runner1 = new MetadataSynchronizationRunner<Location>(new TestCsvSource(), new TestUuidSynchronization());
		SyncResult<Location> result1 = runner1.run();

		Assert.assertThat(result1.getCreated(), hasSize(3)); // Created all 3 new locations in CSV
		Assert.assertThat(result1.getUpdated(), hasSize(0));
		Assert.assertThat(result1.getRetired(), hasSize(2)); // Retired 'Unknown Location' and 'Xanadu' from standardTestDataset.xml

		// Modify name of one of the new locations
		Location location2 = locationService.getLocationByUuid("C271874A-DACE-480A-8D55-840A96ADA70F");
		location2.setName("Wrong name");
		locationService.saveLocation(location2);

		// Second sync should reset modified name
		MetadataSynchronizationRunner<Location> runner2 = new MetadataSynchronizationRunner<Location>(new TestCsvSource(), new TestUuidSynchronization());
		SyncResult<Location> result2 = runner2.run();

		Assert.assertThat(result2.getCreated(), hasSize(0));
		Assert.assertThat(result2.getUpdated(), hasSize(1)); // Changed name of second location back to 'Location #2'
		Assert.assertThat(result2.getRetired(), hasSize(0));

		Assert.assertThat(location2.getName(), is("Location #2"));
	}

	/**
	 * Location source for testing
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

	/**
	 * Location UUID based synchronization for testing
	 */
	protected class TestUuidSynchronization implements ObjectSynchronization<Location> {

		@Override
		public List<Location> fetchAllExisting() {
			return locationService.getAllLocations(true);
		}

		@Override
		public Object getObjectSyncKey(Location obj) {
			return obj.getUuid();
		}

		@Override
		public boolean updateRequired(Location incoming, Location existing) {
			boolean objectsMatch = OpenmrsUtil.nullSafeEquals(incoming.getName(), existing.getName())
				&& OpenmrsUtil.nullSafeEquals(incoming.getDescription(), existing.getDescription());

			return !objectsMatch;
		}
	}
}