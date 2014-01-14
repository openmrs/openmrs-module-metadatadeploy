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
	 * @see ObjectUtils#copy(org.openmrs.OpenmrsObject, org.openmrs.OpenmrsObject, java.util.Set)
	 */
	@Test
	public void copy_shouldCopySourceFieldValuesToTarget() {
		TestClass1 target = new TestClass1();
		TestClass1 source = new TestClass1(null, "test", 123.0);

		ObjectUtils.copy(source, target, null);

		Assert.assertThat(target.getId(), nullValue());
		Assert.assertThat(target.getStringValue(), is("test"));
		Assert.assertThat(target.getDoubleValue(), is(123.0));
	}

	/**
	 * @see ObjectUtils#copy(org.openmrs.OpenmrsObject, org.openmrs.OpenmrsObject, java.util.Set)
	 */
	@Test
	public void copy_shouldUpdateBackReferencesToSource() {
		TestClass1 target = new TestClass1();
		TestClass1 source = new TestClass1(null, "test", 123);

		TestClass2 owned1 = new TestClass2();
		TestClass2 owned2 = new TestClass2();
		source.addOwned(owned1);
		source.addOwned(owned2);

		ObjectUtils.copy(source, target, null);

		Assert.assertThat(target.getOwned(), contains(owned1, owned2));
		Assert.assertThat(target.getOwned().get(0).getOwner(), is(target));
		Assert.assertThat(target.getOwned().get(1).getOwner(), is(target));
	}

	/**
	 * @see ObjectUtils#copy(org.openmrs.OpenmrsObject, org.openmrs.OpenmrsObject, java.util.Set)
	 */
	@Test
	public void copy_shouldIgnoreExcludedFields() {
		TestClass1 target = new TestClass1(1, "abc", 123.0);
		TestClass1 source = new TestClass1(2, "xyz", 234.0);

		ObjectUtils.copy(source, target, Collections.singleton("stringValue"));

		Assert.assertThat(target.getId(), is(2));
		Assert.assertThat(target.getStringValue(), is("abc"));
		Assert.assertThat(target.getDoubleValue(), is(234.0));
	}

	/**
	 * @see ObjectUtils#copy(org.openmrs.OpenmrsObject, org.openmrs.OpenmrsObject, java.util.Set)
	 */
	@Test
	public void copy_shouldNotReplaceNonNullCollectionsInTarget() {
		TestClass1 target = new TestClass1();
		TestClass1 source = new TestClass1(null, "test", 123);

		TestClass2 owned1 = new TestClass2();
		TestClass2 owned2 = new TestClass2();

		target.addOwned(owned1);
		source.addOwned(owned2);

		Collection<TestClass2> preCopyOwned = target.getOwned();

		ObjectUtils.copy(source, target, null);

		Assert.assertThat(target.getOwned(), sameInstance(preCopyOwned));
		Assert.assertThat(target.getOwned(), contains(owned2));
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
		private List<TestClass2> owned = new ArrayList<TestClass2>();

		public TestClass1() {
		}

		public TestClass1(Integer id, String stringValue, double doubleValue) {
			this.id = id;
			this.stringValue = stringValue;
			this.doubleValue = doubleValue;
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

		public void addOwned(TestClass2 class2) {
			class2.setOwner(this);
			owned.add(class2);
		}

		public List<TestClass2> getOwned() {
			return owned;
		}
	}

	public static class TestClass2 {

		private TestClass1 owner;

		public TestClass1 getOwner() {
			return owner;
		}

		public void setOwner(TestClass1 owner) {
			this.owner = owner;
		}
	}
}