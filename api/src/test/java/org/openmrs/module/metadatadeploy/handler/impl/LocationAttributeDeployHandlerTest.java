package org.openmrs.module.metadatadeploy.handler.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.LocationAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.location;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.locationAttribute;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.locationAttributeType;

public class LocationAttributeDeployHandlerTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/metadatadeploy/include/";

    @Autowired
    private MetadataDeployService deployService;

    @Test
    public void integration() {

        // Set up the existing objects that we will need
        deployService.installObject(locationAttributeType("New name", "New desc", FreeTextDatatype.class, null, 0, 1, "attribute-type-uuid"));
        deployService.installObject(location("New name", "New desc", "location-uuid"));

        deployService.installObject(locationAttribute("location-uuid", "attribute-type-uuid", "test me", "attribute-uuid"));

        LocationAttribute created = Context.getLocationService().getLocationAttributeByUuid("attribute-uuid");

        Assert.assertThat((String) created.getValue(), is("test me"));
        Assert.assertThat(created.getLocation().getUuid(), is("location-uuid"));
        Assert.assertThat(created.getAttributeType().getUuid(), is("attribute-type-uuid"));
        Assert.assertThat(created.getUuid(), is("attribute-uuid"));

        LocationAttribute updated = deployService.installObject(locationAttribute("location-uuid", "attribute-type-uuid", "new value", "attribute-uuid"));

        Assert.assertThat((String) updated.getValue(), is("new value"));
        Assert.assertThat(updated.getLocation().getUuid(), is("location-uuid"));
        Assert.assertThat(updated.getAttributeType().getUuid(), is("attribute-type-uuid"));
        Assert.assertThat(updated.getUuid(), is("attribute-uuid"));

        // Check uninstall voided
        deployService.uninstallObject(deployService.fetchObject(LocationAttribute.class, "attribute-uuid"), "Testing");
        Assert.assertThat(Context.getLocationService().getLocationAttributeByUuid("attribute-uuid"), nullValue());

        // Check re-install unvoided
        LocationAttribute reinstalled = deployService.installObject(locationAttribute("location-uuid", "attribute-type-uuid", "test me", "attribute-uuid"));
        Assert.assertThat((String) reinstalled.getValue(), is("test me"));
        Assert.assertThat(reinstalled.getLocation().getUuid(), is("location-uuid"));
        Assert.assertThat(reinstalled.getAttributeType().getUuid(), is("attribute-type-uuid"));
        Assert.assertThat(reinstalled.getUuid(), is("attribute-uuid"));
        Assert.assertThat(reinstalled.isVoided(), is(false));
        Assert.assertThat(reinstalled.getVoidReason(), nullValue());
        Assert.assertThat(reinstalled.getVoidedBy(), nullValue());
        Assert.assertThat(reinstalled.getDateVoided(), nullValue());

        Context.flushSession();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailIfAttemptingToChangeLocation() {

        // Set up the existing objects that we will need
        deployService.installObject(locationAttributeType("New name", "New desc", FreeTextDatatype.class, null, 0, 1, "attribute-type-uuid"));
        deployService.installObject(location("New name", "New desc", "location-uuid"));
        deployService.installObject(location("Another location", "Another location", "another-location-uuid"));

        // install the attribute
        deployService.installObject(locationAttribute("location-uuid", "attribute-type-uuid", "test me", "attribute-uuid"));

        // attempt to change the location on the attribute
        deployService.installObject(locationAttribute("another-location-uuid", "attribute-type-uuid", "new value", "attribute-uuid"));

    }

}
