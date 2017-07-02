package befaster;

import befaster.runner.ClientRunner;
import befaster.runner.ConfigNotFoundException;
import befaster.runner.RunnerAction;

import static befaster.runner.CredentialsConfigFile.readFromConfigFile;

public class BeFasterApp {
    /**
     * ~~~~~~~~~~ The workflow ~~~~~~~~~~~~~
     *
     *   +------+-----------------------------------------+-----------------------------------------------+
     *   | Step |          IDE                            |         Web console                           |
     *   +------+-----------------------------------------+-----------------------------------------------+
     *   |  1.  |                                         | Open your browser and go to:                  |
     *   |      |                                         |    http://run.befaster.io:8111                |
     *   |  2.  |                                         | Configure your email                          |
     *   |  3.  |                                         | Start a challenge, should display "Started"   |
     *   |  4.  | Set the email variable                  |                                               |
     *   |  5.  | Run "getNewRoundDescription"            |                                               |
     *   |  6.  | Read description from ./challenges      |                                               |
     *   |  7.  | Implement the required method in        |                                               |
     *   |      |   ./src/main/java/befaster/solutions    |                                               |
     *   |  8.  | Run "testConnectivity", observe output  |                                               |
     *   |  9.  | If ready, run "deployToProduction"      |                                               |
     *   | 10.  |                                         | Type "done"                                   |
     *   | 11.  |                                         | Check failed requests                         |
     *   | 12.  |                                         | Go to step 5.                                 |
     *   +------+-----------------------------------------+-----------------------------------------------+
     *
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
     **/
    public static void main(String[] args) throws ConfigNotFoundException {
        ClientRunner.forUsername(readFromConfigFile("tdl_username"))
                .withServerHostname("run.befaster.io")
                .withActionIfNoArgs(RunnerAction.testConnectivity)
                .start(args);
    }

}
