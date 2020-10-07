package org.openmrs.module.metadatadeploy.bundle;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.metadatadeploy.descriptor.PatientIdentifierTypeDescriptor1_11;

@OpenmrsProfile(openmrsVersion = "1.11.*")
public abstract class AbstractMetadataBundle1_11 extends AbstractMetadataBundle {

    protected void install(PatientIdentifierTypeDescriptor1_11 d) {
        install(CoreConstructors1_11.patientIdentifierType(d.name(), d.description(), d.format(), d.formatDescription(), d.validator(), d.locationBehavior(), d.uniquenessBehavior(), d.required(), d.uuid()));
    }
}
