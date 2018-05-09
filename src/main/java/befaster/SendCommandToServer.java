package befaster;

import befaster.runner.ConfigNotFoundException;
import befaster.runner.UserInputAction;
import tdl.client.queue.QueueBasedImplementationRunner;
import tdl.client.runner.ChallengeSession;

import static befaster.runner.Utils.getConfig;
import static befaster.runner.Utils.getRunnerConfig;

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
        EntryPointMapping entry = new EntryPointMapping();

        QueueBasedImplementationRunner runner = new QueueBasedImplementationRunner.Builder()
                .setConfig(getRunnerConfig())
                .withSolutionFor("sum", entry::sum)
                .withSolutionFor("hello", entry::hello)
                .withSolutionFor("fizz_buzz", entry::fizzBuzz)
                .withSolutionFor("checkout", entry::checkout)
                .create();

        ChallengeSession.forRunner(runner)
                .withConfig(getConfig())
                .withActionProvider(new UserInputAction(args))
                .start();
    }

}
