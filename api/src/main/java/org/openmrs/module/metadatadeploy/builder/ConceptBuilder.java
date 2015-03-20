package org.openmrs.module.metadatadeploy.builder;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Locale;

/**
 * Builder pattern for constructing a Concept (since it has too many fields to be easy to do via CoreConstructors)
 */
public class ConceptBuilder {

    private Concept entity;

    public ConceptBuilder(String uuid) {
        entity = new Concept();
        entity.setUuid(uuid);
    }

    /**
     * E.g. if you want to do new ConceptBuilder("uuid", new ConceptNumeric())...
     * @param entity
     */
    public ConceptBuilder(String uuid, Concept entity) {
        entity.setUuid(uuid);
        this.entity = entity;
    }

    public ConceptBuilder uuid(String uuid) {
        entity.setUuid(uuid);
        return this;
    }

    public ConceptBuilder datatype(ConceptDatatype conceptDatatype) {
        entity.setDatatype(conceptDatatype);
        return this;
    }

    public ConceptBuilder conceptClass(ConceptClass conceptClass) {
        entity.setConceptClass(conceptClass);
        return this;
    }

    public ConceptBuilder name(String uuid, String name, Locale locale, ConceptNameType type, String... tags) {
        ConceptName conceptName = new ConceptName();
        conceptName.setUuid(uuid);
        conceptName.setName(name);
        conceptName.setLocale(locale);
        conceptName.setConceptNameType(type);
        for (String tag : tags) {
            ConceptNameTag tagObject = MetadataUtils.existing(ConceptNameTag.class, tag);
            conceptName.addTag(tagObject);
        }
        entity.addName(conceptName);
        return this;
    }

    /**
     * Make sure you set the UUID of the name before passing it in here
     * @param name
     * @return
     */
    public ConceptBuilder name(ConceptName name) {
        entity.addName(name);
        return this;
    }

    public ConceptBuilder description(ConceptDescription description) {
        entity.addDescription(description);
        return this;
    }

    public ConceptBuilder description(String uuid, String description, Locale locale) {
        entity.addDescription(new ConceptDescriptionBuilder(uuid)
                .description(description)
                .locale(locale).build());
        return this;
    }

    public ConceptBuilder mapping(ConceptMap map) {
        entity.addConceptMapping(map);
        return this;
    }

    public ConceptBuilder setMembers(Concept... members) {
        for (Concept member : members) {
            entity.addSetMember(member);
        }
        return this;
    }

    public ConceptBuilder answers(Concept... answers) {
        for (Concept answer : answers) {
            entity.addAnswer(new ConceptAnswer(answer));
        }
        return this;
    }

    public Concept build() {
        return entity;
    }

}
