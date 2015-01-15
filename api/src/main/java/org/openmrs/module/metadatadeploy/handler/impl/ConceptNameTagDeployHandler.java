package org.openmrs.module.metadatadeploy.handler.impl;

import org.openmrs.ConceptNameTag;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;

/**
 * Deployment handler for concept name tags
 */
@Handler(supports = { ConceptNameTag.class })
public class ConceptNameTagDeployHandler extends AbstractObjectDeployHandler<ConceptNameTag> {

    @Autowired
    @Qualifier("conceptService")
    private ConceptService conceptService;

    /**
     * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#fetch(String)
     */
    @Override
    public ConceptNameTag fetch(String identifier) {
        return conceptService.getConceptNameTagByUuid(identifier);
    }

    /**
     * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#findAlternateMatch(org.openmrs.OpenmrsObject)
     */
    @Override
    public ConceptNameTag findAlternateMatch(ConceptNameTag obj) {
        return conceptService.getConceptNameTagByName(obj.getTag());
    }

    /**
     * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#save(org.openmrs.OpenmrsObject)
     */
    @Override
    public ConceptNameTag save(ConceptNameTag obj) {
        return conceptService.saveConceptNameTag(obj);
    }

    /**
     * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#uninstall(org.openmrs.OpenmrsObject, String)
     */
    @Override
    public void uninstall(ConceptNameTag obj, String reason) {
        // for some reason there is no void method in the
        obj.setVoided(true);
        obj.setVoidedBy(Context.getAuthenticatedUser());
        obj.setVoidReason(reason);
        obj.setDateVoided(new Date());
        conceptService.saveConceptNameTag(obj);
    }

}
