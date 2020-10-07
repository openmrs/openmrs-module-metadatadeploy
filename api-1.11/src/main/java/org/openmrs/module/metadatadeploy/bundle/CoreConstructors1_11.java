package org.openmrs.module.metadatadeploy.bundle;

import org.openmrs.PatientIdentifierType;
import org.openmrs.patient.IdentifierValidator;

public class CoreConstructors1_11 {

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
                                                              PatientIdentifierType.UniquenessBehavior uniquenessBehavior,
                                                              boolean required,
                                                              String uuid) {

        PatientIdentifierType obj = new PatientIdentifierType();
        obj.setName(name);
        obj.setDescription(description);
        obj.setFormat(format);
        obj.setFormatDescription(formatDescription);
        obj.setValidator(validator != null ? validator.getName() : null);
        obj.setLocationBehavior(locationBehavior);
        obj.setUniquenessBehavior(uniquenessBehavior);
        obj.setRequired(required);
        obj.setUuid(uuid);
        return obj;
    }
}
