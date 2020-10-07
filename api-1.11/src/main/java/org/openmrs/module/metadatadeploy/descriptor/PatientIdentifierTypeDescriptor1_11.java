package org.openmrs.module.metadatadeploy.descriptor;

import org.openmrs.PatientIdentifierType;

public abstract class PatientIdentifierTypeDescriptor1_11 extends  PatientIdentifierTypeDescriptor{


    public PatientIdentifierType.UniquenessBehavior uniquenessBehavior() {
        return PatientIdentifierType.UniquenessBehavior.UNIQUE;
    }
}
