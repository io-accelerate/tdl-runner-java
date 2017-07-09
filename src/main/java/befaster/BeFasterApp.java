package befaster;

import befaster.runner.ClientRunner;
import befaster.runner.ConfigNotFoundException;
import befaster.runner.RunnerAction;

import static befaster.runner.CredentialsConfigFile.readFromConfigFile;

public class BeFasterApp {
    /**
     * ~~~~~~~~~~ Running the system: ~~~~~~~~~~~~~
     *
     *   From command line:
     *      ./gradlew run -Daction=$ACTION
     *
     *   From IDE:
     *      Set the value of the `actionIfNoArgs`
     *      Run this file from the IDE.
     *
     *   Available actions:
     *        * getNewRoundDescription    - Get the round description (call once per round).
     *        * testConnectivity          - Test you can connect to the server (call any number of time)
     *        * deployToProduction        - Release your code. Real requests will be used to test your solution.
     *                                      If your solution is wrong you get a penalty of 10 minutes.
     *                                      After you fix the problem, you should deploy a new version into production.
     *
     *   To run your unit tests locally:
     *      ./gradlew test -i
     *
     * ~~~~~~~~~~ The workflow ~~~~~~~~~~~~~
     *
     *   +------+-----------------------------------------+-----------------------------------------------+
     *   | Step |          IDE                            |         Web console                           |
     *   +------+-----------------------------------------+-----------------------------------------------+
     *   |  1.  |                                         | Start a challenge, should display "Started"   |
     *   |  2.  | Run "getNewRoundDescription"            |                                               |
     *   |  3.  | Read description from ./challenges      |                                               |
     *   |  4.  | Implement the required method in        |                                               |
     *   |      |   ./src/main/java/befaster/solutions    |                                               |
     *   |  5.  | Run "testConnectivity", observe output  |                                               |
     *   |  6.  | If ready, run "deployToProduction"      |                                               |
     *   |  7.  |                                         | Type "done"                                   |
     *   |  8.  |                                         | Check failed requests                         |
     *   |  9.  |                                         | Go to step 2.                                 |
     *   +------+-----------------------------------------+-----------------------------------------------+
     *
     **/
    public static void main(String[] args) throws ConfigNotFoundException {
        ClientRunner.forUsername(readFromConfigFile("tdl_username"))
                .withServerHostname("run.befaster.io")
                .withActionIfNoArgs(RunnerAction.testConnectivity)
                .start(args);
    }

}
