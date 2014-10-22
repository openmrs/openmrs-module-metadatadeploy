package org.openmrs.module.metadatadeploy.handler;

import org.openmrs.OpenmrsObject;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Master bean that stores all the object deploy handlers
 * We don't wire these directly into the MetadataDeployServiceImpl because of the slowdown that occurs
 * when autowiring beans into a proxied bean (ie, a service)
 */
@Component(value = "objectDeployHandlers")
public class ObjectDeployHandlers {

    private Map<Class<? extends OpenmrsObject>, ObjectDeployHandler> handlers;

    /**
     * Sets the object handlers, reorganising them into a map
     * @param handlers the handler components
     */
    @Autowired
    public void setHandlers(Set<ObjectDeployHandler> handlers) {
        this.handlers = new HashMap<Class<? extends OpenmrsObject>, ObjectDeployHandler>();

        for (ObjectDeployHandler handler : handlers) {
            Handler handlerAnnotation = handler.getClass().getAnnotation(Handler.class);
            if (handlerAnnotation != null) {
                for (Class<?> supportedClass : handlerAnnotation.supports()) {
                    if (OpenmrsObject.class.isAssignableFrom(supportedClass)) {
                        if (!this.handlers.containsKey(supportedClass)
                                || handlerAnnotation.order() < this.handlers.get(supportedClass).getClass().getAnnotation(Handler.class).order()) {
                            this.handlers.put((Class<? extends OpenmrsObject>) supportedClass, handler);
                        }
                    }
                    else {
                        throw new APIException("Handler annotation specifies a non OpenmrsObject subclass");
                    }
                }
            }
        }
    }

    public Map<Class<? extends OpenmrsObject>, ObjectDeployHandler> getHandlers() {
        return handlers;
    }
}
