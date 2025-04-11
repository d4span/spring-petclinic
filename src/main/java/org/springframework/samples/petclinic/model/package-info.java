/**
 * The shared model components used across all modules. This package is considered an
 * "internal" component that can be accessed by all modules.
 */
@org.springframework.modulith.ApplicationModule(allowedDependencies = "util",
		type = org.springframework.modulith.ApplicationModule.Type.OPEN)
@org.springframework.modulith.NamedInterface(name = "SharedModel")
package org.springframework.samples.petclinic.model;
