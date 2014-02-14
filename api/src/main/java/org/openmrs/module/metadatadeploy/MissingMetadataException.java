/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.metadatadeploy;

import org.openmrs.OpenmrsObject;

/**
 * Thrown when a request for metadata which is assumed to exist, fails because the metadata doesn't exist
 */
public class MissingMetadataException extends RuntimeException {

	private Class<? extends OpenmrsObject> objectClass;

	private String objectIdentifier;

	/**
	 * Convenience constructor for a missing metadata exception
	 * @param objectClass the metadata object class
	 * @param objectIdentifier the object identifier
	 */
	public MissingMetadataException(Class<? extends OpenmrsObject> objectClass, String objectIdentifier) {
		super("No such " + objectClass.getSimpleName() + " with identifier '" + objectIdentifier + "'");

		this.objectClass = objectClass;
		this.objectIdentifier = objectIdentifier;
	}

	/**
	 * Gets the class of the missing object
	 * @return the class
	 */
	public Class<? extends OpenmrsObject> getObjectClass() {
		return objectClass;
	}

	/**
	 * Gets the requested identifier of the missing object
	 * @return the identifier
	 */
	public String getObjectIdentifier() {
		return objectIdentifier;
	}
}