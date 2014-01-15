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

package org.openmrs.module.metadatadeploy.sync;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.source.ObjectSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Abstract base class for metadata synchronization operations
 */
public abstract class AbstractMetadataSynchronization<T extends OpenmrsMetadata> implements MetadataSynchronization<T> {

	protected static final Log log = LogFactory.getLog(AbstractMetadataSynchronization.class);

	protected ObjectSource<T> source;

	protected Map<Object, Integer> keyCache = new HashMap<Object, Integer>();

	protected Set<Integer> notSyncedObjects = new HashSet<Integer>();

	protected SyncResult<T> result = new SyncResult<T>();

	/**
	 * Creates and initiates a new synchronization
	 */
	public AbstractMetadataSynchronization(ObjectSource<T> source) {
		this.source = source;
	}

	/**
	 * @see MetadataSynchronization#run()
	 */
	@Override
	public SyncResult<T> run() {
		MetadataDeployService deployService = Context.getService(MetadataDeployService.class);

		initializeCache();

		try {
			T next;

			while ((next = source.fetchNext()) != null) {
				Object syncKey = getObjectSyncKey(next);

				if (syncKey == null) {
					log.error("Unable to synchronize object '" + next.getName() + "' with no sync key");
				} else {
					synchronizeObject(deployService, syncKey, next);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		retireExistingNotInSource(deployService);

		return result;
	}

	/**
	 * Initializes the key -> object cache
	 */
	protected void initializeCache() {
		for (T obj : fetchAllExisting()) {
			Object syncKey = getObjectSyncKey(obj);

			if (syncKey == null) {
				log.warn("Ignoring object '" + obj.getName() + "' with no sync key");
			}
			else {
				// Check there isn't another object with this key
				if (keyCache.containsKey(syncKey)) {
					log.warn("Ignoring object '" + obj.getName() + "' with duplicate sync key " + syncKey);
				}
				else {
					keyCache.put(syncKey, obj.getId());
					notSyncedObjects.add(obj.getId());
				}
			}
		}

		log.info("Loaded " + keyCache.size() + " existing objects with sync keys");
	}

	/**
	 * Synchronizes an object
	 * @param syncKey the sync key
	 * @param incoming the object
	 */
	protected void synchronizeObject(MetadataDeployService deployService, Object syncKey, T incoming) {
		// Look in the cache for an existing object with this sync key
		Integer existingId = keyCache.get(syncKey);
		T existing = existingId != null ? fetchExistingById(existingId) : null;

		if (existing == null) {
			// Save incoming as new
			deployService.saveObject(incoming);
			keyCache.put(syncKey, incoming.getId());

			log.info("Created new object '" + incoming.getName() + "' with sync key " + syncKey);
			result.getCreated().add(incoming);
		}
		else {
			// Compute hashes of incoming and existing locations
			String incomingHash = getObjectHash(incoming);
			String existingHash = getObjectHash(existing);

			// Only update if hashes are different
			if (!incomingHash.equals(existingHash)) {
				deployService.overwriteObject(incoming, existing);

				log.info("Updated existing object '" + existing.getName() + "' with sync key " + syncKey);
				result.getUpdated().add(existing);
			}

			notSyncedObjects.remove(existing.getId());
		}
	}

	/**
	 * Retires existing objects not found in the source
	 */
	protected void retireExistingNotInSource(MetadataDeployService deployService) {
		// Retire objects that weren't in the sync source
		for (Integer notSyncedId : notSyncedObjects) {
			T notSynced = fetchExistingById(notSyncedId);
			if (!notSynced.isRetired()) {
				deployService.uninstallObject(notSynced, "Not found in sync source");

				log.info("Retired existing object '" + notSynced.getName() + "'");
				result.getRetired().add(notSynced);
			}
		}
	}

	/**
	 * Fetches all existing objects
	 * @return the existing objects
	 */
	protected abstract List<T> fetchAllExisting();

	/**
	 * Fetches an existing object by its id
	 * @param id the object id
	 * @return the existing object
	 */
	protected abstract T fetchExistingById(int id);

	/**
	 * Gets the synchronization key of the given object
	 * @param obj the object
	 * @return the synchronization key
	 */
	protected abstract Object getObjectSyncKey(T obj);

	/**
	 * Gets the hash of the given object
	 * @param obj the object
	 * @return the hash
	 */
	protected abstract String getObjectHash(T obj);
}