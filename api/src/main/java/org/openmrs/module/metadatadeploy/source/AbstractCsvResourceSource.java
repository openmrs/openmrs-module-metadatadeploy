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

package org.openmrs.module.metadatadeploy.source;

import au.com.bytecode.opencsv.CSVReader;
import org.openmrs.OpenmrsObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Abstract base class for object sources from CSV resources.
 */
public abstract class AbstractCsvResourceSource<T extends OpenmrsObject> implements ObjectSource<T> {

	private CSVReader reader;

	/**
	 * Constructs a new source
	 * @param csvFile the resource file path
	 * @param hasHeader true if file has a header row
	 * @throws IOException if an error occurs
	 */
	public AbstractCsvResourceSource(String csvFile, boolean hasHeader) throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream(csvFile);
		reader = new CSVReader(new InputStreamReader(in));

		// Throw away first line if it's a header
		if (hasHeader) {
			reader.readNext();
		}
	}

	/**
	 * @see ObjectSource#fetchNext()
	 */
	@Override
	public T fetchNext() throws IOException {
		String[] line = reader.readNext();
		if (line == null) {
			close();
			return null;
		}
		return parseLine(line);
	}

	/**
	 * Parses a CSV line into an object
	 * @param line the line
	 * @return the object
	 */
	protected abstract T parseLine(String[] line);

	/**
	 * Closes the source
	 * @throws IOException if an error occurs
	 */
	protected void close() throws IOException {
		reader.close();
	}
}