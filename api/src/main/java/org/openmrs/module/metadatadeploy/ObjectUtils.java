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

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import org.hibernate.Hibernate;
import org.openmrs.OpenmrsObject;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

/**
 * Utility methods for OpenmrsObjects
 */
public class ObjectUtils {

	final static PureJavaReflectionProvider reflector = new PureJavaReflectionProvider();

	/**
	 * Copies an object into another of the same class
	 * @param source the source object
	 * @param target the target object
	 * @param excludeFields the names of fields to be excluded
	 * @param <T> the class of both objects
	 */
	public static <T extends OpenmrsObject> void copy(final T source, final T target, final Set<String> excludeFields) {

		ensureInitialized(target); // TODO figure out if this is necessary

		reflector.visitSerializableFields(source, new ReflectionProvider.Visitor() {

			/**
			 * @see ReflectionProvider#visitSerializableFields(Object, com.thoughtworks.xstream.converters.reflection.ReflectionProvider.Visitor)
			 */
			@Override
			public void visit(String fieldName, Class type, Class definedIn, Object value) {

				// Check if field should be excluded from operation
				if (excludeFields != null && excludeFields.contains(fieldName)) {
					return;
				}

				if (Collection.class.isAssignableFrom(type)) {
					Collection sourceCollection = (Collection) value;
					Collection targetCollection = (Collection) readField(target, fieldName, definedIn);

					if (sourceCollection != null) {
						for (Object itemInSourceCollection : sourceCollection) {
							if (itemInSourceCollection instanceof OpenmrsObject) {
								updateBackReferences(itemInSourceCollection, source, target);
							}
						}
					}

					// This collection might be Hibernate managed in which case we can't just replace it
					if (targetCollection != null) {
						targetCollection.clear();

						if (sourceCollection != null) {
							targetCollection.addAll(sourceCollection);
						}
					}
					else {
						reflector.writeField(target, fieldName, value, definedIn);
					}
				} else {
					if (value instanceof OpenmrsObject) {
						updateBackReferences(value, source, target);
					}

					reflector.writeField(target, fieldName, value, definedIn);
				}
			}
		});
	}

	/**
	 * A field of the source object might be have it's own field which references the source object. For example
	 * an attribute on an OpenmrsObject has a field called owner which references the owning object. This method looks
	 * for references to the source object and updates them to reference the target object.
	 * @param obj the object
	 * @param source the source object
	 * @param target the target object
	 * @param <T> the source and target object class
	 */
	protected static <T extends OpenmrsObject> void updateBackReferences(final Object obj, final T source, final T target) {
		ensureInitialized(obj);

		reflector.visitSerializableFields(obj, new ReflectionProvider.Visitor() {
			/**
			 * @see ReflectionProvider#visitSerializableFields(Object, com.thoughtworks.xstream.converters.reflection.ReflectionProvider.Visitor)
			 */
			@Override
			public void visit(String fieldName, Class type, Class definedIn, Object value) {
				if (value != null && value == source) {
					reflector.writeField(obj, fieldName, target, definedIn);
				}
			}
		});
	}

	/**
	 * Ensures that the given object is initialized if its a Hibernate proxy
	 * @param obj the object
	 */
	protected static void ensureInitialized(Object obj) {
		if (!Hibernate.isInitialized(obj)) {
			Hibernate.initialize(obj);
		}
	}

	/**
	 * Reads the value of a field from the given object
	 * @param object the object
	 * @param fieldName the name of the field
	 * @param definedIn the class in the objects hierarchy where the field was defined
	 * @return the field value
	 */
	protected static Object readField(Object object, String fieldName, Class<?> definedIn) {
		Field field = reflector.getField(definedIn, fieldName);
		field.setAccessible(true);
		Object result;
		try {
			result = field.get(object);
		}
		catch (IllegalAccessException e) {
			throw new ObjectAccessException("Cannot access field " + object.getClass().getName() + "." + field.getName());
		}
		return result;
	}

	/**
	 * Checks if an object uses the the standard id property
	 * @param obj the object
	 * @return true if it uses id
	 */
	public static boolean usesId(OpenmrsObject obj) {
		try {
			obj.getId();
			return true;
		}
		catch (UnsupportedOperationException ex) {
			return false;
		}
	}
}