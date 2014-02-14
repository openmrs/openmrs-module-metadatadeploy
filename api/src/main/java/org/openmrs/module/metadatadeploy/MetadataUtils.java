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

import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.Drug;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationAttributeType;
import org.openmrs.OpenmrsObject;
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
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;

/**
 * Utility methods for fail-fast fetching of metadata
 */
public class MetadataUtils {

	/**
	 * Gets the specified concept (by mapping or UUID)
	 * @param identifier the mapping or UUID
	 * @return the concept
	 * @throws MissingMetadataException if no such concept exists
	 */
	public static Concept getConcept(String identifier) {
		Concept concept;

		if (identifier.contains(":")) {
			String[] tokens = identifier.split(":");
			concept = Context.getConceptService().getConceptByMapping(tokens[1].trim(), tokens[0].trim());
		}
		else {
			// Assume it's a UUID
			concept = Context.getConceptService().getConceptByUuid(identifier);
		}

		if (concept == null) {
			throw new MissingMetadataException(Concept.class, identifier);
		}

		// getConcept doesn't always return ConceptNumeric for numeric concepts
		if (concept.getDatatype().isNumeric() && !(concept instanceof ConceptNumeric)) {
			concept = Context.getConceptService().getConceptNumeric(concept.getId());

			if (concept == null) {
				throw new MissingMetadataException(ConceptNumeric.class, identifier);
			}
		}

		return concept;
	}

	/**
	 * Gets the specified drug
	 * @param uuid the uuid
	 * @return the drug
	 * @throws MissingMetadataException if no such drug exists
	 */
	public static Drug getDrug(String uuid) {
		Drug ret = Context.getConceptService().getDrugByUuid(uuid);
		if (ret == null) {
			throw new MissingMetadataException(Drug.class, uuid);
		}
		return ret;
	}

	/**
	 * Gets the specified encounter type
	 * @param uuid the uuid
	 * @return the encounter type
	 * @throws MissingMetadataException if no such encounter type exists
	 */
	public static EncounterType getEncounterType(String uuid) {
		return fetchExisting(EncounterType.class, uuid);
	}

	/**
	 * Gets the specified form
	 * @param uuid the uuid
	 * @return the form
	 * @throws MissingMetadataException if no such form exists
	 */
	public static Form getForm(String uuid) {
		return fetchExisting(Form.class, uuid);
	}

	/**
	 * Gets the specified location
	 * @param uuid the identifier
	 * @return the location
	 * @throws MissingMetadataException if no such location exists
	 */
	public static Location getLocation(String uuid) {
		return fetchExisting(Location.class, uuid);
	}

	/**
	 * Gets the specified location attribute type
	 * @param uuid the uuid
	 * @return the location attribute type
	 * @throws MissingMetadataException if no such location attribute type exists
	 */
	public static LocationAttributeType getLocationAttributeType(String uuid) {
		return fetchExisting(LocationAttributeType.class, uuid);
	}

	/**
	 * Gets the specified patient identifier type
	 * @param uuid the uuid
	 * @return the patient identifier type
	 * @throws MissingMetadataException if no such patient identifier type exists
	 */
	public static PatientIdentifierType getPatientIdentifierType(String uuid) {
		return fetchExisting(PatientIdentifierType.class, uuid);
	}

	/**
	 * Gets the specified person attribute type
	 * @param uuid the uuid
	 * @return the person attribute type
	 * @throws MissingMetadataException if no such person attribute type exists
	 */
	public static PersonAttributeType getPersonAttributeType(String uuid) {
		return fetchExisting(PersonAttributeType.class, uuid);
	}

	/**
	 * Gets the specified privilege
	 * @param identifier the name or uuid
	 * @return the privilege
	 * @throws MissingMetadataException if no such privilege exists
	 */
	public static Privilege getPrivilege(String identifier) {
		Privilege ret = null;

		if (isValidUuid(identifier)) {
			ret = Context.getUserService().getPrivilegeByUuid(identifier);
		}
		if (ret == null) {
			ret = Context.getUserService().getPrivilege(identifier);
		}
		if (ret == null) {
			throw new MissingMetadataException(Privilege.class, identifier);
		}
		return ret;
	}

	/**
	 * Gets the specified program
	 * @param uuid the uuid
	 * @return the program
	 * @throws MissingMetadataException if no such program exists
	 */
	public static Program getProgram(String uuid) {
		return fetchExisting(Program.class, uuid);
	}

	/**
	 * Gets the specified provider attribute type
	 * @param uuid the uuid
	 * @return the visit attribute type
	 * @throws MissingMetadataException if no such visit attribute type exists
	 */
	public static ProviderAttributeType getProviderAttributeType(String uuid) {
		return fetchExisting(ProviderAttributeType.class, uuid);
	}

	/**
	 * Gets the specified relationship type
	 * @param uuid the uuid
	 * @return the relationship type
	 * @throws MissingMetadataException if no such relationship type exists
	 */
	public static RelationshipType getRelationshipType(String uuid) {
		return fetchExisting(RelationshipType.class, uuid);
	}

	/**
	 * Gets the specified role
	 * @param identifier the name or uuid
	 * @return the role
	 * @throws MissingMetadataException if no such role exists
	 */
	public static Role getRole(String identifier) {
		Role ret = null;

		if (isValidUuid(identifier)) {
			ret = Context.getUserService().getRoleByUuid(identifier);
		}
		if (ret == null) {
			ret = Context.getUserService().getRole(identifier);
		}
		if (ret == null) {
			throw new MissingMetadataException(Role.class, identifier);
		}
		return ret;
	}

	/**
	 * Gets the specified visit attribute type
	 * @param uuid the uuid
	 * @return the visit attribute type
	 * @throws MissingMetadataException if no such visit attribute type exists
	 */
	public static VisitAttributeType getVisitAttributeType(String uuid) {
		return fetchExisting(VisitAttributeType.class, uuid);
	}

	/**
	 * Gets the specified visit type
	 * @param uuid the uuid
	 * @return the visit type
	 * @throws MissingMetadataException if no such visit type exists
	 */
	public static VisitType getVisitType(String uuid) {
		return fetchExisting(VisitType.class, uuid);
	}

	/**
	 * Fetches an object which is assumed to exist
	 * @param clazz the object class
	 * @param identifier the object identifier
	 * @return the object
	 * @throws org.openmrs.module.metadatadeploy.MissingMetadataException if object doesn't exist
	 */
	protected static <T extends OpenmrsObject> T fetchExisting(Class<T> clazz, String identifier) {
		T ret = Context.getService(MetadataDeployService.class).fetchObject(clazz, identifier);
		if (ret == null) {
			throw new MissingMetadataException(VisitType.class, identifier);
		}
		return ret;
	}

	/**
	 * Determines if the passed string is in valid UUID format By OpenMRS standards, a UUID must be
	 * 36 characters in length and not contain whitespace, but we do not enforce that a uuid be in
	 * the "canonical" form, with alphanumerics separated by dashes, since the MVP dictionary does
	 * not use this format.
	 */
	protected static boolean isValidUuid(String uuid) {
		return uuid != null && uuid.length() == 36 && !uuid.contains(" ");
	}
}