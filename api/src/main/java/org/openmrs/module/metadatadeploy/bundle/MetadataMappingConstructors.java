package org.openmrs.module.metadatadeploy.bundle;

import org.openmrs.module.metadatamapping.MetadataSet;
import org.openmrs.module.metadatamapping.MetadataSetMember;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;

public class MetadataMappingConstructors {
	
	/**
	 * Constructs a Metadata Source
	 * @param name the name
	 * @param description the description
	 * @param uuid the UUID
	 * @return the transient object
	 */
	public static MetadataSource metadataSource(String name, String description, String uuid) {
		MetadataSource obj = new MetadataSource();
		obj.setName(name);
		obj.setDescription(description);
		obj.setUuid(uuid);
		return obj;
	}

	/**
	 * Constructs a Metadata Term Mapping
	 * @param metadataSource
	 * @param code
	 * @param metadataClass
	 * @param metadataUuid
	 * @param uuid
     * @return
     */
	public static MetadataTermMapping metadataTermMapping(MetadataSource metadataSource, String code, String metadataClass,
                                                          String metadataUuid, String uuid) {
		MetadataTermMapping obj = new MetadataTermMapping();
		obj.setMetadataSource(metadataSource);
		obj.setCode(code);
		obj.setMetadataClass(metadataClass);
		obj.setMetadataUuid(metadataUuid);
		obj.setUuid(uuid);
		return obj;
	}

	/**
	 * Constructs a Metadata Set
	 * @param uuid
	 * @return
     */
	public static MetadataSet metadataSet(String uuid) {
		MetadataSet obj = new MetadataSet();
		obj.setUuid(uuid);
		return obj;
	}

	/**
	 * Constructs  Metadata Set Memeber
	 * @param set
	 * @param metadataClass
	 * @param metadataUuid
	 * @param sortWeight
	 * @param uuid
     * @return
     */
	public static MetadataSetMember metadataSetMember(MetadataSet set, String metadataClass, String metadataUuid,
                                                      Double sortWeight, String uuid) {
		MetadataSetMember obj = new MetadataSetMember();
		obj.setMetadataSet(set);
		obj.setMetadataClass(metadataClass);
		obj.setMetadataUuid(metadataUuid);
		obj.setSortWeight(sortWeight);
		obj.setUuid(uuid);
		return obj;
	}
	
}
