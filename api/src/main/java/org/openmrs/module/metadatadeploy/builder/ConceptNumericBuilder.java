package org.openmrs.module.metadatadeploy.builder;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.ConceptNameType;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Locale;

public class ConceptNumericBuilder extends ConceptBuilder {

    public ConceptNumericBuilder(String uuid) {
        super(uuid, new ConceptNumeric());
        this.entity.setUuid(uuid);
    }

    public ConceptNumericBuilder datatype(ConceptDatatype conceptDatatype) {
        return (ConceptNumericBuilder) super.datatype(conceptDatatype);
    }

    public ConceptNumericBuilder conceptClass(ConceptClass conceptClass) {
        return (ConceptNumericBuilder) super.conceptClass(conceptClass);
    }

    public ConceptNumericBuilder name(String uuid, String name, Locale locale, ConceptNameType type, String... tags) {
        return (ConceptNumericBuilder) super.name(uuid, name, locale, type, tags);
    }

    /**
     * Make sure you set the UUID of the name before passing it in here
     * @param name
     * @return
     */
    public ConceptNumericBuilder name(ConceptName name) {
        return (ConceptNumericBuilder) super.name(name);
    }

    public ConceptNumericBuilder description(ConceptDescription description) {
        return (ConceptNumericBuilder) super.description(description);
    }

    public ConceptNumericBuilder description(String uuid, String description, Locale locale) {
        return (ConceptNumericBuilder) super.description(uuid, description, locale);
    }

    public ConceptNumericBuilder mapping(ConceptMap map) {
        return (ConceptNumericBuilder) super.mapping(map);
    }

    public ConceptNumericBuilder setMembers(Concept... members) {
        return (ConceptNumericBuilder) super.setMembers(members);
    }

    public ConceptNumericBuilder answers(Concept... answers) {
        return (ConceptNumericBuilder) super.answers(answers);
    }

    public ConceptNumericBuilder hiAbsolute(Double hiAbsolute) {
        ((ConceptNumeric) entity).setHiAbsolute(hiAbsolute);
        return this;
    }

    public ConceptNumericBuilder hiCritical(Double hiCritical) {
        ((ConceptNumeric) entity).setHiCritical(hiCritical);
        return this;
    }

    public ConceptNumericBuilder hiNormal(Double hiNormal) {
        ((ConceptNumeric) entity).setHiNormal(hiNormal);
        return this;
    }

    public ConceptNumericBuilder lowAbsolute(Double lowAbsolute) {
        ((ConceptNumeric) entity).setLowAbsolute(lowAbsolute);
        return this;
    }

    public ConceptNumericBuilder lowCritical(Double lowCritical) {
        ((ConceptNumeric) entity).setLowCritical(lowCritical);
        return this;
    }

    public ConceptNumericBuilder lowNormal(Double lowNormal) {
        ((ConceptNumeric) entity).setLowNormal(lowNormal);
        return this;
    }

    public ConceptNumericBuilder units(String units) {
        ((ConceptNumeric) entity).setUnits(units);
        return this;
    }

    public ConceptNumericBuilder precise(Boolean precise) {
    	try {
    		((ConceptNumeric) entity).setPrecise(precise);
    	}
        catch(NoSuchMethodError ex) {
			try {
				Method method = ((ConceptNumeric) entity).getClass().getMethod("setAllowDecimal", new Class[] { Boolean.class });
				method.invoke(((ConceptNumeric) entity), precise);
			}
			catch (Exception e) {e.printStackTrace();}
		}
        return this;
    }

}
