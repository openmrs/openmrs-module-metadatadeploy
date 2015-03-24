package org.openmrs.module.metadatadeploy.handler.impl;

import org.openmrs.ConceptMapType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Handler that lets you fetch ConceptMapType (but not edit them)
 */
@Handler(supports = ConceptMapType.class)
public class ConceptMapTypeDeployHandler extends AbstractObjectDeployHandler<ConceptMapType> {

    @Autowired
    @Qualifier("conceptService")
    private ConceptService conceptService;

    @Override
    public ConceptMapType fetch(String identifier) {
        return conceptService.getConceptMapTypeByUuid(identifier);
    }

    @Override
    public ConceptMapType save(ConceptMapType obj) {
        throw new UnsupportedOperationException("ConceptMapType can only be fetched (for now)");
    }

    @Override
    public void uninstall(ConceptMapType obj, String reason) {
        throw new UnsupportedOperationException("ConceptMapType can only be fetched (for now)");
    }
}
