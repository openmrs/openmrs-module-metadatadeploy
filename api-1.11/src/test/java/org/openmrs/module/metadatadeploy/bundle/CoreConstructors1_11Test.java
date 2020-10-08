package org.openmrs.module.metadatadeploy.bundle;


import org.junit.Test;
import org.junit.Assert;
import org.openmrs.module.metadatadeploy.bundle.CoreConstructors1_11;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.PatientIdentifierType;
import org.openmrs.patient.IdentifierValidator;
import org.openmrs.patient.UnallowedIdentifierException;

import static org.hamcrest.Matchers.*;

public class CoreConstructors1_11Test extends BaseModuleContextSensitiveTest {


    @Test
    public void integration() {
        new CoreConstructors1_11();
    }


    /**
     * @see CoreConstructors1_11#patientIdentifierType(String, String, String, String, Class, org.openmrs.PatientIdentifierType.LocationBehavior, org.openmrs.PatientIdentifierType.UniquenessBehavior, boolean, String)
     */
    @Test
    public void patientIdentifierType() {
        PatientIdentifierType obj = CoreConstructors1_11.patientIdentifierType("name", "desc", "\\d+", "format-desc", TestingIdentifierValidator.class,
                PatientIdentifierType.LocationBehavior.NOT_USED,
                PatientIdentifierType.UniquenessBehavior.NON_UNIQUE, false, "obj-uuid");

        Assert.assertThat(obj.getName(), is("name"));
        Assert.assertThat(obj.getDescription(), is("desc"));
        Assert.assertThat(obj.getFormat(), is("\\d+"));
        Assert.assertThat(obj.getFormatDescription(), is("format-desc"));
        Assert.assertThat(obj.getValidator(), is(TestingIdentifierValidator.class.getName()));
        Assert.assertThat(obj.getLocationBehavior(), is(PatientIdentifierType.LocationBehavior.NOT_USED));
        Assert.assertThat(obj.getUniquenessBehavior(), is(PatientIdentifierType.UniquenessBehavior.NON_UNIQUE));
        Assert.assertThat(obj.getRequired(), is(false));
        Assert.assertThat(obj.getUuid(), is("obj-uuid"));
    }

    /**
     * Custom identifier validator for testing
     */
    public static class TestingIdentifierValidator implements IdentifierValidator {

        @Override
        public String getName() {
            return "Test validator";
        }

        @Override
        public boolean isValid(String identifier) throws UnallowedIdentifierException {
            return true;
        }

        @Override
        public String getValidIdentifier(String undecoratedIdentifier) throws UnallowedIdentifierException {
            return null;
        }

        @Override
        public String getAllowedCharacters() {
            return null;
        }
    }
}
