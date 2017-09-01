package org.openmrs.module.metadatadeploy.descriptor;

import org.openmrs.ProgramWorkflow;

import java.util.HashSet;
import java.util.Set;

public abstract class ProgramWorkflowDescriptor extends MetadataDescriptor<ProgramWorkflow> {

    public abstract String conceptUuid();

    @Override
    public String name() { return null; }

    @Override
    public String description() { return null; }

    public Set<ProgramWorkflowStateDescriptor> states() {
        return new HashSet<ProgramWorkflowStateDescriptor>();
    }

    @Override
    public Class<ProgramWorkflow> getDescribedType() {
        return ProgramWorkflow.class;
    }
}
