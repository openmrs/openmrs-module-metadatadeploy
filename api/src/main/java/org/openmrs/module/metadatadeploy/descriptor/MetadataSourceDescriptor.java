package org.openmrs.module.metadatadeploy.descriptor;

import org.openmrs.module.metadatadeploy.descriptor.Descriptor;
import org.openmrs.module.metadatadeploy.descriptor.MetadataDescriptor;
import org.openmrs.module.metadatamapping.MetadataSource;

public abstract class MetadataSourceDescriptor extends MetadataDescriptor<MetadataSource> {
	
	/**
	 * @see Descriptor#getDescribedType()
	 */
	@Override
	public Class<MetadataSource> getDescribedType() {
		return MetadataSource.class;
	}
	
}
