# tdl-runner-java


## 1. Requirements

- `Java 1.8`

## 2. How to start

- Open `src/main/java/befaster/SendCommandToServer.java`
- Read the comments as documentation, they will guide through the rest of the setup




---

## Detailed notes on importing a project

You have to import and run the respective Maven project (`pom.xml` file)
or Gradle project (`build.gradle` file) files into your IDE,
depending on the support for these types of build files.

### IntelliJ

IntelliJ comes with build-in support for both Gradle and Maven.
You just need to import the appropriate file.

#### Gradle project

Importing an existing Gradle project into IntelliJ
- From the Welcome screen, click Import Project...
- Navigate to your Gradle project (`build.gradle` file) and select the top-level folder...
- Click OK...

See also [Importing a project from a Gradle model](https://www.jetbrains.com/help/idea/gradle.html#gradle_import)

#### Maven project

Importing an existing Maven project into IntelliJ
- From the Welcome screen, click Import Project...
- Navigate to your Maven project (`pom.xml` file) and select the top-level folder...
- Click OK...

See [How to import post from JetBrains](https://blog.jetbrains.com/idea/2008/03/opening-maven-projects-is-easy-as-pie/)


### Eclipse

#### Colourised Console plugin

Eclipse supports a number of plugins to enable colourised Console text, we suggest you install `ANSI Console plugin`.

#### Gradle project

Importing an existing Gradle project into Eclipse
- Select the File > Import menu option, select Gradle > Existing Gradle Project
- Navigate to your Gradle project folder and select the top-level folder...

See also  [here](http://www.vogella.com/tutorials/EclipseGradle/article.html)

#### Maven project

Importing an existing Maven project into Eclipse
- Select the File > Import menu option, select Maven > Existing Maven Project
- Navigate to your Maven project folder and select the top-level folder
- The `pom.xml` file of the project should be automatically select and displayed

See also  [here](http://www.vogella.com/tutorials/EclipseMaven/article.html)


### Other IDEs

IDEs normally come with support for Maven or Gradle. If not, you might have to research how it is done for your particular IDE.
