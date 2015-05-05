package org.openmrs.module.metadatadeploy.bundle;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.OrderFrequency;
import org.openmrs.OrderType;
import org.openmrs.module.metadatadeploy.MetadataUtils;

/**
 * Constructors for core metadata classes introduced in openmrs-core 1.10
 *
 * Most constructors are in {@link CoreConstructors}
 */
public class CoreConstructors1_10 {

    public static OrderFrequency orderFrequency(String conceptUuid, Double frequencyPerDay, String uuid) {
        OrderFrequency orderFrequency = new OrderFrequency();
        orderFrequency.setConcept(MetadataUtils.existing(Concept.class, conceptUuid));
        if (frequencyPerDay != null) {
            orderFrequency.setFrequencyPerDay(frequencyPerDay);
        }
        orderFrequency.setUuid(uuid);
        return orderFrequency;
    }

    public static OrderType orderType(String name, String description, String uuid, String javaClassName, String parentUuid) {
        OrderType obj = new OrderType();
        obj.setName(name);
        obj.setDescription(description);
        obj.setUuid(uuid);
        obj.setJavaClassName(javaClassName);

        if (StringUtils.isNotBlank(parentUuid)) {
            obj.setParent(MetadataUtils.existing(OrderType.class, parentUuid));
        }

        return obj;
    }
}
