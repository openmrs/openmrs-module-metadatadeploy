/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.metadatadeploy.handler.impl;

import org.openmrs.Concept;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.OpenmrsObject;
import org.openmrs.Retireable;
import org.openmrs.Voidable;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.ObjectUtils;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Deployment handler for concepts.
 */
@Handler(supports = { Concept.class, ConceptNumeric.class })
public class ConceptDeployHandler extends AbstractObjectDeployHandler<Concept> {

	@Autowired
	@Qualifier("conceptService")
	private ConceptService conceptService;

    private Map<Class, Set<String>> excludeFields;

    public ConceptDeployHandler() {
        super();
        excludeFields = new HashMap<Class, Set<String>>();
        excludeFields.put(Concept.class, new HashSet<String>(Arrays.asList(
                "conceptId", "names", "descriptions"
        )));
        excludeFields.put(ConceptName.class, new HashSet<String>(Arrays.asList(
                "conceptNameId", "concept"
        )));
        excludeFields.put(ConceptDescription.class, new HashSet<String>(Arrays.asList(
                "conceptDescriptionId", "concept"
        )));
    }

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#fetch(String)
	 */
	@Override
	public Concept fetch(String identifier) {
		return conceptService.getConceptByUuid(identifier);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#findAlternateMatch(org.openmrs.OpenmrsObject)
	 */
	@Override
	public Concept findAlternateMatch(Concept obj) {
		return null;
	}

    /**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#save(org.openmrs.OpenmrsObject)
	 */
	@Override
	public Concept save(Concept concept) {
        return conceptService.saveConcept(concept);
    }

    @Override
    public void overwrite(Concept incoming, Concept existing) {
        merge(existing, incoming);
    }

    private <T extends OpenmrsObject> void merge(T existing, T incoming) {
        Set<String> fieldsToExclude = excludeFields.get(existing.getClass());
        if (fieldsToExclude == null) {
            throw new IllegalStateException("Don't know what to do with class: " + existing.getClass());
        }

        ObjectUtils.overwrite(incoming, existing, fieldsToExclude);
        if (existing instanceof Concept) {
            Concept existingConcept = (Concept) existing;
            Concept incomingConcept = (Concept) incoming;
            mergeCollection(existingConcept.getNames(true), incomingConcept.getNames());
            mergeCollection(existingConcept.getDescriptions(), incomingConcept.getDescriptions());
        }
    }

    private <T extends OpenmrsObject> void mergeCollection(Collection<T> existing, Collection<T> incoming) {
        Set<T> handled = new HashSet<T>();
        Set<T> incomingToAdd = new HashSet<T>();
        for (T incomingItem : incoming) {
            T existingItem = findExisting(existing, incomingItem);
            if (existingItem == null) {
                incomingToAdd.add(incomingItem);
            } else {
                merge(existingItem, incomingItem);
                handled.add(existingItem);
            }
        }

        for (T existingItem : existing) {
            if (!handled.contains(existingItem)) {
                delete(existingItem);
            }
        }

        for (T incomingItem : incomingToAdd) {
            existing.add(incomingItem);
        }
    }


    private <T extends OpenmrsObject> T findExisting(Collection<T> collection, T incomingItem) {
        for (T candidate : collection) {
            if (candidate.getUuid().equals(incomingItem.getUuid())) {
                return candidate;
            }
        }
        return null;
    }


    private void delete(OpenmrsObject existing) {
        if (existing instanceof Voidable) {
            Voidable voidable = (Voidable) existing;
            voidable.setVoided(true);
            voidable.setDateVoided(new Date());
            voidable.setVoidReason("metadata deploy");
            voidable.setVoidedBy(Context.getAuthenticatedUser());
        }
        else if (existing instanceof Retireable) {
            Retireable retireable = (Retireable) existing;
            retireable.setRetired(true);
            retireable.setDateRetired(new Date());
            retireable.setRetireReason("metadata deploy");
            retireable.setRetiredBy(Context.getAuthenticatedUser());
        }
        else {
            throw new IllegalStateException(existing.getClass().getName() + " is not Voidable or Retirable");
        }
    }


    /**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#uninstall(org.openmrs.OpenmrsObject, String)
	 */
	@Override
	public void uninstall(Concept obj, String reason) {
        conceptService.retireConcept(obj, reason);
    }
}