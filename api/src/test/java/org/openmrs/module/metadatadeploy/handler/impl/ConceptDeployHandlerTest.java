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

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.ConceptNumeric;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptDescriptionBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptNameBuilder;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Locale;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.metadatadeploy.MetadataUtils.existing;

/**
 * Tests for {@link ConceptDeployHandler}
 */
public class ConceptDeployHandlerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private MetadataDeployService deployService;

    @Autowired
    private ConceptService conceptService;

    private static final String UUID = "f35c9fde-cdc3-11e4-9dcf-b36e1005e77b";

    /**
     * Tests creating, adding name and mapping, retiring, and unretiring
     */
    @Test
    public void testCreate() {
        Concept initial = new ConceptBuilder(UUID)
                .datatype(existing(ConceptDatatype.class, "8d4a4ab4-c2cc-11de-8d13-0010c6dffd0f"))
                .conceptClass(existing(ConceptClass.class, "a82ef63c-e4e4-48d6-988a-fdd74d7541a7"))
                .name(new ConceptName("History of present illness", Locale.ENGLISH))
                .description(new ConceptDescription("How the patient describes it", Locale.ENGLISH))
                .build();
        initial.getName().addTag(new ConceptNameTag("refapp-preferred", "preferred in refapp"));

        // Check installing new
        deployService.installObject(initial);

        Concept created = conceptService.getConceptByUuid(UUID);
        assertThat(created.getNames().size(), is(1));
        assertThat(created.getName(), allOf(
                hasProperty("name", is("History of present illness")),
                hasProperty("locale", is(Locale.ENGLISH)),
                hasProperty("localePreferred", is(true)),
                hasProperty("conceptNameType", is(ConceptNameType.FULLY_SPECIFIED)),
                hasProperty("tags", containsInAnyOrder(Arrays.asList(
                                hasProperty("tag", is("refapp-preferred"))
                        )
                ))));
        assertThat(created.getDescriptions().size(), is(1));
        assertThat(created.getDescription(), allOf(
                hasProperty("description", is("How the patient describes it")),
                hasProperty("locale", is(Locale.ENGLISH))));
    }

    @Test
    public void testCreateNumeric() throws Exception {
        ConceptNumeric numeric = new ConceptNumeric();
        numeric.setUuid(UUID);
        numeric.setDatatype(conceptService.getConceptDatatypeByName("Numeric"));
        numeric.setConceptClass(existing(ConceptClass.class, "a82ef63c-e4e4-48d6-988a-fdd74d7541a7"));
        numeric.addName(new ConceptName("Favorite single digit number", Locale.ENGLISH));
        numeric.addDescription(new ConceptDescription("1-10", Locale.ENGLISH));
        numeric.setLowAbsolute(1d);
        numeric.setHiAbsolute(10d);
        numeric.setPrecise(false);
        deployService.installObject(numeric);

        ConceptNumeric created = (ConceptNumeric) conceptService.getConceptByUuid(UUID);
        assertThat(created.getLowAbsolute(), is(1d));
        assertThat(created.getHiAbsolute(), is(10d));
        assertThat(created.getPrecise(), is(false));
        assertThat(created.getName().getName(), is("Favorite single digit number"));
    }

    @Test
    public void testUpdate() throws Exception {
        executeDataSet("concept/existingConcepts.xml");

        ConceptNameTag tag = conceptService.getConceptNameTagByName("refapp-preferred");

        ConceptSource snomed = existing(ConceptSource.class, "j3nfjk33-639f-4cb4-961f-1e025b908433");
        ConceptMapType sameAs = Context.getConceptService().getConceptMapTypeByName("same-as");

        Concept changed = new ConceptBuilder(UUID)
                .datatype(existing(ConceptDatatype.class, "8d4a4ab4-c2cc-11de-8d13-0010c6dffd0f"))
                .conceptClass(existing(ConceptClass.class, "a82ef63c-e4e4-48d6-988a-fdd74d7541a7"))
                .name(new ConceptNameBuilder("56778f02-cdc4-11e4-9dcf-b36e1005e77b")
                        .name("History of present illness")
                        .locale(Locale.ENGLISH)
                        .localePreferred(true)
                        .type(ConceptNameType.FULLY_SPECIFIED)
                        .build())
                .name(new ConceptNameBuilder("new-name-uuid")
                        .name("HPI")
                        .locale(Locale.ENGLISH)
                        .localePreferred(false)
                        .tag(tag)
                        .build())
                .description(new ConceptDescriptionBuilder("75bcd12e-cdc4-11e4-9dcf-b36e1005e77b")
                        .description("How the patient describes it")
                        .locale(Locale.ENGLISH)
                        .build())
                .mapping(new ConceptMapBuilder("mapping-uuid")
                        .type(sameAs)
                        .ensureTerm(snomed, "422625006")
                        .build())
                .build();

        // Check installing new
        deployService.installObject(changed);

        Concept updated = Context.getConceptService().getConceptByUuid(UUID);
        assertThat(updated.getNames(), containsInAnyOrder(
                allOf(
                        hasProperty("uuid", is("56778f02-cdc4-11e4-9dcf-b36e1005e77b")),
                        hasProperty("name", is("History of present illness")),
                        hasProperty("locale", is(Locale.ENGLISH)),
                        hasProperty("localePreferred", is(true)),
                        hasProperty("conceptNameType", is(ConceptNameType.FULLY_SPECIFIED)),
                        hasProperty("tags", empty())),
                allOf(
                        hasProperty("uuid", is("new-name-uuid")),
                        hasProperty("name", is("HPI")),
                        hasProperty("locale", is(Locale.ENGLISH)),
                        hasProperty("localePreferred", is(false)),
                        hasProperty("conceptNameType", nullValue()),
                        hasProperty("tags", containsInAnyOrder(Arrays.asList(
                                hasProperty("tag", is("refapp-preferred"))
                        ))))));
        assertThat(updated.getDescriptions().size(), is(1));
        assertThat(updated.getDescription(), allOf(
                hasProperty("uuid", is("75bcd12e-cdc4-11e4-9dcf-b36e1005e77b")),
                hasProperty("description", is("How the patient describes it")),
                hasProperty("locale", is(Locale.ENGLISH))));
        assertThat(updated.getConceptMappings().size(), is(1));
        assertThat(updated.getConceptMappings().iterator().next(), allOf(
                hasProperty("conceptMapType", hasProperty("name", is("same-as"))),
                hasProperty("conceptReferenceTerm", allOf(
                        hasProperty("code", is("422625006")),
                        hasProperty("conceptSource", hasProperty("uuid", is("j3nfjk33-639f-4cb4-961f-1e025b908433")))
                ))
        ));

        // Check everything can be persisted
        Context.flushSession();
    }

    @Test
    public void testRetire() throws Exception {
        executeDataSet("concept/existingConcepts.xml");
        deployService.uninstallObject(deployService.fetchObject(Concept.class, UUID), "Testing");
        assertThat(Context.getConceptService().getConceptByUuid(UUID).isRetired(), is(true));
    }

    @Test
    public void testUnretire() throws Exception {
        executeDataSet("concept/existingConcepts.xml");
        deployService.installObject(conceptService.getConcept(1790));

        Concept unretired = conceptService.getConcept(1790);
        assertThat(unretired.isRetired(), is(false));
        assertThat(unretired.getDateRetired(), is(nullValue()));
        assertThat(unretired.getRetiredBy(), is(nullValue()));
        assertThat(unretired.getRetireReason(), is(nullValue()));
    }

    @Test
    public void testOverwriteSet() throws Exception {
        Concept newMember1 = conceptService.getConcept(18);
        Concept newMember2 = conceptService.getConcept(3);

        deployService.installObject(new ConceptBuilder("0f97e14e-cdc2-49ac-9255-b5126f8a5147")
                .datatype(conceptService.getConceptDatatype(4))
                .conceptClass(conceptService.getConceptClass(10))
                .name("b3ebe4f6-50b3-4e78-82b1-7d192a7ede92", "FOOD CONSTRUCT", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .description("0a7e860b-d73c-4033-be1e-2053ee025c5b", "Holder for all things edible", Locale.ENGLISH)
                .setMembers(newMember1, newMember2)
                .build());

        Concept updated = conceptService.getConceptByUuid("0f97e14e-cdc2-49ac-9255-b5126f8a5147");
        assertThat(updated.getConceptSets().size(), is(2));
        assertThat(updated.getConceptSets(), containsInAnyOrder(
                hasProperty("concept", is(newMember1)),
                hasProperty("concept", is(newMember2))
        ));
    }
}