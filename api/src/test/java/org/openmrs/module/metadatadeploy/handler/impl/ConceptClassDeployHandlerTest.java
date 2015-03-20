package org.openmrs.module.metadatadeploy.handler.impl;

import org.junit.Test;
import org.openmrs.ConceptClass;
import org.openmrs.api.ConceptService;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link ConceptClassDeployHandler}
 */
public class ConceptClassDeployHandlerTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private MetadataDeployService deployService;

    @Autowired
    private ConceptService conceptService;

    @Test
    public void testFetch() {
        ConceptClass question = conceptService.getConceptClassByName("Question");
        assertThat(deployService.fetchObject(ConceptClass.class, "a82ef63c-e4e4-48d6-988a-fdd74d7541a7"), is(question));
    }

    @Test
    public void testCreate() throws Exception {
        ConceptClass clazz = new ConceptClass();
        clazz.setUuid("obj-uuid");
        clazz.setName("The name");
        clazz.setDescription("The description");

        ConceptClass created = deployService.installObject(clazz);
        assertThat(created.getUuid(), is("obj-uuid"));
        assertThat(created.getName(), is("The name"));
        assertThat(created.getDescription(), is("The description"));
    }

    @Test
    public void testRetire() throws Exception {
        ConceptClass question = conceptService.getConceptClassByName("Question");
        deployService.uninstallObject(question, "testing");

        question = conceptService.getConceptClassByName("Question");
        assertThat(question.getRetired(), is(true));
        assertThat(question.getRetireReason(), is("testing"));
    }

    @Test
    public void testUpdate() throws Exception {
        ConceptClass clazz = new ConceptClass();
        clazz.setUuid("a82ef63c-e4e4-48d6-988a-fdd74d7541a7");
        clazz.setName("Question?");
        clazz.setDescription("The description");

        ConceptClass created = deployService.installObject(clazz);
        assertThat(created.getUuid(), is("a82ef63c-e4e4-48d6-988a-fdd74d7541a7"));
        assertThat(created.getName(), is("Question?"));
        assertThat(created.getDescription(), is("The description"));
    }

}
