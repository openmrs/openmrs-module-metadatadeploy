package org.openmrs.module.metadatadeploy.builder;

import org.openmrs.ConceptDescription;

import java.util.Locale;

public class ConceptDescriptionBuilder {

    private ConceptDescription entity;

    public ConceptDescriptionBuilder(String uuid) {
        entity = new ConceptDescription();
        entity.setUuid(uuid);
    }

    public ConceptDescriptionBuilder description(String description) {
        entity.setDescription(description);
        return this;
    }

    public ConceptDescriptionBuilder locale(Locale locale) {
        entity.setLocale(locale);
        return this;
    }

    public ConceptDescription build() {
        return entity;
    }

}
