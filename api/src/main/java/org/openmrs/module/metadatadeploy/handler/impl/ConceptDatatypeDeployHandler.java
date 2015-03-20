package org.openmrs.module.metadatadeploy.handler.impl;

import org.openmrs.ConceptDatatype;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Handler that lets you fetch ConceptDatatype (but not edit them)
 */
@Handler(supports = ConceptDatatype.class)
public class ConceptDatatypeDeployHandler extends AbstractObjectDeployHandler<ConceptDatatype> {

    @Autowired
    @Qualifier("conceptService")
    private ConceptService conceptService;

    @Override
    public ConceptDatatype fetch(String identifier) {
        return conceptService.getConceptDatatypeByUuid(identifier);
    }

    @Override
    public ConceptDatatype save(ConceptDatatype obj) {
        throw new UnsupportedOperationException("ConceptDatatype can only be fetched");
    }

    @Override
    public void uninstall(ConceptDatatype obj, String reason) {
        throw new UnsupportedOperationException("ConceptDatatype can only be fetched");
    }

}
