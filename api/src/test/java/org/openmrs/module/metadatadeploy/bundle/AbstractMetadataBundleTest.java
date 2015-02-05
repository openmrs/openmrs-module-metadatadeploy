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
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.VisitType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.MissingMetadataException;
import org.openmrs.module.metadatadeploy.source.ObjectSource;
import org.openmrs.module.metadatadeploy.sync.ObjectSynchronization;
import org.openmrs.module.metadatadeploy.sync.SyncResult;
import org.openmrs.module.metadatasharing.ImportMode;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link AbstractMetadataBundle}
 */
public class AbstractMetadataBundleTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private TestEmptyBundle emptyBundle;

	/**
	 * @see AbstractMetadataBundle#install(PackageDescriptor)
	 */
	@Test
	public void install_package() {
		final String TEST_PACKAGE_GROUP_UUID = "5c7fd8e7-e9a5-43a2-8ba5-c7694fc8db4a";
		final ClassLoader loader = getClass().getClassLoader();
		final String TEST_PACKAGE_FILENAME = "test-package-1.zip";

		PackageDescriptor pkg1 = new PackageDescriptor(TEST_PACKAGE_FILENAME, loader, TEST_PACKAGE_GROUP_UUID);
		emptyBundle.install(pkg1);

		// Check contained visit type was installed
		Assert.assertThat(MetadataUtils.possible(VisitType.class, "3371a4d4-f66f-4454-a86d-92c7b3da990c"), notNullValue());
	}

    /**
     * @see AbstractMetadataBundle#install(PackageDescriptor)
     */
    @Test
    public void install_package_with_import_mode() {
        final String TEST_PACKAGE_GROUP_UUID = "5c7fd8e7-e9a5-43a2-8ba5-c7694fc8db4a";
        final ClassLoader loader = getClass().getClassLoader();
        final String TEST_PACKAGE_FILENAME = "test-package-1.zip";

        PackageDescriptor pkg1 = new PackageDescriptor(TEST_PACKAGE_FILENAME, loader, TEST_PACKAGE_GROUP_UUID, ImportMode.PARENT_AND_CHILD);
        emptyBundle.install(pkg1);

        // Check contained visit type was installed
        Assert.assertThat(MetadataUtils.possible(VisitType.class, "3371a4d4-f66f-4454-a86d-92c7b3da990c"), notNullValue());
    }


    /**
	 * @see AbstractMetadataBundle#install(org.openmrs.module.metadatadeploy.source.ObjectSource)
	 */
	@Test
	public void install_shouldInstallAllObjectsFromSource() {
		TestEncounterTypeSource source = new TestEncounterTypeSource();
		List<EncounterType> installed = emptyBundle.install(source);

		EncounterType type1 = Context.getEncounterService().getEncounterType("name1");
		EncounterType type2 = Context.getEncounterService().getEncounterType("name2");

		Assert.assertThat(type1, notNullValue());
		Assert.assertThat(type2, notNullValue());
		Assert.assertThat(installed, contains(type1, type2));
	}

	/**
	 * @see AbstractMetadataBundle#install(org.openmrs.module.metadatadeploy.source.ObjectSource)
	 */
	@Test
	public void install_shouldThrowAPIExceptionIfSourceThrowsException() {
		BrokenEncounterTypeSource source = new BrokenEncounterTypeSource();

		try {
			emptyBundle.install(source);
			Assert.fail();
		}
		catch(APIException ex) {
		}

		// TODO figure out why this doesn't work. Does the test database not support transactions?

		// Check the first item in the source is not installed (i.e. transaction was rolled back)
		//Assert.assertThat(Context.getEncounterService().getEncounterType("name1"), nullValue());
	}

	/**
	 * @see AbstractMetadataBundle#sync(org.openmrs.module.metadatadeploy.source.ObjectSource, org.openmrs.module.metadatadeploy.sync.ObjectSynchronization)
	 */
	@Test
	public void sync_shouldRunSyncOperation() {
		ObjectSource<EncounterType> source = new TestEncounterTypeSource();

		SyncResult<EncounterType> result = emptyBundle.sync(source, new EncounterTypeSync());

		Assert.assertThat(result.getCreated(), hasSize(2));
	}

	/**
	 * @see AbstractMetadataBundle#uninstall(org.openmrs.OpenmrsObject, String)
	 */
	@Test
	public void uninstall_shouldRemoveObjectIfItExists() {
		// Fetch existing object
		Form form = emptyBundle.existing(Form.class, "d9218f76-6c39-45f4-8efa-4c5c6c199f50");
		Assert.assertThat(form, is(notNullValue()));

		// Check uninstall of existing object
		emptyBundle.uninstall(form, "Testing");

		Form retired = Context.getFormService().getFormByUuid("d9218f76-6c39-45f4-8efa-4c5c6c199f50");
		Assert.assertThat(retired, is(notNullValue()));
		Assert.assertThat(retired.isRetired(), is(true));
		Assert.assertThat(retired.getRetiredBy(), is(notNullValue()));
		Assert.assertThat(retired.getDateRetired(), is(notNullValue()));
		Assert.assertThat(retired.getRetireReason(), is("Testing"));

		// Check uninstall of null object (shouldn't do anything)
		emptyBundle.uninstall(null, "Testing");
	}

	/**
	 * @see AbstractMetadataBundle#possible(Class, String)
	 */
	@Test
	public void possible_shouldFetchExistingObject() {
		Form form1 = emptyBundle.possible(Form.class, "d9218f76-6c39-45f4-8efa-4c5c6c199f50");
		Assert.assertThat(form1, notNullValue());
		Assert.assertThat(form1.getName(), is("Basic Form"));
		Assert.assertThat(form1.getUuid(), is("d9218f76-6c39-45f4-8efa-4c5c6c199f50"));
	}

	/**
	 * @see AbstractMetadataBundle#possible(Class, String)
	 */
	@Test
	public void possible_shouldReturnNullForNonExisting() {
		Assert.assertThat(emptyBundle.possible(Form.class, "xxxxxxxx"), nullValue());
	}

	/**
	 * @see AbstractMetadataBundle#existing(Class, String)
	 */
	@Test
	public void existing_shouldFetchExistingObject() {
		// Check valid object
		Form form1 = emptyBundle.existing(Form.class, "d9218f76-6c39-45f4-8efa-4c5c6c199f50");
		Assert.assertThat(form1, notNullValue());
		Assert.assertThat(form1.getName(), is("Basic Form"));
		Assert.assertThat(form1.getUuid(), is("d9218f76-6c39-45f4-8efa-4c5c6c199f50"));
	}

	/**
	 * @see AbstractMetadataBundle#existing(Class, String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void existing_shouldThrowExceptionForNonExisting() {
		Form form2 = emptyBundle.existing(Form.class, "xxxxxxxx");
	}

	/**
	 * Bundle for testing
	 */
	@Component
	public static class TestEmptyBundle extends AbstractMetadataBundle {
		@Override
		public void install() {
		}
	}

	/**
	 * Working EncounterType source for testing
	 */
	public static class TestEncounterTypeSource implements ObjectSource<EncounterType> {

		private Queue<EncounterType> queue = new LinkedList<EncounterType>();

		public TestEncounterTypeSource() {
			queue.add(new EncounterType("name1", "desc1"));
			queue.add(new EncounterType("name2", "desc2"));
		}

		@Override
		public EncounterType fetchNext() throws Exception {
			return queue.poll();
		}
	}

	/**
	 * Non-working EncounterType source for testing that throws NPE on second call to fetchNext()
	 */
	public static class BrokenEncounterTypeSource implements ObjectSource<EncounterType> {

		private boolean first = true;

		@Override
		public EncounterType fetchNext() throws Exception {
			if (first) {
				first = false;
				return new EncounterType("name1", "desc1");

			}
			throw new NullPointerException();
		}
	}

	public static class EncounterTypeSync implements ObjectSynchronization<EncounterType> {

		@Override
		public List<EncounterType> fetchAllExisting() {
			return Context.getEncounterService().getAllEncounterTypes(true);
		}

		@Override
		public Object getObjectSyncKey(EncounterType obj) {
			return obj.getName();
		}

		@Override
		public boolean updateRequired(EncounterType incoming, EncounterType existing) {
			return true;
		}
	}
}