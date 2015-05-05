package org.openmrs.module.metadatadeploy.handler.impl;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.OrderFrequency;
import org.openmrs.api.ConceptService;
import org.openmrs.api.OrderService;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.bundle.CoreConstructors1_10;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Locale;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OrderFrequencyDeployHandlerTest extends BaseModuleContextSensitiveTest {

    public static final String EXISTING_UUID = "28090760-7c38-11e3-baa7-0800200c9a66";
    public static final String ANOTHER_EXISTING_UUID = "38090760-7c38-11e3-baa7-0800200c9a66";

    @Autowired
    private MetadataDeployService deployService;

    @Autowired
    private OrderService orderService;

    @Autowired
    @Qualifier("conceptService")
    private ConceptService conceptService;

    @Test
    public void testFetch() throws Exception {
        OrderFrequency expected = orderService.getOrderFrequencyByUuid(EXISTING_UUID);
        OrderFrequency actual = deployService.fetchObject(OrderFrequency.class, EXISTING_UUID);
        assertThat(actual, is(expected));
    }

    @Test
    public void testCreate() throws Exception {
        Concept bid = createBidConcept();

        OrderFrequency orderFrequency = CoreConstructors1_10.orderFrequency(bid.getUuid(), 2.0, "of-uuid");
        OrderFrequency created = deployService.installObject(orderFrequency);
        assertThat(created.getUuid(), is("of-uuid"));
        assertThat(created.getFrequencyPerDay(), is(2.0));
        assertThat(created.getConcept(), is(bid));
    }

    @Test
    public void testRetire() throws Exception {
        OrderFrequency existing = orderService.getOrderFrequencyByUuid(EXISTING_UUID);
        deployService.uninstallObject(existing, "because");

        existing = orderService.getOrderFrequencyByUuid(EXISTING_UUID);
        assertThat(existing.getRetired(), is(true));
        assertThat(existing.getRetireReason(), is("because"));
    }

    @Test
    public void testUpdate() throws Exception {
        Concept bid = createBidConcept();

        OrderFrequency orderFrequency = CoreConstructors1_10.orderFrequency(bid.getUuid(), 2.0, ANOTHER_EXISTING_UUID);
        deployService.installObject(orderFrequency);
        OrderFrequency created = orderService.getOrderFrequencyByUuid(ANOTHER_EXISTING_UUID);

        assertThat(created.getFrequencyPerDay(), is(2.0));
        assertThat(created.getConcept(), is(bid));
    }

    private Concept createBidConcept() {
        return conceptService.saveConcept(new ConceptBuilder(UUID.randomUUID().toString())
                .name(new ConceptName("BID", Locale.ENGLISH))
                .datatype(conceptService.getConceptDatatypeByName("N/A"))
                .conceptClass(conceptService.getConceptClassByName("Frequency"))
                .build());
    }
}