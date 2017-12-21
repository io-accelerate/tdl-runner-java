package befaster;

import befaster.runner.ConfigNotFoundException;
import befaster.runner.ConsoleOutSystem;
import befaster.runner.UserInputAction;
import befaster.solutions.Checkout;
import befaster.solutions.FizzBuzz;
import befaster.solutions.Hello;
import befaster.solutions.Sum;
import tdl.client.audit.StdoutAuditStream;
import tdl.client.queue.QueueBasedImplementationRunner;
import tdl.client.runner.ActionProvider;
import tdl.client.runner.ChallengeSession;
import tdl.client.runner.ConsoleOut;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
     *   You are encouraged to change this project as you please:
     *        * You can use your preferred libraries.
     *        * You can use your own test framework.
     *        * You can change the file structure.
     *        * Anything really, provided that this file stays runnable.
     *
     **/
    public static void main(String[] args) throws ConfigNotFoundException {

        ConsoleOut consoleOut = new ConsoleOutSystem();
        QueueBasedImplementationRunner runner = new QueueBasedImplementationRunner.Builder()
                .setUniqueId(readFromConfigFile("tdl_username"))
                .setAuditStream(new StdoutAuditStream())
                .setHostname(readFromConfigFile("tdl_hostname"))
                .withSolutionFor("sum", p -> Sum.sum(asInt(p[0]), asInt(p[1])))
                .withSolutionFor("hello", p -> Hello.hello(p[0]))
                .withSolutionFor("fizz_buzz", p -> FizzBuzz.fizzBuzz(asInt(p[0])))
                .withSolutionFor("checkout", p -> Checkout.checkout(p[0]))
                .create();

        ChallengeSession.forUsername(readFromConfigFile("tdl_username"))
                .withServerHostname(readFromConfigFile("tdl_hostname"))
                .withJourneyId(readFromConfigFile("tdl_journey_id"))
                .withColours(Boolean.parseBoolean(readFromConfigFile("tdl_use_coloured_output", "true")))
                .withRecordingSystemOn(Boolean.parseBoolean(readFromConfigFile("tdl_require_rec", "true")))
                .withPort(Integer.parseInt(readFromConfigFile("tdl_port")))
                .withConsoleOut(consoleOut)
                .withActionProvider(new UserInputAction(args))
                .withImplementationRunner(runner)
                .start();
    }

}
