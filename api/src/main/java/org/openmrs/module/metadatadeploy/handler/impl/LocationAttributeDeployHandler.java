package org.openmrs.module.metadatadeploy.handler.impl;

import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.annotation.Handler;
import org.openmrs.api.LocationService;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Handler(supports = { LocationAttribute.class })
public class LocationAttributeDeployHandler extends AbstractObjectDeployHandler<LocationAttribute> {

    @Autowired
    @Qualifier("locationService")
    private LocationService locationService;

    @Override
    public LocationAttribute fetch(String identifier) {
        return locationService.getLocationAttributeByUuid(identifier);
    }

    @Override
    public void overwrite(LocationAttribute incoming, LocationAttribute existing) {

        if (existing.getLocation() != incoming.getLocation()) {
            throw new IllegalStateException("Unable to change location associated with a location attribute");
        }

        existing.setValue(incoming.getValue());
        existing.setAttributeType(incoming.getAttributeType());
    }

    @Override
    public LocationAttribute save(LocationAttribute obj) {
        Location location = obj.getLocation();
        location.addAttribute(obj);
        locationService.saveLocation(location);
        return obj;
    }

    @Override
    public void uninstall(LocationAttribute obj, String reason) {
        Location location = obj.getLocation();
        location.getAttributes().remove(obj);
        locationService.saveLocation(location);
    }

}
