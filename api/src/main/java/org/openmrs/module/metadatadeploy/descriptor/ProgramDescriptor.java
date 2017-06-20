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

package org.openmrs.module.metadatadeploy.descriptor;

import org.openmrs.Program;

/**
 * Encapsulates the information needed to create a new Program
 */
public abstract class ProgramDescriptor extends MetadataDescriptor<Program> {

    /**
     * Get the concept uuid as String
     */
    public abstract String conceptUuid();

    /**
     * Get the outcomes concept uuid as a String; override if this program has an outcomes concept
     */
    public String outcomesConceptUuid() {
        return null;
    }

    /**
     * @see Descriptor#getDescribedType()
     */
    @Override
    public Class<Program> getDescribedType() {
        return Program.class;
    }
}
