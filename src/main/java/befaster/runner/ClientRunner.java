package befaster.runner;

import com.mashape.unirest.http.exceptions.UnirestException;
import tdl.client.Client;
import tdl.client.ProcessingRules;
import tdl.client.abstractions.UserImplementation;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static befaster.runner.ChallengeServerClient.AVAILABLE_ACTIONS;
import static befaster.runner.ChallengeServerClient.JOURNEY_PROGRESS_ENDPOINT;
import static befaster.runner.CredentialsConfigFile.readFromConfigFile;
import static tdl.client.actions.ClientActions.publish;

public class ClientRunner {
    private String hostname;
    private String journeyId;
    private RunnerAction defaultRunnerAction;
    private final String username;
    private final Map<String, UserImplementation> solutions;

    public static ClientRunner forUsername(@SuppressWarnings("SameParameterValue") String username) {
        return new ClientRunner(username);
    }

    private ClientRunner(String username) {
        this.username = username;
        this.solutions = new HashMap<>();
    }

    public ClientRunner withServerHostname(@SuppressWarnings("SameParameterValue") String hostname) {
        this.hostname = hostname;
        return this;
    }

    public ClientRunner withActionIfNoArgs(RunnerAction actionIfNoArgs) {
        defaultRunnerAction = actionIfNoArgs;
        return this;
    }

    public ClientRunner withSolutionFor(String methodName, UserImplementation solution) {
        solutions.put(methodName, solution);
        return this;
    }

    public ClientRunner withJourneyId(String journeyId) {
        this.journeyId = journeyId;
        return this;
    }


    public void start(String[] args) {
        if(!isRecordingSystemOk()) {
            System.out.println("Please run `record_screen_and_upload` before continuing.");
            return;
        }

        ChallengeServerClient challengeServerClient = new ChallengeServerClient(hostname, journeyId, true);

        try {
            System.out.println(challengeServerClient.get(JOURNEY_PROGRESS_ENDPOINT));
            System.out.println(challengeServerClient.get(AVAILABLE_ACTIONS));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not encode the URL - badly formed URL?");
        } catch (UnirestException e) {
            e.printStackTrace();
            System.out.println("Something went wrong with communicating with the server. Try again.");
        }

        RunnerAction runnerAction = extractActionFrom(args).orElse(defaultRunnerAction);
        System.out.println("Chosen action is: "+runnerAction.name());

        Client client = new Client.Builder()
                .setHostname(hostname)
                .setUniqueId(username)
                .create();

        ProcessingRules processingRules = new ProcessingRules();
        processingRules
                .on("display_description")
                .call(p -> RoundManagement.displayAndSaveDescription(p[0], p[1]))
                .then(publish());

        solutions.forEach((methodName, userImplementation) -> processingRules
                        .on(methodName)
                        .call(userImplementation)
                        .then(runnerAction.getClientAction()));

        client.goLiveWith(processingRules);

        RecordingSystem.notifyEvent(RoundManagement.getLastFetchedRound(), runnerAction.getShortName());
    }

    private static Optional<RunnerAction> extractActionFrom(String[] args) {
        String firstArg = args.length > 0 ? args[0] : null;
        return Arrays.stream(RunnerAction.values())
                .filter(runnerAction -> runnerAction.name().equalsIgnoreCase(firstArg))
                .findFirst();
    }


    private boolean isRecordingSystemOk() {
        boolean requireRecording = Boolean.parseBoolean(readFromConfigFile("tdl_require_rec", "true"));

        //noinspection SimplifiableIfStatement
        if (requireRecording) {
            return RecordingSystem.isRunning();
        } else {
            return true;
        }
    }
}
