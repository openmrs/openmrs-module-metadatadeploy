package org.openmrs.module.metadatadeploy.handler.impl;

import org.junit.Test;
import org.openmrs.ConceptNameTag;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.conceptNameTag;

public class ConceptNameTagDeployHandlerTest extends BaseModuleContextSensitiveTest {

    public static final String UUID = "obj-uuid";

    @Autowired
    private MetadataDeployService deployService;

    @Autowired
    private ConceptService conceptService;

    @Test
    public void integration() {
        // Check installing new
        deployService.installObject(conceptNameTag("New tag", "New desc", UUID));

        ConceptNameTag created = conceptService.getConceptNameTagByUuid(UUID);
        assertThat(created.getTag(), is("New tag"));
        assertThat(created.getDescription(), is("New desc"));

        // Check updating existing
        deployService.installObject(conceptNameTag("Updated tag", "Updated desc", UUID));

        ConceptNameTag updated = conceptService.getConceptNameTagByUuid(UUID);
        assertThat(updated.getId(), is(created.getId()));
        assertThat(updated.getTag(), is("Updated tag"));
        assertThat(updated.getDescription(), is("Updated desc"));

        // Check uninstall voids
        deployService.uninstallObject(deployService.fetchObject(ConceptNameTag.class, UUID), "Testing");

        assertThat(conceptService.getConceptNameTagByUuid(UUID).getVoided(), is(true));

        // Check re-install unvoids
        deployService.installObject(conceptNameTag("Unvoided tag", "Unvoided desc", UUID));

        ConceptNameTag unvoided = conceptService.getConceptNameTagByUuid(UUID);
        assertThat(unvoided.getTag(), is("Unvoided tag"));
        assertThat(unvoided.getDescription(), is("Unvoided desc"));
        assertThat(unvoided.getVoided(), is(false));
        assertThat(unvoided.getDateVoided(), nullValue());
        assertThat(unvoided.getVoidedBy(), nullValue());
        assertThat(unvoided.getVoidReason(), nullValue());

        // Check everything can be persisted
        Context.flushSession();
    }

}