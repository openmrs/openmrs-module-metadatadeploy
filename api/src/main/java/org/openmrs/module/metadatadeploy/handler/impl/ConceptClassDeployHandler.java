package org.openmrs.module.metadatadeploy.handler.impl;

import org.openmrs.ConceptClass;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;

/**
 * Handler that lets you fetch ConceptClass
 */
@Handler(supports = ConceptClass.class)
public class ConceptClassDeployHandler extends AbstractObjectDeployHandler<ConceptClass> {

    @Autowired
    @Qualifier("conceptService")
    private ConceptService conceptService;

    @Override
    public ConceptClass fetch(String identifier) {
        return conceptService.getConceptClassByUuid(identifier);
    }

    @Override
    public ConceptClass save(ConceptClass obj) {
        return conceptService.saveConceptClass(obj);
    }

    @Override
    public void uninstall(ConceptClass obj, String reason) {
        // API doesn't have a retireConceptClass method
        obj.setRetired(true);
        obj.setRetireReason(reason);
        obj.setDateRetired(new Date());
        obj.setRetiredBy(Context.getAuthenticatedUser());
        conceptService.saveConceptClass(obj);
    }

}
