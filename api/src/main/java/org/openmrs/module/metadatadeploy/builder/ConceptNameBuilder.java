package org.openmrs.module.metadatadeploy.builder;

import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Locale;

public class ConceptNameBuilder {

    ConceptName entity;

    public ConceptNameBuilder(String uuid) {
        entity = new ConceptName();
        entity.setUuid(uuid);
    }

    public ConceptNameBuilder name(String name) {
        entity.setName(name);
        return this;
    }

    public ConceptNameBuilder locale(Locale locale) {
        entity.setLocale(locale);
        return this;
    }

    public ConceptNameBuilder type(ConceptNameType type) {
        entity.setConceptNameType(type);
        return this;
    }

    public ConceptNameBuilder localePreferred(boolean localePreferred) {
        entity.setLocalePreferred(localePreferred);
        return this;
    }

    public ConceptNameBuilder tag(ConceptNameTag tag) {
        entity.addTag(tag);
        return this;
    }

    public ConceptNameBuilder tag(String tag) {
        ConceptNameTag tagObject = MetadataUtils.existing(ConceptNameTag.class, tag);
        entity.addTag(tagObject);
        return this;
    }

    public ConceptName build() {
        return entity;
    }

}
