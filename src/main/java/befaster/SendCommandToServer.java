package befaster;

import befaster.runner.ClientRunner;
import befaster.runner.ConfigNotFoundException;
import befaster.runner.RunnerAction;
import befaster.solutions.Checkout;
import befaster.solutions.FizzBuzz;
import befaster.solutions.Hello;
import befaster.solutions.Sum;

import static befaster.runner.CredentialsConfigFile.readFromConfigFile;
import static befaster.runner.TypeConversion.asInt;

public class SendCommandToServer {
    /**
     * ~~~~~~~~~~ Running the system: ~~~~~~~~~~~~~
     *
     *   From IDE:
     *      Run this file from the IDE.
     *
     *   From command line:
     *      ./gradlew run
     *
     *   To run your unit tests locally:
     *      ./gradlew test -i
     *
     * ~~~~~~~~~~ The workflow ~~~~~~~~~~~~~
     *
     *   By running this file you interact with a challenge server.
     *   The interaction follows a request-response pattern:
     *        * You are presented with your current progress and a list of actions.
     *        * You trigger one of the actions by typing it on the console.
     *        * After the action feedback is presented, the execution will stop.
     *
     *   +------+-------------------------------------------------------------+
     *   | Step | The usual workflow                                          |
     *   +------+-------------------------------------------------------------+
     *   |  1.  | Run this file.                                              |
     *   |  2.  | Start a challenge by typing "start".                        |
     *   |  3.  | Read description from the "challenges" folder               |
     *   |  4.  | Implement the required method in                            |
     *   |      |   ./src/main/java/befaster/solutions                        |
     *   |  5.  | Deploy to production by typing "deploy".                    |
     *   |  6.  | Observe output, check for failed requests.                  |
     *   |  7.  | If passed, go to step 3.                                    |
     *   +------+-------------------------------------------------------------+
     *
     **/
    public static void main(String[] args) throws ConfigNotFoundException {
        ClientRunner.forUsername(readFromConfigFile("tdl_username"))
                .withServerHostname(readFromConfigFile("tdl_hostname"))
                .withActionIfNoArgs(RunnerAction.testConnectivity)
                .withSolutionFor("sum", p -> Sum.sum(asInt(p[0]), asInt(p[1])))
                .withSolutionFor("hello", p -> Hello.hello(p[0]))
                .withSolutionFor("fizz_buzz", p -> FizzBuzz.fizzBuzz(asInt(p[0])))
                .withSolutionFor("checkout", p -> Checkout.checkout(p[0]))
                .start(args);
    }

}
