package befaster.runner;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tdl.client.Client;
import tdl.client.ProcessingRules;
import tdl.client.abstractions.UserImplementation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static befaster.runner.ChallengeServerClient.DEPLOY_ENDPOINT;
import static befaster.runner.CredentialsConfigFile.readFromConfigFile;
import static befaster.runner.RoundManagement.saveDescription;
import static befaster.runner.RunnerAction.getNewRoundDescription;
import static tdl.client.actions.ClientActions.publish;

public class ClientRunner {
    private final Logger LOG = LoggerFactory.getLogger(ClientRunner.class);
    private String hostname;
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


    public void start(String[] args) {
        if (!RecordingSystem.isRecordingSystemOk()) {
            System.out.println("Please run `record_screen_and_upload` before continuing.");
            return;
        }
        System.out.println("Connecting to " + hostname);

        if (useExperimentalFeature()) {
            executeServerActionFromUserInput(args);
        } else {
            executeRunnerActionFromArgs(args);
        }

    }

    //~~~~~~~~ Runner Actions ~~~~~~~~~

    private void executeRunnerActionFromArgs(String[] args) {
        RunnerAction runnerAction = extractActionFrom(args).orElse(defaultRunnerAction);
        executeRunnerAction(runnerAction);
    }

    private static Optional<RunnerAction> extractActionFrom(String[] args) {
        String firstArg = args.length > 0 ? args[0] : null;
        return extractActionFrom(firstArg);
    }

    private static Optional<RunnerAction> extractActionFrom(String firstArg) {
        return Arrays.stream(RunnerAction.values())
                .filter(runnerAction -> runnerAction.name().equalsIgnoreCase(firstArg))
                .findFirst();
    }

    private void executeRunnerAction(RunnerAction runnerAction) {
        System.out.println("Chosen action is: " + runnerAction.name());

        Client client = new Client.Builder()
                .setHostname(hostname)
                .setUniqueId(username)
                .create();

        ProcessingRules processingRules = new ProcessingRules();
        processingRules
                .on("display_description")
                .call(p -> saveDescription(p[0], p[1]))
                .then(publish());

        solutions.forEach((methodName, userImplementation) -> processingRules
                .on(methodName)
                .call(userImplementation)
                .then(runnerAction.getClientAction()));

        client.goLiveWith(processingRules);
        RecordingSystem.notifyEvent(RoundManagement.getLastFetchedRound(), runnerAction.getShortName());
    }

    //~~~~~~~~ Server Actions ~~~~~~~~~


    private void executeServerActionFromUserInput(String[] args) {
        ChallengeServerClient challengeServerClient;
        try {
            challengeServerClient = startUpChallengeServerClient();
        } catch (ConfigNotFoundException e) {
            LOG.error("Cannot find tdl_journey_id, needed to communicate with the server. Add this to the credentials.config.", e);
            return;
        }

        try {
            String journeyProgress = challengeServerClient.getJourneyProgress();
            System.out.println(journeyProgress);

            String availableActions = challengeServerClient.getAvailableActions();
            System.out.println(availableActions);

            if (availableActions.contains("No actions available.")) {
                LOG.debug("No available challenges from server");
                return;
            }

            String userInput = getUserInput(args);

            if (userInput.equals(DEPLOY_ENDPOINT)) {
                // DEBT - the RecordingSystem.notifyEvent happens in executeRunnerAction, but once we migrate form the legacy system, we should move it outside for clarity
                RunnerAction runnerAction = RunnerAction.deployToProduction;
                executeRunnerAction(runnerAction);
            }

            String response = challengeServerClient.sendAction(userInput);
            System.out.println(response);

            String responseString = challengeServerClient.getRoundDescription();
            RoundManagement.saveDescription(
                    responseString,
                    lastFetchedRound -> RecordingSystem.notifyEvent(lastFetchedRound, getNewRoundDescription.getShortName())
            );

        } catch (UnsupportedEncodingException e) {
            LOG.error("Could not encode the URL - badly formed URL?", e);
        } catch (IOException e) {
            LOG.error("Could not read user input.", e);
        } catch (UnirestException e) {
            LOG.error("Something went wrong with communicating with the server. Try again.", e);
        } catch (ChallengeServerClient.ServerErrorException e) {
            LOG.error("Server experienced an error. Try again.", e);
        } catch (ChallengeServerClient.OtherServerException e) {
            LOG.error("Client threw an unexpected error.", e);
        } catch (ChallengeServerClient.ClientErrorException e) {
            LOG.error("The client sent something the server didn't expect.");
            System.out.println(e.getResponseMessage());
        }
    }

    private String getUserInput(String[] args) throws IOException {
        Optional<String> gradleInput = readFromGradleArgs(args);
        String userInput;
        if (gradleInput.isPresent()) {
            userInput = gradleInput.get();
        } else {
            userInput = readUserInput();
        }
        return userInput;
    }

    private static Optional<String> readFromGradleArgs(String[] args) {
        return args.length > 0 ? Optional.of(args[0]) : Optional.empty();
    }

    private String readUserInput() throws IOException {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        return buffer.readLine().trim();
    }

    private ChallengeServerClient startUpChallengeServerClient() throws ConfigNotFoundException {
        String journeyId = readFromConfigFile("tdl_journey_id");
        boolean useColours = Boolean.parseBoolean(readFromConfigFile("tdl_use_coloured_output", "true"));
        return new ChallengeServerClient(hostname, journeyId, useColours);
    }


    private boolean useExperimentalFeature() {
        return Boolean.parseBoolean(readFromConfigFile("tdl_enable_experimental", "false"));
    }
}
