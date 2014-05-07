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
import java.util.Map;
import java.util.Set;

/**
 * Class which runs synchronizations of metadata objects
 */
public class MetadataSynchronizationRunner<T extends OpenmrsMetadata> {

	protected static final Log log = LogFactory.getLog(MetadataSynchronizationRunner.class);

	protected ObjectSource<T> source;

	protected ObjectSynchronization<T> sync;

	protected SyncResult<T> result = new SyncResult<T>();

	// Cache of sync keys to objects to avoid re-fetching objects from database
	protected Map<Object, T> keyCache = new HashMap<Object, T>();

	// After sync this will contain all existing items that weren't in the source. This is a map by id rather than just
	// a set because object equality is based on UUIDs and those can change during a sync
	protected Map<Integer, T> notSyncedObjects = new HashMap<Integer, T>();

	/**
	 * Creates a new synchronization process
	 * @param source the object source
	 * @param sync the synchronization
	 */
	public MetadataSynchronizationRunner(ObjectSource<T> source, ObjectSynchronization<T> sync) {
		this.source = source;
		this.sync = sync;
	}

	/**
	 * Performs the synchronization
	 */
	public SyncResult<T> run() {
		MetadataDeployService deployService = Context.getService(MetadataDeployService.class);

		initializeCache();

		try {
			T next;

			while ((next = source.fetchNext()) != null) {
				Object syncKey = sync.getObjectSyncKey(next);

				if (syncKey == null) {
					throw new RuntimeException("Incoming object '" + next.getName() + "' has no sync key");
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
		for (T obj : sync.fetchAllExisting()) {
			Object syncKey = sync.getObjectSyncKey(obj);

			if (syncKey == null) {
				log.warn("Ignoring object '" + obj.getName() + "' with no sync key");
			}
			else {
				// Check there isn't another object with this key
				if (keyCache.containsKey(syncKey)) {
					log.warn("Ignoring object '" + obj.getName() + "' with duplicate sync key " + syncKey);
				}
				else {
					keyCache.put(syncKey, obj);
					notSyncedObjects.put(obj.getId(), obj);
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
		T existing = keyCache.get(syncKey);

		if (existing == null) {
			// Save incoming as new
			deployService.saveObject(incoming);
			keyCache.put(syncKey, incoming);

			log.info("Created new object '" + incoming.getName() + "' with sync key " + syncKey);
			result.getCreated().add(incoming);
		}
		else {
			// Only if incoming object differs
			if (sync.updateRequired(incoming, existing)) {
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
		for (T notSynced : notSyncedObjects.values()) {
			if (!notSynced.isRetired()) {
				deployService.uninstallObject(notSynced, "Not found in sync source");

				log.info("Retired existing object '" + notSynced.getName() + "'");
				result.getRetired().add(notSynced);
			}
		}
	}
}