package org.openmrs.module.metadatadeploy.descriptor;

import org.openmrs.ProgramWorkflowState;

public abstract class ProgramWorkflowStateDescriptor extends MetadataDescriptor<ProgramWorkflowState> {

    public abstract String conceptUuid();

    public abstract Boolean initial();

    public abstract Boolean terminal();

    @Override
    public String name() { return null; }

    @Override
    public String description() { return null; }

    @Override
    public Class<ProgramWorkflowState> getDescribedType() {
        return ProgramWorkflowState.class;
    }
}
