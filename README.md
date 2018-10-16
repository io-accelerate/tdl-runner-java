# tdl-runner-java


## 1. Requirements

- `Java 1.8`

## 2. Import project

### Eclipse

#### Maven project

Importing an existing Maven project into Eclipse
- Open Eclipse and close any existing project
- Select the File > Import menu option
- From the Import dialog, select Maven > Existing Maven Project
- Navigate till you get a dialog with the Browse button, Click on Browse
- Navigate to your Maven project folder and select the top-level folder
- The `pom.xml` file of the project should be automatically select and displayed
- Navigate further by clicking on Next (if visible), till you arrive at Finish
- Click Finish...

See detailed help [here](http://www.vogella.com/tutorials/EclipseMaven/article.html)

#### Gradle project

Importing an existing Gradle project into Eclipse
- Open Eclipse and close any existing project
- Select the File > Import menu option
- From the Import dialog, select Gradle > Existing Gradle Project
- Navigate till you get a dialog with the Browse button, Click on Browse 
- Navigate to your Gradle project folder and select the top-level folder...
- Keep clicking Next
- On the final screen Gradle wrapper related details are shown along with the Finish button 
- Click Finish...

See detailed help [here](http://www.vogella.com/tutorials/EclipseGradle/article.html)

### IntelliJ

#### Maven project

Importing an existing Maven project into IntelliJ
- Open IntelliJ IDEA and close any existing project
- From the Welcome screen, click Import Project...
- Navigate to your Maven project (`pom.xml` file) and select the top-level folder...
- Click OK...

See [How to import post from JetBrains](https://blog.jetbrains.com/idea/2008/03/opening-maven-projects-is-easy-as-pie/)

#### Gradle project

Importing an existing Gradle project into IntelliJ
- Open IntelliJ IDEA and close any existing project
- From the Welcome screen, click Import Project...
- Navigate to your Gradle project (`build.gradle` file) and select the top-level folder...
- Click OK...

See also [Importing a project from a Gradle model](https://www.jetbrains.com/help/idea/gradle.html#gradle_import)

### Other IDEs

You have to import and run the respective Maven project (`pom.xml` file) or Gradle project (`build.gradle` file) files into your IDE, depending on the support for these types of build systems in your IDE. 

## 3. How to start

- Open `src/main/java/befaster/SendCommandToServer.java`
- Read the comments as documentation, they will guide through the rest of the setup
