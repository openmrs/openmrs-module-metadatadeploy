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

package org.openmrs.module.metadatadeploy.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler;
import org.openmrs.module.metadatadeploy.handler.ObjectDeployHandlers;
import org.openmrs.module.metadatadeploy.source.ObjectSource;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportMode;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of the metadata deploy service
 */
public class MetadataDeployServiceImpl extends BaseOpenmrsService implements MetadataDeployService {

	protected static final Log log = LogFactory.getLog(MetadataDeployServiceImpl.class);

    
    
	/**
	 * @see MetadataDeployService#installBundles(java.util.Collection)
	 */
	@Override
	public void installBundles(Collection<MetadataBundle> bundles) throws APIException {
		// Organize into map by class
		Map<Class<? extends MetadataBundle>, MetadataBundle> all = new HashMap<Class<? extends MetadataBundle>, MetadataBundle>();
		for (MetadataBundle bundle : bundles) {
			all.put(bundle.getClass(), bundle);
		}

		// Begin recursive processing
		Set<MetadataBundle> installed = new HashSet<MetadataBundle>();
		for (MetadataBundle bundle : bundles) {
			installBundle(bundle, all, installed);
		}
	}

    public void installBundle(MetadataBundle bundle) throws APIException {

        Map<Class<? extends MetadataBundle>, MetadataBundle> all = new HashMap<Class<? extends MetadataBundle>, MetadataBundle>();
        for (MetadataBundle b: Context.getRegisteredComponents(MetadataBundle.class)) {
            all.put(b.getClass(), b);
        }

        Set<MetadataBundle> installed = new HashSet<MetadataBundle>();
        installBundle(bundle, all, installed);
    }

    /**
	 * Installs a metadata bundle by recursively installing it's required bundles
	 * @param bundle the bundle
	 * @param all the map of all bundles and their ids
	 * @param installed the set of previously installed bundles
	 */
	protected void installBundle(MetadataBundle bundle, Map<Class<? extends MetadataBundle>, MetadataBundle> all, Set<MetadataBundle> installed) throws APIException {
		// Return immediately if bundle has already been installed
		if (installed.contains(bundle)) {
			return;
		}

		try {
			// Install required bundles first
			Requires requires = bundle.getClass().getAnnotation(Requires.class);
			if (requires != null) {
				for (Class<? extends MetadataBundle> requiredClass : requires.value()) {
					MetadataBundle required = all.get(requiredClass);

					if (required == null) {
						throw new RuntimeException("Can't find required bundle class " + requiredClass + " for " + bundle.getClass());
					}

					installBundle(required, all, installed);
				}
			}

			bundle.install();
			installed.add(bundle);

			Context.flushSession();
		}
		catch (Exception ex) {
			throw new APIException("Unable to install bundle " + bundle.getClass().getSimpleName(), ex);
		}
	}

	/**
	 * @see MetadataDeployService#installPackage(String, ClassLoader, String)
	 */
	public boolean installPackage(String filename, ClassLoader loader, String groupUuid, ImportMode importMode) throws APIException {
		Matcher matcher = Pattern.compile("[\\w/-]+-(\\d+).zip").matcher(filename);
		if (!matcher.matches()) {
			throw new APIException("Filename must match PackageNameWithNoSpaces-X.zip");
		}

		Integer version = Integer.valueOf(matcher.group(1));

		ImportedPackage installed = Context.getService(MetadataSharingService.class).getImportedPackageByGroup(groupUuid);
		if (installed != null && installed.getVersion() >= version && installed.getDateImported() != null) {
			log.info("Metadata package " + filename + " is already installed with version " + installed.getVersion());
			return false;
		}

		if (loader.getResource(filename) == null) {
			throw new APIException("Cannot load " + filename + " for group " + groupUuid);
		}

		try {
			PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
			metadataImporter.setImportConfig(ImportConfig.valueOf(importMode));
			metadataImporter.loadSerializedPackageStream(loader.getResourceAsStream(filename));
			metadataImporter.importPackage();

			log.debug("Loaded metadata package '" + filename + "'");
			return true;

		} catch (Exception ex) {
			throw new APIException("Failed to install metadata package " + filename, ex);
		}
	}


    /**
     * @see MetadataDeployService#installPackage(String, ClassLoader, String)
     */
    public boolean installPackage(String filename, ClassLoader loader, String groupUuid) throws APIException {
        return installPackage(filename, loader, groupUuid, ImportMode.MIRROR);
    }

	/**
	 * @see MetadataDeployService#installObject(org.openmrs.OpenmrsObject)
	 */
	@Override
	public <T extends OpenmrsObject> T installObject(T incoming) {
		ObjectDeployHandler<T> handler = getHandler(incoming);

		// Get globally unique identifier
		String identifier = handler.getIdentifier(incoming);

		if (identifier == null) {
			throw new APIException("Can't install object with no identifier");
		}

		// Look for existing by primary identifier (i.e. exact match)
		T existing = handler.fetch(identifier);

		// If no exact match, look for another existing item that should be replaced
		if (existing == null) {
			existing = handler.findAlternateMatch(incoming);
		}

		if (existing != null) {
			handler.overwrite(incoming, existing);

			return handler.save(existing);
		}
		else {
			return handler.save(incoming);
		}
	}

	/**
	 * @see MetadataDeployService#installFromSource(org.openmrs.module.metadatadeploy.source.ObjectSource)
	 */
	@Override
	public <T extends OpenmrsObject> List<T> installFromSource(ObjectSource<T> source) throws APIException {
		List<T> installed = new ArrayList<T>();
		T incoming;

		try {
			while ((incoming = source.fetchNext()) != null) {
				installed.add(installObject(incoming));
			}
			return installed;
		}
		catch (Exception ex) {
			throw new APIException("Unable to install objects from " + source.getClass().getSimpleName());
		}
	}

	/**
	 * @see MetadataDeployService#uninstallObject(org.openmrs.OpenmrsObject, String)
	 */
	@Override
	public <T extends OpenmrsObject> void uninstallObject(T outgoing, String reason) {
		ObjectDeployHandler<T> handler = getHandler(outgoing);

		handler.uninstall(outgoing, reason);
	}

	/**
	 * @see MetadataDeployService#fetchObject(Class, String)
	 */
	@Override
	public <T extends OpenmrsObject> T fetchObject(Class<T> clazz, String identifier) {
		ObjectDeployHandler<T> handler = getHandler(clazz);
		return handler.fetch(identifier);
	}

	/**
	 * @see MetadataDeployService#saveObject(org.openmrs.OpenmrsObject)
	 */
	@Override
	public <T extends OpenmrsObject> T saveObject(T obj) {
		ObjectDeployHandler<T> handler = getHandler(obj);
		return handler.save(obj);
	}

	/**
	 * @see MetadataDeployService#overwriteObject(org.openmrs.OpenmrsObject, org.openmrs.OpenmrsObject)
	 */
	@Override
	public <T extends OpenmrsObject> void overwriteObject(T source, T target) {
		ObjectDeployHandler<T> handler = getHandler(source);

		handler.overwrite(source, target);
		handler.save(target);
	}

	/**
	 * Convenience method to get the handler for the given object
	 * @param obj the object
	 * @return the handler
	 * @throws RuntimeException if no suitable handler exists
	 */
	protected <T extends OpenmrsObject> ObjectDeployHandler<T> getHandler(T obj) throws RuntimeException {
		return getHandler((Class<T>) obj.getClass());
	}

	/**
	 * Convenience method to get the handler for the given object class
	 * @param clazz the object class
	 * @return the handler
	 * @throws RuntimeException if no suitable handler exists
	 */
	protected <T extends OpenmrsObject> ObjectDeployHandler<T> getHandler(Class<T> clazz) throws RuntimeException {
        // assumes there is only one ObjectDeployHandlers component
		ObjectDeployHandler<T> handler = Context.getRegisteredComponents(ObjectDeployHandlers.class).get(0).getHandlers().get(clazz);
		if (handler != null) {
			return handler;
		}
		if (clazz.getSimpleName().contains("_$$")) {
			return getHandler((Class<T>) clazz.getSuperclass());
		}

		throw new RuntimeException("No handler class found for " + clazz.getName());
	}
}
