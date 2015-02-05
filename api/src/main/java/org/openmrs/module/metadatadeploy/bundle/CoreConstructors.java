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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.openmrs.Concept;
import org.openmrs.ConceptNameTag;
import org.openmrs.ConceptSource;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.LocationTag;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.Privilege;
import org.openmrs.Program;
import org.openmrs.ProviderAttributeType;
import org.openmrs.RelationshipType;
import org.openmrs.Role;
import org.openmrs.VisitAttributeType;
import org.openmrs.VisitType;
import org.openmrs.customdatatype.CustomDatatype;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatasharing.ImportMode;
import org.openmrs.patient.IdentifierValidator;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Constructors for different core metadata classes
 */
public class CoreConstructors {

	/**
	 * Constructs a concept source
	 * @param name the name
	 * @param description the description
	 * @param hl7Code the HL7 code
	 * @param uuid the UUID
	 * @return the transient object
	 */
	public static ConceptSource conceptSource(String name, String description, String hl7Code, String uuid) {
		ConceptSource obj = new ConceptSource();
		obj.setName(name);
		obj.setDescription(description);
		obj.setHl7Code(hl7Code);
		obj.setUuid(uuid);
		return obj;
	}

	/**
	 * Constructs a ConceptNameTag
	 * @param tag
	 * @param description
	 * @param uuid
	 * @return the transient object
	 */
	public static ConceptNameTag conceptNameTag(String tag, String description, String uuid) {
		ConceptNameTag obj = new ConceptNameTag();
		obj.setTag(tag);
		obj.setDescription(description);
		obj.setUuid(uuid);
		return obj;
	}

	/**
	 * Constructs an encounter role
	 * @param name the name
	 * @param description the description
	 * @param uuid the UUID
	 * @return the transient object
	 */
	public static EncounterRole encounterRole(String name, String description, String uuid) {
		EncounterRole obj = new EncounterRole();
		obj.setName(name);
		obj.setDescription(description);
		obj.setUuid(uuid);
		return obj;
	}

	/**
	 * Constructs an encounter type
	 * @param name the name
	 * @param description the description
	 * @param uuid the UUID
	 * @return the transient object
	 */
	public static EncounterType encounterType(String name, String description, String uuid) {
		EncounterType obj = new EncounterType();
		obj.setName(name);
		obj.setDescription(description);
		obj.setUuid(uuid);
		return obj;
	}

	/**
	 * Constructs a form
	 * @param name the name
	 * @param description the description
	 * @param encTypeUuid the encounter type UUID
	 * @param uuid the UUID
	 * @return the transient object
	 */
	public static Form form(String name, String description, String encTypeUuid, String version, String uuid) {
		Form obj = new Form();
		obj.setName(name);
		obj.setDescription(description);
		obj.setEncounterType(MetadataUtils.existing(EncounterType.class, encTypeUuid));
		obj.setVersion(version);
		obj.setUuid(uuid);
		return obj;
	}

	/**
	 * Constructs a form resource without an explicitly defined UUID
	 * @param name the name
	 * @param formUuid the form UUID
	 * @param datatype the custom data type (can be null)
	 * @param datatypeConfig the data type config (can be null)
	 * @param value the value
	 * @return the transient object
	 */
	public static <T, H extends CustomDatatype<T>> FormResource formResource(String name, String formUuid, Class<H> datatype, String datatypeConfig, T value) {
		FormResource obj = new FormResource();
		obj.setName(name);
		obj.setForm(MetadataUtils.existing(Form.class, formUuid));
		obj.setDatatypeClassname(datatype.getName());
		obj.setDatatypeConfig(datatypeConfig);
		obj.setValue(value);
		return obj;
	}

	/**
	 * Constructs a global property
	 * @param property the property
	 * @param description the description
	 * @param value the value (can be null)
	 * @return the transient object
	 */
	public static GlobalProperty globalProperty(String property, String description, String value) {
		return globalProperty(property, description, FreeTextDatatype.class, null, value);
	}

	/**
	 * Constructs a global property
	 * @param property the property
	 * @param description the description
	 * @param datatype the custom data type
	 * @param datatypeConfig the data type config (can be null)
	 * @param value the value (can be null)
	 * @return the transient object
	 */
	public static <T, H extends CustomDatatype<T>> GlobalProperty globalProperty(String property,
																		  String description,
																		  Class<H> datatype,
																		  String datatypeConfig,
																		  T value) {
		GlobalProperty obj = new GlobalProperty();
		obj.setProperty(property);
		obj.setDescription(description);
		obj.setDatatypeClassname(datatype.getName());
		obj.setDatatypeConfig(datatypeConfig);

		// Global properties can't have null values
		if (value != null) {
			obj.setValue(value);
		}

		return obj;
	}

	/**
	 * Constructs a location (with the minimum required fields)
	 * @param name the name
	 * @param description the description
	 * @param uuid the UUID
	 * @return the transient object
	 */
	public static Location location(String name, String description, String uuid) {
		Location obj = new Location();
		obj.setName(name);
		obj.setDescription(description);
		obj.setUuid(uuid);
		return obj;
	}

    /**
     * Constructs a location (with more fields)
     * @param name
     * @param description
     * @param uuid
     * @param parentLocationUuid
     * @param tagUuids
     * @return the transient object
     */
    public static Location location(String name, String description, String uuid, String parentLocationUuid, Collection<String> tagUuids) {
        Location obj = new Location();
        obj.setName(name);
        obj.setDescription(description);
        obj.setUuid(uuid);
        if (parentLocationUuid != null) {
            obj.setParentLocation(MetadataUtils.existing(Location.class, parentLocationUuid));
        }
        if (tagUuids != null) {
            for (String tagUuid : tagUuids) {
                obj.addTag(MetadataUtils.existing(LocationTag.class, tagUuid));
            }
        }
        return obj;
    }

	/**
	 * Constructs a location attribute type
	 * @param name the name
	 * @param description the description
	 * @param datatype the datatype class
	 * @param datatypeConfig the data type config (can be null)
	 * @param minOccurs the minimum allowed occurrences
	 * @param maxOccurs the maximum allowed occurrences
	 * @param uuid the UUID
	 * @return the transient object
	 */
	public static LocationAttributeType locationAttributeType(String name, String description, Class<?> datatype, String datatypeConfig, int minOccurs, int maxOccurs, String uuid) {
		LocationAttributeType obj = new LocationAttributeType();
		obj.setName(name);
		obj.setDescription(description);
		obj.setDatatypeClassname(datatype.getName());
		obj.setDatatypeConfig(datatypeConfig);
		obj.setMinOccurs(minOccurs);
		obj.setMaxOccurs(maxOccurs);
		obj.setUuid(uuid);
		return obj;
	}

    /**
     * Constructs a location attribute
     */
     public static LocationAttribute locationAttribute(String locationUuid, String locationAttributeType, Object value, String uuid) {
         LocationAttribute obj = new LocationAttribute();
         obj.setLocation(MetadataUtils.existing(Location.class, locationUuid));
         obj.setAttributeType(MetadataUtils.existing(LocationAttributeType.class, locationAttributeType));
         obj.setValue(value);
         obj.setUuid(uuid);
         return obj;
     }


	/**
	 * Constructs a location tag
	 * @param name the name
	 * @param description the description
	 * @param uuid the UUID
	 * @return the transient object
	 */
	public static LocationTag locationTag(String name, String description, String uuid) {
		LocationTag obj = new LocationTag();
		obj.setName(name);
		obj.setDescription(description);
		obj.setUuid(uuid);
		return obj;
	}

	/**
	 * Constructs a package descriptor
	 * @param filename the name
	 * @param classLoader the description
	 * @param groupUuid the group UUID
	 * @return the package descriptor
	 */
	public static PackageDescriptor packageFile(String filename, ClassLoader classLoader, String groupUuid) {
		return new PackageDescriptor(filename, classLoader, groupUuid);
	}

    /**
     * Constructs a package descriptor
     * @param filename the name
     * @param classLoader the description
     * @param groupUuid the group UUID
     * @param importMode the import mode to use
     * @return the package descriptor
     */
    public static PackageDescriptor packageFile(String filename, ClassLoader classLoader, String groupUuid, ImportMode importMode) {
        return new PackageDescriptor(filename, classLoader, groupUuid, importMode);
    }

	/**
	 * Constructs a patient identifier type
	 * @param name the name
	 * @param description the description
	 * @param format the format regex
	 * @param uuid the UUID
	 * @return the transient object
	 */
	public static PatientIdentifierType patientIdentifierType(String name,
													   String description,
													   String format,
													   String formatDescription,
													   Class<? extends IdentifierValidator> validator,
													   PatientIdentifierType.LocationBehavior locationBehavior,
													   boolean required,
													   String uuid) {

		PatientIdentifierType obj = new PatientIdentifierType();
		obj.setName(name);
		obj.setDescription(description);
		obj.setFormat(format);
		obj.setFormatDescription(formatDescription);
		obj.setValidator(validator != null ? validator.getName() : null);
		obj.setLocationBehavior(locationBehavior);
		obj.setRequired(required);
		obj.setUuid(uuid);
		return obj;
	}

	/**
	 * Constructs a person attribute type
	 * @param name the name
	 * @param description the description
	 * @param format the format class
	 * @param foreignKey the foreign key (can be null)
	 * @param searchable whether attribute is searchable
	 * @param sortWeight the sort weight
	 * @param uuid the UUID
	 * @return the transient object
	 */
	public static PersonAttributeType personAttributeType(String name,
												   String description,
												   Class<?> format,
												   Integer foreignKey,
												   boolean searchable,
												   double sortWeight,
												   String uuid) {

		PersonAttributeType obj = new PersonAttributeType();
		obj.setName(name);
		obj.setDescription(description);
		obj.setFormat(format.getName());
		obj.setForeignKey(foreignKey);
		obj.setSearchable(searchable);
		obj.setSortWeight(sortWeight);
		obj.setUuid(uuid);
		return obj;
	}

	/**
	 * Constructs a privilege
	 * @param privilege the privilege
	 * @param description the description
	 * @return the transient object
	 */
	public static Privilege privilege(String privilege, String description) {
		Privilege obj = new Privilege();
		obj.setPrivilege(privilege);
		obj.setDescription(description);
		return obj;
	}

	/**
	 * Constructs a program
	 * @param name the name
	 * @param description the description
	 * @param conceptUuid the concept UUID
	 * @param uuid the UUID
	 * @return the transient object
	 */
	public static Program program(String name, String description, String conceptUuid, String uuid) {
		Program obj = new Program();
		obj.setName(name);
		obj.setDescription(description);
		obj.setConcept(MetadataUtils.existing(Concept.class, conceptUuid));
		obj.setUuid(uuid);
		return obj;
	}

	/**
	 * Constructs a provider attribute type
	 * @param name the name
	 * @param description the description
	 * @param datatype the datatype class
	 * @param datatypeConfig the data type config (can be null)
	 * @param minOccurs the minimum allowed occurrences
	 * @param maxOccurs the maximum allowed occurrences
	 * @param uuid the UUID
	 * @return the transient object
	 */
	public static ProviderAttributeType providerAttributeType(String name, String description, Class<?> datatype, String datatypeConfig, int minOccurs, int maxOccurs, String uuid) {
		ProviderAttributeType obj = new ProviderAttributeType();
		obj.setName(name);
		obj.setDescription(description);
		obj.setDatatypeClassname(datatype.getName());
		obj.setDatatypeConfig(datatypeConfig);
		obj.setMinOccurs(minOccurs);
		obj.setMaxOccurs(maxOccurs);
		obj.setUuid(uuid);
		return obj;
	}

	/**
	 * Constructs a role
	 * @param role the role
	 * @param description the description
	 * @param inherited the inherited roles
	 * @param privileges the privileges
	 * @return the transient object
	 */
	public static Role role(String role, String description, Set<String> inherited, Set<String> privileges) {
		Role obj = new Role();
		obj.setRole(role);
		obj.setDescription(description);

		if (CollectionUtils.isNotEmpty(inherited)) {
			obj.setInheritedRoles((Set) CollectionUtils.collect(inherited, new Transformer() {
				@Override
				public Object transform(Object o) {
					return MetadataUtils.existing(Role.class, (String) o);
				}
			}, new HashSet()));
		}
		if (CollectionUtils.isNotEmpty(privileges)) {
			obj.setPrivileges((Set) CollectionUtils.collect(privileges, new Transformer() {
				@Override
				public Object transform(Object o) {
					return MetadataUtils.existing(Privilege.class, (String) o);
				}
			}, new HashSet()));
		}

		return obj;
	}

	/**
	 * Constructs a visit attribute type
	 * @param name the name
	 * @param description the description
	 * @param datatype the datatype class
	 * @param datatypeConfig the data type config (can be null)
	 * @param minOccurs the minimum allowed occurrences
	 * @param maxOccurs the maximum allowed occurrences
	 * @param uuid the UUID
	 * @return the transient object
	 */
	public static VisitAttributeType visitAttributeType(String name, String description, Class<?> datatype, String datatypeConfig, int minOccurs, int maxOccurs, String uuid) {
		VisitAttributeType obj = new VisitAttributeType();
		obj.setName(name);
		obj.setDescription(description);
		obj.setDatatypeClassname(datatype.getName());
		obj.setDatatypeConfig(datatypeConfig);
		obj.setMinOccurs(minOccurs);
		obj.setMaxOccurs(maxOccurs);
		obj.setUuid(uuid);
		return obj;
	}

	/**
	 * Constructs a visit type
	 * @param name the name
	 * @param description the description
	 * @param uuid the UUID
	 * @return the transient object
	 */
	public static VisitType visitType(String name, String description, String uuid) {
		VisitType obj = new VisitType();
		obj.setName(name);
		obj.setDescription(description);
		obj.setUuid(uuid);
		return obj;
	}

	/**
	 * Construct a relationship type
	 * @param aIsToB the A is to B
	 * @param bIsToA the B is to A
	 * @param description the description
	 * @param uuid  the UUID
	 * @return the transient object
	 */
	public static RelationshipType relationshipType(String aIsToB, String bIsToA, String description, String uuid) {
		RelationshipType relationshipType = new RelationshipType();
		relationshipType.setaIsToB(aIsToB);
		relationshipType.setbIsToA(bIsToA);
		relationshipType.setDescription(description);
		relationshipType.setUuid(uuid);
		return relationshipType;
	}

	/**
	 * Convenience method to construct a set of identifiers
	 * @param identifiers the identifiers
	 * @return the set of identifiers
	 */
	public static Set<String> idSet(String... identifiers) {
		Set<String> set = new LinkedHashSet<String>();
		for (String identifier : identifiers) {
			set.add(identifier);
		}
		return set;
	}
}
