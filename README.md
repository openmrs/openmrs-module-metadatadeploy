Metadata Deploy OpenMRS Module
===========================

Overview
--------
This module provides a fast and simple mechanism for deploying metadata. Metadata objects are described in code in
"bundle" classes. Bundles can "require" other bundles, and the deploy service will automatically install required
bundles first. A simple of example of a bundle looks like:

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

This bundle could be installed (with all other bundles) during module startup:

	MetadataDeployService svc = Context.getService(MetadataDeployService.class);
	svc.installBundles(Context.getRegisteredComponents(MetadataBundle.class));

Or used on it's own in a unit test:

	@Autowired
	MyMetadata myMetadata;

	@Before
	public void setup() throws Exception {
		myMetadata.install();
	}