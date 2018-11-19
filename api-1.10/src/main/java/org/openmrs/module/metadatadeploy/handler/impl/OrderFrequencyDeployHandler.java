package org.openmrs.module.metadatadeploy.handler.impl;

import org.openmrs.OrderFrequency;
import org.openmrs.annotation.Handler;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.OrderService;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@OpenmrsProfile(openmrsVersion = "1.10.0")
@Handler(supports = OrderFrequency.class)
public class OrderFrequencyDeployHandler extends AbstractObjectDeployHandler<OrderFrequency> {

    @Autowired
    @Qualifier("orderService")
    private OrderService orderService;

    @Override
    public OrderFrequency fetch(String uuid) {
        return orderService.getOrderFrequencyByUuid(uuid);
    }

    @Override
    public OrderFrequency save(OrderFrequency obj) {
    	OrderFrequency existing = fetch(obj.getUuid());
		if (existing == null) {
			return orderService.saveOrderFrequency(obj);
		}
		return obj;
	}

    @Override
    public void uninstall(OrderFrequency obj, String reason) {
        orderService.retireOrderFrequency(obj, reason);
    }
}
