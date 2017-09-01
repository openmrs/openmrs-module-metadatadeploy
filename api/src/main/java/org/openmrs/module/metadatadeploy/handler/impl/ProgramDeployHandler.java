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

package org.openmrs.module.metadatadeploy.handler.impl;

import org.openmrs.OpenmrsObject;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.ObjectUtils;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Deployment handler for programs
 */
@Handler(supports = { Program.class })
public class ProgramDeployHandler extends AbstractObjectDeployHandler<Program> {

	@Autowired
	@Qualifier("programWorkflowService")
	private ProgramWorkflowService programService;

	private Map<Class, Set<String>> excludeFields;

	public ProgramDeployHandler() {
		super();
		excludeFields = new HashMap<Class, Set<String>>();
		excludeFields.put(Program.class, new HashSet<String>(Arrays.asList(
				"programId", "allWorkflows", "descriptions", "conceptMappings"
		)));
		excludeFields.put(ProgramWorkflow.class, new HashSet<String>(Arrays.asList(
				"programWorkflowId", "program", "states"
		)));
		excludeFields.put(ProgramWorkflowState.class, new HashSet<String>(Arrays.asList(
				"programWorkflowStateId", "programWorkflow"
		)));
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#fetch(String)
	 */
	@Override
	public Program fetch(String uuid) {
		return programService.getProgramByUuid(uuid);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#save(org.openmrs.OpenmrsObject)
	 */
	@Override
	public Program save(Program obj) {
		return programService.saveProgram(obj);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#findAlternateMatch(org.openmrs.OpenmrsObject)
	 */
	@Override
	public Program findAlternateMatch(Program incoming) {
		// In 1.9.x getProgramByName incorrectly looks at concept name (TRUNK-3504)
		for (Program p : programService.getAllPrograms(true)) {
			if (p.getName().equals(incoming.getName())) {
				return p;
			}
		}
		return null;
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.handler.ObjectDeployHandler#uninstall(org.openmrs.OpenmrsObject, String)
	 * @param obj the object to uninstall
	 */
	@Override
	public void uninstall(Program obj, String reason) {
		// Because of TRUNK-4160, we can't just call retireProgram
		obj.setRetired(true);
		obj.setRetiredBy(Context.getAuthenticatedUser());
		obj.setRetireReason(reason);
		obj.setDateRetired(new Date());

		for (ProgramWorkflow workflow : obj.getWorkflows()) {
			workflow.setRetired(true);
			for (ProgramWorkflowState state : workflow.getStates()) {
				state.setRetired(true);
			}
		}

		programService.saveProgram(obj);
	}

	@Override
	public void overwrite(Program incoming, Program existing) {
		ObjectUtils.overwrite(incoming, existing, excludeFields.get(Program.class));
		mergeCollection(existing.getAllWorkflows(), incoming.getAllWorkflows(), excludeFields.get(ProgramWorkflow.class));
	}

	private <T extends OpenmrsObject> void mergeCollection(Collection<T> existing, Collection<T> incoming, Set<String> fieldsToExclude) {

		Set<T> handled = new HashSet<T>();
		Set<T> incomingToAdd = new HashSet<T>();
		for (T incomingItem : incoming) {
			T existingItem = findExisting(existing, incomingItem);
			if (existingItem == null) {
				incomingToAdd.add(incomingItem);
			} else {
				ObjectUtils.overwrite(incomingItem, existingItem, fieldsToExclude);
				if (incomingItem instanceof ProgramWorkflow) {
					mergeCollection(((ProgramWorkflow) existingItem).getStates(), ((ProgramWorkflow) incomingItem).getStates(), excludeFields.get(ProgramWorkflowState.class));
				}

				handled.add(existingItem);
			}
		}

		for (Iterator<T> iter = existing.iterator(); iter.hasNext(); ) {
			T existingItem = iter.next();
			if (!handled.contains(existingItem)) {
				if (existingItem instanceof ProgramWorkflow) {
					for (ProgramWorkflowState state : ((ProgramWorkflow) existingItem).getStates()) {
						voidOrRetire(state);
					}
				}
				voidOrRetire(existingItem);
			}
		}

		for (T incomingItem : incomingToAdd) {
			existing.add(incomingItem);
		}
	}

}