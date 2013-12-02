Metadata Deploy OpenMRS Module
==============================

Overview
--------
This module provides a simple framework for deploying metadata. It enables the same metadata definitions to be easily and quickly deployed at both runtime and test-time. 

 * Guarantees consistency of metadata across installations. Distributions can know that the metadata they require is always present on start up, and is exactly as they expect it.
 * Provides mechanism for saving metadata objects which is significantly faster than loading metadata packages, and thus suitable for inclusion in unit tests.
 * Allows complete visibility of the metadata (i.e. nothing hidden in zip files or liquibase changesets).
 * Encourages a readable name-spaced pattern for identifying metadata objects, e.g. `TbMetadata._Form.TB_ENROLLMENT`.

Usage
-----
Metadata objects are described in "bundle" classes which are a logical grouping for related metadata. The metadata objects themselves can be defined in different ways:
 * By creating transient objects. The [CoreContructors](https://github.com/I-TECH/openmrs-module-metadatadeploy/blob/master/api/src/main/java/org/openmrs/module/metadatadeploy/bundle/CoreConstructors.java) class provides many convenient contructors for standard metadata classes but you can also write your own.
 * Via any custom logic (e.g. importing drugs from a spreadsheet).
 * By importing from metadata packages (slow so only recommended for data that can't be defined any other way)

Bundles can also have dependency relationships to other bundles, and the deploy service will automatically install required bundles first. A simple of example of a bundle looks like:

```java
@Component
@Requires({ BaseMetadata.class })
public class MyMetadata extends AbstractMetadataBundle {

	public static final class _EncounterType {
		public static final String ENCOUNTER_TYPE1 = "d3e3d723-7458-4b4e-8998-408e8a551a84";
	}

	public static final class _Form {
		public static final String FORM_TYPE1 = "4b296dd0-f6be-4007-9eb8-d0fd4e94fb3a";
		public static final String FORM_TYPE2 = "89994550-9939-40f3-afa6-173bce445c79";
	}

	public static final class _Package {
		public static final String LOCATIONS = "5856a8fc-7ebc-46e8-929c-5ae2c780ab54";
	}

	@Override
	public void install() {
		install(encounterType("Encounter Type #1", "Something...", _EncounterType.ENCOUNTER_TYPE1));

		install(form("Form #1", null, _EncounterType.ENCOUNTER_TYPE1, "1", _Form.FORM_TYPE1));
		install(form("Form #2", null, _EncounterType.ENCOUNTER_TYPE1, "1", _Form.FORM_TYPE2));

		install(packageFile("locations-1.zip", null, _Package.LOCATIONS));
	}
}
```

This bundle could be installed (with all other bundles) during module startup:

```java
MetadataDeployService svc = Context.getService(MetadataDeployService.class);
svc.installBundles(Context.getRegisteredComponents(MetadataBundle.class));
```

Or used on it's own in a unit test:

```java
@Autowired
MyMetadata myMetadata;

@Before
public void setup() throws Exception {
	myMetadata.install();
}
```
	
How it works
============
Every metadata object in the data model has an associated globally unique identifier. Most of the time this will be the UUID but there are exceptions, e.g. for global properties it is the property name. When a transient object is passed to the `install(...)` method, the module looks for the existing object with that identifier. If it exists, then it is completely overwritten with the object in the bundle. If not, then a new object is saved to the database with that identifier. 

The module knows how to handle almost all the standard OpenMRS metadata classes. You can easily include other classes by providing additional [handler classes](https://github.com/I-TECH/openmrs-module-metadatadeploy/tree/master/api/src/main/java/org/openmrs/module/metadatadeploy/handler/impl) in your own module.
