package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class SpringModulithTest {

	ApplicationModules modules = ApplicationModules.of(PetClinicApplication.class);

	@Test
	void verifyModularity() {
		modules.verify();
	}

	@Test
	void writeDocumentationSnippets() {
		new Documenter(modules).writeModulesAsPlantUml()
			.writeIndividualModulesAsPlantUml()
			.writeModuleCanvases()
			.writeAggregatingDocument();
	}

}
