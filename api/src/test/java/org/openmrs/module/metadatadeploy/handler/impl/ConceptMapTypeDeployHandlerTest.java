package org.openmrs.module.metadatadeploy.handler.impl;

import org.junit.Test;
import org.openmrs.ConceptMapType;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ConceptMapTypeDeployHandlerTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private MetadataDeployService deployService;

    @Test
    public void testFetch() throws Exception {
        ConceptMapType sameAs = Context.getConceptService().getConceptMapTypeByName("same-as");
        assertThat(deployService.fetchObject(ConceptMapType.class, "35543629-7d8c-11e1-909d-c80aa9edcf4e"), is(sameAs));
    }
}