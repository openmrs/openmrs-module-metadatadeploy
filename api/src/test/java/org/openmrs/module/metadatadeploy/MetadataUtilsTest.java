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
import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.Drug;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationAttributeType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.Privilege;
import org.openmrs.Program;
import org.openmrs.ProviderAttributeType;
import org.openmrs.RelationshipType;
import org.openmrs.Role;
import org.openmrs.VisitAttributeType;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link MetadataUtils}
 */
public class MetadataUtilsTest extends BaseModuleContextSensitiveTest {

	private static final String NONEXISTENT_UUID = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"; // Valid syntactically

	@Test
	public void integration() {
		new MetadataUtils();
	}

	/**
	 * @see MetadataUtils#existing(Class, String)
	 */
	@Test
	public void existing_shouldFetchExisting() {
		VisitType initial = Context.getVisitService().getVisitTypeByUuid("c0c579b0-8e59-401d-8a4a-976a0b183519");
		Assert.assertThat(MetadataUtils.existing(VisitType.class, "c0c579b0-8e59-401d-8a4a-976a0b183519"), is(initial));
	}

	/**
	 * @see MetadataUtils#existing(Class, String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void existing_shouldThrowExceptionForNonExistent() {
		MetadataUtils.existing(VisitType.class, NONEXISTENT_UUID);
	}

	/**
	 * @see MetadataUtils#possible(Class, String)
	 */
	@Test
	public void possible_shouldFetchExisting() {
		VisitType initial = Context.getVisitService().getVisitTypeByUuid("c0c579b0-8e59-401d-8a4a-976a0b183519");
		Assert.assertThat(MetadataUtils.possible(VisitType.class, "c0c579b0-8e59-401d-8a4a-976a0b183519"), is(initial));
	}

	/**
	 * @see MetadataUtils#existing(Class, String)
	 */
	@Test
	public void possible_shouldReturnNullForNonExistent() {
		Assert.assertThat(MetadataUtils.possible(VisitType.class, NONEXISTENT_UUID), nullValue());
	}

	/**
	 * @see MetadataUtils#getConcept(String)
	 */
	@Test
	public void getConcept_shouldFetchByUuid() {
		// Test non-numeric concept
		Concept yes = Context.getConceptService().getConcept(7);
		Assert.assertThat(MetadataUtils.getConcept("b055abd8-a420-4a11-8b98-02ee170a7b54"), is(yes));

		// Test numeric concept
		Concept cd4 = Context.getConceptService().getConcept(5497);
		Concept fetched = MetadataUtils.getConcept("a09ab2c5-878e-4905-b25d-5784167d0216");
		Assert.assertThat(fetched, is(cd4));
		Assert.assertThat(fetched, is(instanceOf(ConceptNumeric.class)));
	}

	/**
	 * @see MetadataUtils#getConcept(String)
	 */
	@Test
	public void getConcept_shouldFetchByMapping() {
		Concept cd4 = Context.getConceptService().getConcept(5497);
		Assert.assertThat(MetadataUtils.getConcept("SSTRM:CD41003"), is(cd4));
	}

	/**
	 * @see MetadataUtils#getConcept(String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void getConcept_shouldThrowExceptionForNonExistentMapping() {
		MetadataUtils.getConcept("SSTRM:XXXXXX");
	}

	/**
	 * @see MetadataUtils#getConcept(String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void getConcept_shouldThrowExceptionForNonExistentUuid() {
		MetadataUtils.getConcept(NONEXISTENT_UUID);
	}

	/**
	 * @see MetadataUtils#getConcept(String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void getConcept_shouldThrowExceptionForNumericConceptWithNoConceptNumeric() {
		MetadataUtils.getConcept("11716f9c-1434-4f8d-b9fc-9aa14c4d6129");
	}

	/**
	 * @see MetadataUtils#getDrug(String)
	 */
	@Test
	public void getDrug_shouldFetchByUuid() {
		Drug aspirin = Context.getConceptService().getDrug(3);
		Assert.assertThat(MetadataUtils.getDrug("05ec820a-d297-44e3-be6e-698531d9dd3f"), is(aspirin));
	}

	/**
	 * @see MetadataUtils#getDrug(String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void getDrug_shouldThrowExceptionForNonExistentUuid() {
		MetadataUtils.getDrug(NONEXISTENT_UUID);
	}

	/**
	 * @see MetadataUtils#getEncounterType(String)
	 */
	@Test
	public void getEncounterType_shouldFetchByUuid() {
		EncounterType emergency = Context.getEncounterService().getEncounterType(2);
		Assert.assertThat(MetadataUtils.getEncounterType("07000be2-26b6-4cce-8b40-866d8435b613"), is(emergency));
	}

	/**
	 * @see MetadataUtils#getEncounterType(String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void getEncounterType_shouldThrowExceptionForNonExistent() {
		MetadataUtils.getEncounterType(NONEXISTENT_UUID);
	}

	/**
	 * @see MetadataUtils#getForm(String)
	 */
	@Test
	public void getForm_shouldFetchByUuid() {
		Form basic = Context.getFormService().getForm(1);
		Assert.assertThat(MetadataUtils.getForm("d9218f76-6c39-45f4-8efa-4c5c6c199f50"), is(basic));
	}

	/**
	 * @see MetadataUtils#getForm(String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void getForm_shouldThrowExceptionForNonExistent() {
		MetadataUtils.getForm(NONEXISTENT_UUID);
	}

	/**
	 * @see MetadataUtils#getLocation(String)
	 */
	@Test
	public void getLocation_shouldFetchByUuid() {
		Location unknown = Context.getLocationService().getLocation(1);
		Assert.assertThat(MetadataUtils.getLocation("8d6c993e-c2cc-11de-8d13-0010c6dffd0f"), is(unknown));
	}

	/**
	 * @see MetadataUtils#getLocation(String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void getLocation_shouldThrowExceptionForNonExistent() {
		MetadataUtils.getLocation(NONEXISTENT_UUID);
	}

	/**
	 * @see MetadataUtils#getLocationAttributeType(String)
	 */
	@Test
	public void getLocationAttributeType_shouldFetchByUuid() {
		// No location attribute type in the standard test data so make one..
		LocationAttributeType phoneAttrType = new LocationAttributeType();
		phoneAttrType.setName("Facility Phone");
		phoneAttrType.setMinOccurs(0);
		phoneAttrType.setMaxOccurs(1);
		phoneAttrType.setDatatypeClassname("org.openmrs.customdatatype.datatype.FreeTextDatatype");
		Context.getLocationService().saveLocationAttributeType(phoneAttrType);
		String savedUuid = phoneAttrType.getUuid();

		Assert.assertThat(MetadataUtils.getLocationAttributeType(savedUuid), is(phoneAttrType));
	}

	/**
	 * @see MetadataUtils#getLocationAttributeType(String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void getLocationAttributeType_shouldThrowExceptionForNonExistent() {
		MetadataUtils.getLocationAttributeType(NONEXISTENT_UUID);
	}

	/**
	 * @see MetadataUtils#getPatientIdentifierType(String)
	 */
	@Test
	public void getPatientIdentifierType_shouldFetchByUuid() {
		PatientIdentifierType openmrs = Context.getPatientService().getPatientIdentifierType(1);
		Assert.assertThat(MetadataUtils.getPatientIdentifierType("1a339fe9-38bc-4ab3-b180-320988c0b968"), is(openmrs));
	}

	/**
	 * @see MetadataUtils#getPatientIdentifierType(String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void getPatientIdentifierType_shouldThrowExceptionForNonExistent() {
		MetadataUtils.getPatientIdentifierType(NONEXISTENT_UUID);
	}

	/**
	 * @see MetadataUtils#getPersonAttributeType(String)
	 */
	@Test
	public void getPersonAttributeType_shouldFetchByUuid() {
		PersonAttributeType civil = Context.getPersonService().getPersonAttributeType(8);
		Assert.assertThat(MetadataUtils.getPersonAttributeType("a0f5521c-dbbd-4c10-81b2-1b7ab18330df"), is(civil));
	}

	/**
	 * @see MetadataUtils#getPersonAttributeType(String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void getPersonAttributeType_shouldThrowExceptionForNonExistent() {
		MetadataUtils.getPersonAttributeType(NONEXISTENT_UUID);
	}

	/**
	 * @see MetadataUtils#getPrivilege(String)
	 */
	@Test
	public void getPrivilege_shouldFetchByPrivilegeOrUuid() {
		final String VIEW_TESTS = "51569FBC-2D55-4E83-8800-104652D17CD5";
		Privilege viewTests = new Privilege("View Tests", "desc");
		viewTests.setUuid(VIEW_TESTS);
		Context.getUserService().savePrivilege(viewTests);

		Assert.assertThat(MetadataUtils.getPrivilege("View Tests"), is(viewTests));
		Assert.assertThat(MetadataUtils.getPrivilege(VIEW_TESTS), is(viewTests));
	}

	/**
	 * @see MetadataUtils#getPrivilege(String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void getPrivilege_shouldThrowExceptionForNonExistent() {
		MetadataUtils.getPrivilege(NONEXISTENT_UUID);
	}

	/**
	 * @see MetadataUtils#getProgram(String)
	 */
	@Test
	public void getProgram_shouldFetchByUuid() {
		Program hiv = Context.getProgramWorkflowService().getProgram(1);
		Assert.assertThat(MetadataUtils.getProgram("da4a0391-ba62-4fad-ad66-1e3722d16380"), is(hiv));
	}

	/**
	 * @see MetadataUtils#getProgram(String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void getProgram_shouldThrowExceptionForNonExistent() {
		MetadataUtils.getProgram(NONEXISTENT_UUID);
	}

	/**
	 * @see MetadataUtils#getProviderAttributeType(String)
	 */
	@Test
	public void getProviderAttributeType_shouldFetchByUuid() {
		// No provider attribute type in the standard test data so make one..
		ProviderAttributeType phoneAttrType = new ProviderAttributeType();
		phoneAttrType.setName("Provider Phone");
		phoneAttrType.setMinOccurs(0);
		phoneAttrType.setMaxOccurs(1);
		phoneAttrType.setDatatypeClassname("org.openmrs.customdatatype.datatype.FreeTextDatatype");
		Context.getProviderService().saveProviderAttributeType(phoneAttrType);
		String savedUuid = phoneAttrType.getUuid();

		Assert.assertThat(MetadataUtils.getProviderAttributeType(savedUuid), is(phoneAttrType));
	}

	/**
	 * @see MetadataUtils#getProviderAttributeType(String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void getProviderAttributeType_shouldThrowExceptionForNonExistent() {
		MetadataUtils.getProviderAttributeType(NONEXISTENT_UUID);
	}

	/**
	 * @see MetadataUtils#getRelationshipType(String)
	 */
	@Test
	public void getRelationshipType_shouldFetchByUuid() {
		RelationshipType patdoc = Context.getPersonService().getRelationshipType(1);
		Assert.assertThat(MetadataUtils.getRelationshipType("6d9002ea-a96b-4889-af78-82d48c57a110"), is(patdoc));
	}

	/**
	 * @see MetadataUtils#getRelationshipType(String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void getRelationshipType_shouldThrowExceptionForNonExistent() {
		MetadataUtils.getRelationshipType(NONEXISTENT_UUID);
	}

	/**
	 * @see MetadataUtils#getRole(String)
	 */
	@Test
	public void getRole_shouldFetchByNameOrUuid() {
		Role provider = Context.getUserService().getRole("Provider");
		Assert.assertThat(MetadataUtils.getRole("Provider"), is(provider));
		Assert.assertThat(MetadataUtils.getRole("3480cb6d-c291-46c8-8d3a-96dc33d199fb"), is(provider));
	}

	/**
	 * @see MetadataUtils#getRole(String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void getRole_shouldThrowExceptionForNonExistent() {
		MetadataUtils.getRole(NONEXISTENT_UUID);
	}

	/**
	 * @see MetadataUtils#getVisitAttributeType(String)
	 */
	@Test
	public void getVisitAttributeType_shouldFetchByUuid() {
		VisitAttributeType auditDate = Context.getVisitService().getVisitAttributeType(1);
		Assert.assertThat(MetadataUtils.getVisitAttributeType("8770f6d6-7673-11e0-8f03-001e378eb67e"), is(auditDate));
	}

	/**
	 * @see MetadataUtils#getVisitAttributeType(String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void getVisitAttributeType_shouldThrowExceptionForNonExistent() {
		MetadataUtils.getVisitAttributeType(NONEXISTENT_UUID);
	}

	/**
	 * @see MetadataUtils#getVisitType(String)
	 */
	@Test
	public void getVisitType_shouldFetchByUuid() {
		Assert.assertNotNull(MetadataUtils.getVisitType("c0c579b0-8e59-401d-8a4a-976a0b183519")); // Initial
	}

	/**
	 * @see MetadataUtils#getVisitType(String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void getVisitType_shouldThrowExceptionForNonExistent() {
		MetadataUtils.getVisitType(NONEXISTENT_UUID);
	}

	/**
	 * @see MetadataUtils#isValidUuid(String)
	 */
	@Test
	public void isValidUuid_shouldCheckForValidUuids() {
		Assert.assertThat(MetadataUtils.isValidUuid(null), is(false));
		Assert.assertThat(MetadataUtils.isValidUuid(""), is(false));
		Assert.assertThat(MetadataUtils.isValidUuid("xxxx-xxxxx"), is(false));

		Assert.assertThat(MetadataUtils.isValidUuid(NONEXISTENT_UUID), is(true));
		Assert.assertThat(MetadataUtils.isValidUuid("c0c579b0-8e59-401d-8a4a-976a0b183519"), is(true));
	}
}