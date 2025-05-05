package org.springframework.samples.petclinic;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.plantuml.rules.PlantUmlArchCondition;
import com.tngtech.archunit.library.plantuml.rules.PlantUmlArchCondition.Configuration;

class ArchitectureTest {

  @Test
  void testArchitecture() {
    var classes =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("org.springframework.samples.petclinic");

    var architectureDiagram = getClass().getClassLoader().getResource("architecture.puml");

    classes()
        .should(
            PlantUmlArchCondition.adhereToPlantUmlDiagram(
                architectureDiagram,
                Configuration.consideringOnlyDependenciesInAnyPackage(
                    "org.springframework.samples.petclinic..")))
        .check(classes);
  }
}
