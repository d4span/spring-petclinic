package org.example;

import java.io.File;
import java.util.HashMap;

import com.structurizr.Workspace;
import com.structurizr.component.ComponentFinderBuilder;
import com.structurizr.component.ComponentFinderStrategy;
import com.structurizr.component.ComponentFinderStrategyBuilder;
import com.structurizr.component.Type;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.CreateImpliedRelationshipsUnlessSameRelationshipExistsStrategy;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.Tags;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.Shape;
import com.structurizr.view.Styles;
import com.structurizr.view.ViewSet;

/** A simple example that creates a Structurizr model for the Spring PetClinic application. */
public class PetClinicStructurizrModel {

  public static void main(String[] args) throws Exception {
    Workspace workspace =
        new Workspace("Spring PetClinic", "A model of the Spring PetClinic application");
    Model model = workspace.getModel();
    model.setImpliedRelationshipsStrategy(
        new CreateImpliedRelationshipsUnlessSameRelationshipExistsStrategy());
    ViewSet views = workspace.getViews();

    Person user = model.addPerson("User", "A user who uses the system");

    SoftwareSystem petClinic =
        model.addSoftwareSystem(
            "Spring PetClinic", "Allows to view and manage pet information and visits");

    Container webApplication =
        petClinic.addContainer(
            "Web Application",
            "The web application providing the user interface",
            "Spring Boot, Spring MVC, Thymeleaf");

    Container database =
        petClinic.addContainer(
            "Database", "Stores pet, owner, vet, and visit information", "H2 / MySQL / PostgreSQL");

    webApplication.uses(database, "");

    var types = new HashMap<String, Type>();

    ComponentFinderStrategy componentFinderStrategy =
        new ComponentFinderStrategyBuilder()
            .matchedBy(
                type -> {
                  return type.getFullyQualifiedName().contains("petclinic");
                })
            .withName(
                type -> {
                  types.put(type.getName(), type);
                  return type.getName();
                })
            .forEach(
                component -> {
                  var type = types.get(component.getName());

                  component.addTags("class");
                  component.setGroup(type.getPackageName());

                  var packageComponent = webApplication.getComponentWithName(type.getPackageName());
                  if (packageComponent == null) {
                    packageComponent =
                        webApplication.addComponent(type.getPackageName(), "", "Java Package");
                    if (component.getName().contains("Repository")) {
                      packageComponent.uses(database, "");
                    }

                    if (component.getName().contains("Controller")) {
                      user.uses(packageComponent, "uses");
                    }
                  }

                  packageComponent.addTags("package");

                  addRelationshipsBetweenPackages(
                      component, types, webApplication, packageComponent);
                })
            .build();

    var componentFinder =
        new ComponentFinderBuilder()
            .forContainer(webApplication)
            .fromClasses(new File("../target/spring-petclinic-3.4.0-SNAPSHOT.jar"))
            .withStrategy(componentFinderStrategy)
            .build();

    componentFinder.run();

    // Create views
    var contextView = views.createSystemContextView(petClinic, "System Context", "");
    contextView.addAllSoftwareSystems();
    contextView.addAllPeople();

    var containerView = views.createContainerView(petClinic, "Containers", "");
    containerView.addAllPeople();
    containerView.addAllSoftwareSystems();
    containerView.addAllContainers();

    var packages =
        webApplication.getComponents().stream()
            .filter(component -> component.getTags().contains("package"))
            .toList();
    var packagesView = views.createComponentView(webApplication, "Packages", "");
    packages.forEach(
        c -> {
          packagesView.add(c);
        });
    packagesView.addAllPeople();
    packagesView.addAllContainers();

    // var classes =
    //     webApplication.getComponents().stream()
    //         .filter(component -> component.getTags().contains("class"))
    //         .toList();
    // var classesView = views.createComponentView(webApplication, "Classes", "");
    // classes.forEach(c -> classesView.add(c));

    Styles styles = views.getConfiguration().getStyles();
    styles.addElementStyle(Tags.SOFTWARE_SYSTEM).background("#1168bd").color("#ffffff");
    styles.addElementStyle(Tags.PERSON).background("#08427b").color("#ffffff").shape(Shape.Person);

    WorkspaceUtils.saveWorkspaceToJson(workspace, new File("workspace.json"));
  }

  private static void addRelationshipsBetweenPackages(
      Component component,
      HashMap<String, Type> types,
      Container webApplication,
      Component packageComponent) {
    component
        .getRelationships()
        .forEach(
            relationship -> {
              var destination = relationship.getDestination();
              var destinationType = types.get(destination.getName());

              if (destinationType == null
                  || destinationType.getPackageName().equals(packageComponent.getName())) {
                return;
              }

              var destinationPackage =
                  webApplication.getComponentWithName(destinationType.getPackageName());
              if (destinationPackage == null) {
                destinationPackage =
                    webApplication.addComponent(
                        destinationType.getPackageName(), "", "Java Package");
                destinationPackage.addTags("package");
              }
              packageComponent.uses(destinationPackage, "uses");
            });
  }
}
