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
 * Utility methods for fetching of metadata outside of a bundle.
 *
 * Note: Having specific methods for each class isn't scalable so these have been deprecated in favour of just
 * existing(...) and possible(...). We should also stop implementing fetch logic which is different to that provided by
 * the deploy handlers.
 */
public class MetadataUtils {

	/**
	 * Fetches an object which is assumed to exist
	 * @param clazz the object class
	 * @param identifier the object identifier
	 * @return the object
	 * @throws org.openmrs.module.metadatadeploy.MissingMetadataException if object doesn't exist
	 */
	public static <T extends OpenmrsObject> T existing(Class<T> clazz, String identifier) {
		T ret = Context.getService(MetadataDeployService.class).fetchObject(clazz, identifier);
		if (ret == null) {
			throw new MissingMetadataException(clazz, identifier);
		}
		return ret;
	}

	/**
	 * Fetches an object which may or may not exist
	 * @param clazz the object class
	 * @param identifier the object identifier
	 * @return the object or null
	 */
	public static <T extends OpenmrsObject> T possible(Class<T> clazz, String identifier) {
		return Context.getService(MetadataDeployService.class).fetchObject(clazz, identifier);
	}

	/**
	 * Gets the specified concept (by mapping or UUID)
	 * @param identifier the mapping or UUID
	 * @return the concept
	 * @throws MissingMetadataException if no such concept exists
	 */
	@Deprecated
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
	@Deprecated
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
	@Deprecated
	public static EncounterType getEncounterType(String uuid) {
		return existing(EncounterType.class, uuid);
	}

	/**
	 * Gets the specified form
	 * @param uuid the uuid
	 * @return the form
	 * @throws MissingMetadataException if no such form exists
	 */
	@Deprecated
	public static Form getForm(String uuid) {
		return existing(Form.class, uuid);
	}

	/**
	 * Gets the specified location
	 * @param uuid the identifier
	 * @return the location
	 * @throws MissingMetadataException if no such location exists
	 */
	@Deprecated
	public static Location getLocation(String uuid) {
		return existing(Location.class, uuid);
	}

	/**
	 * Gets the specified location attribute type
	 * @param uuid the uuid
	 * @return the location attribute type
	 * @throws MissingMetadataException if no such location attribute type exists
	 */
	@Deprecated
	public static LocationAttributeType getLocationAttributeType(String uuid) {
		return existing(LocationAttributeType.class, uuid);
	}

	/**
	 * Gets the specified patient identifier type
	 * @param uuid the uuid
	 * @return the patient identifier type
	 * @throws MissingMetadataException if no such patient identifier type exists
	 */
	@Deprecated
	public static PatientIdentifierType getPatientIdentifierType(String uuid) {
		return existing(PatientIdentifierType.class, uuid);
	}

	/**
	 * Gets the specified person attribute type
	 * @param uuid the uuid
	 * @return the person attribute type
	 * @throws MissingMetadataException if no such person attribute type exists
	 */
	@Deprecated
	public static PersonAttributeType getPersonAttributeType(String uuid) {
		return existing(PersonAttributeType.class, uuid);
	}

	/**
	 * Gets the specified privilege
	 * @param identifier the name or uuid
	 * @return the privilege
	 * @throws MissingMetadataException if no such privilege exists
	 */
	@Deprecated
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
	@Deprecated
	public static Program getProgram(String uuid) {
		return existing(Program.class, uuid);
	}

	/**
	 * Gets the specified provider attribute type
	 * @param uuid the uuid
	 * @return the visit attribute type
	 * @throws MissingMetadataException if no such visit attribute type exists
	 */
	@Deprecated
	public static ProviderAttributeType getProviderAttributeType(String uuid) {
		return existing(ProviderAttributeType.class, uuid);
	}

	/**
	 * Gets the specified relationship type
	 * @param uuid the uuid
	 * @return the relationship type
	 * @throws MissingMetadataException if no such relationship type exists
	 */
	@Deprecated
	public static RelationshipType getRelationshipType(String uuid) {
		return existing(RelationshipType.class, uuid);
	}

	/**
	 * Gets the specified role
	 * @param identifier the name or uuid
	 * @return the role
	 * @throws MissingMetadataException if no such role exists
	 */
	@Deprecated
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
	@Deprecated
	public static VisitAttributeType getVisitAttributeType(String uuid) {
		return existing(VisitAttributeType.class, uuid);
	}

	/**
	 * Gets the specified visit type
	 * @param uuid the uuid
	 * @return the visit type
	 * @throws MissingMetadataException if no such visit type exists
	 */
	@Deprecated
	public static VisitType getVisitType(String uuid) {
		return existing(VisitType.class, uuid);
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