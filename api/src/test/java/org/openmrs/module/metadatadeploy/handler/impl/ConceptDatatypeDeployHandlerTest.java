package org.openmrs.module.metadatadeploy.handler.impl;

import org.junit.Test;
import org.openmrs.ConceptDatatype;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link ConceptDatatypeDeployHandler}
 */
public class ConceptDatatypeDeployHandlerTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private MetadataDeployService deployService;

    /**
     * Tests use of handler for fetching
     */
    @Test
    public void integration() {
        ConceptDatatype text = Context.getConceptService().getConceptDatatypeByName("Text");
        assertThat(deployService.fetchObject(ConceptDatatype.class, "8d4a4ab4-c2cc-11de-8d13-0010c6dffd0f"), is(text));
    }
}