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
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Form;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.Privilege;
import org.openmrs.Role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link ObjectUtils}
 */
public class ObjectUtilsTest {

	/**
	 * @see ObjectUtils#overwrite(org.openmrs.OpenmrsObject, org.openmrs.OpenmrsObject, java.util.Set)
	 */
	@Test
	public void integration() {
		new ObjectUtils();
	}

	/**
	 * @see ObjectUtils#overwrite(org.openmrs.OpenmrsObject, org.openmrs.OpenmrsObject, java.util.Set)
	 */
	@Test
	public void overwrite_shouldCopySourceFieldValuesToTarget() {
		TestClass2 prop = new TestClass2();

		TestClass1 target = new TestClass1();
		TestClass1 source = new TestClass1(null, "test", 123.0, prop);

		ObjectUtils.overwrite(source, target, null);

		Assert.assertThat(target.getId(), nullValue());
		Assert.assertThat(target.getStringValue(), is("test"));
		Assert.assertThat(target.getDoubleValue(), is(123.0));
		Assert.assertThat(target.getObjectValue(), is(prop));
	}

	/**
	 * @see ObjectUtils#overwrite(org.openmrs.OpenmrsObject, org.openmrs.OpenmrsObject, java.util.Set)
	 */
	@Test
	public void overwrite_shouldUpdateBackReferencesToSource() {
		TestClass2 prop = new TestClass2();

		TestClass1 target = new TestClass1();
		TestClass1 source = new TestClass1(null, "test", 123, prop);

		TestClass2 owned1 = new TestClass2();
		TestClass2 owned2 = new TestClass2();
		source.addCollectionValue(owned1);
		source.addCollectionValue(owned2);

		ObjectUtils.overwrite(source, target, null);

		Assert.assertThat(target.getObjectValue().getOwner(), is(target));
		Assert.assertThat(target.getCollectionValues(), contains(owned1, owned2));
		Assert.assertThat(target.getCollectionValues().get(0).getOwner(), is(target));
		Assert.assertThat(target.getCollectionValues().get(1).getOwner(), is(target));
	}

	/**
	 * @see ObjectUtils#overwrite(org.openmrs.OpenmrsObject, org.openmrs.OpenmrsObject, java.util.Set)
	 */
	@Test
	public void overwrite_shouldIgnoreExcludedFields() {
		TestClass1 target = new TestClass1(1, "abc", 123.0, null);
		TestClass1 source = new TestClass1(2, "xyz", 234.0, null);

		ObjectUtils.overwrite(source, target, Collections.singleton("stringValue"));

		Assert.assertThat(target.getId(), is(2));
		Assert.assertThat(target.getStringValue(), is("abc"));
		Assert.assertThat(target.getDoubleValue(), is(234.0));
	}

	/**
	 * @see ObjectUtils#overwrite(org.openmrs.OpenmrsObject, org.openmrs.OpenmrsObject, java.util.Set)
	 */
	@Test
	public void overwrite_shouldReplaceNullCollectionsInTarget() {
		TestClass1 target = new TestClass1();
		TestClass1 source = new TestClass1(null, "test", 123, null);

		TestClass2 owned1 = new TestClass2();

		source.addCollectionValue(owned1);

		ObjectUtils.overwrite(source, target, null);

		Assert.assertThat(target.getCollectionValues(), sameInstance(source.getCollectionValues()));
		Assert.assertThat(target.getCollectionValues(), contains(owned1));
	}

	/**
	 * @see ObjectUtils#overwrite(org.openmrs.OpenmrsObject, org.openmrs.OpenmrsObject, java.util.Set)
	 */
	@Test
	public void overwrite_shouldNotReplaceNonNullCollectionsInTarget() {
		TestClass1 target = new TestClass1();
		TestClass1 source = new TestClass1(null, "test", 123, null);

		TestClass2 owned1 = new TestClass2();
		TestClass2 owned2 = new TestClass2();

		target.addCollectionValue(owned1);
		source.addCollectionValue(owned2);

		Collection<TestClass2> preCopyOwned = target.getCollectionValues();

		ObjectUtils.overwrite(source, target, null);

		Assert.assertThat(target.getCollectionValues(), sameInstance(preCopyOwned));
		Assert.assertThat(target.getCollectionValues(), contains(owned2));
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.ObjectUtils#usesId(org.openmrs.OpenmrsObject)
	 */
	@Test
	public void usesId() {
		Assert.assertThat(ObjectUtils.usesId(new Location()), is(true));
		Assert.assertThat(ObjectUtils.usesId(new Form()), is(true));

		Assert.assertThat(ObjectUtils.usesId(new GlobalProperty()), is(false));
		Assert.assertThat(ObjectUtils.usesId(new Role()), is(false));
		Assert.assertThat(ObjectUtils.usesId(new Privilege()), is(false));
	}

	/**
	 * Class for testing
	 */
	public static class TestClass1 extends BaseOpenmrsObject {

		private Integer id;
		private String stringValue;
		private double doubleValue;
		private TestClass2 objectValue;
		private List<TestClass2> collectionValues;

		public TestClass1() {
		}

		public TestClass1(Integer id, String stringValue, double doubleValue, TestClass2 objectValue) {
			this.id = id;
			this.stringValue = stringValue;
			this.doubleValue = doubleValue;
			this.objectValue = objectValue;

			if (this.objectValue != null) {
				this.objectValue.setOwner(this);
			}
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getStringValue() {
			return stringValue;
		}

		public double getDoubleValue() {
			return doubleValue;
		}

		public TestClass2 getObjectValue() {
			return objectValue;
		}

		public void addCollectionValue(TestClass2 class2) {
			if (collectionValues == null) {
				collectionValues = new ArrayList<TestClass2>();
			}

			class2.setOwner(this);
			collectionValues.add(class2);
		}

		public List<TestClass2> getCollectionValues() {
			return collectionValues;
		}
	}

	public static class TestClass2 extends BaseOpenmrsObject {

		private Integer id;

		private TestClass1 owner;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public TestClass1 getOwner() {
			return owner;
		}

		public void setOwner(TestClass1 owner) {
			this.owner = owner;
		}
	}
}