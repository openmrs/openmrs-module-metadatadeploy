package org.openmrs.module.metadatadeploy.builder;

import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.api.context.Context;

public class ConceptMapBuilder {

    ConceptMap entity = new ConceptMap();

    public ConceptMapBuilder(String uuid) {
        entity = new ConceptMap();
        entity.setUuid(uuid);
    }

    public ConceptMapBuilder type(ConceptMapType type) {
        entity.setConceptMapType(type);
        return this;
    }

    public ConceptMapBuilder term(ConceptReferenceTerm term) {
        entity.setConceptReferenceTerm(term);
        return this;
    }

    /**
     * This will save a new concept reference term if no suitable one exists yet
     * @param source
     * @param code
     * @return
     */
    public ConceptMapBuilder ensureTerm(ConceptSource source, String code) {
        ConceptReferenceTerm term = Context.getConceptService().getConceptReferenceTermByCode(code, source);
        if (term == null) {
            term = new ConceptReferenceTerm(source, code, null);
            Context.getConceptService().saveConceptReferenceTerm(term);
        }
        entity.setConceptReferenceTerm(term);
        return this;
    }

    public ConceptMap build() {
        return entity;
    }
}
