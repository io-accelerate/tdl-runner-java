package befaster.runner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tdl.client.Client;
import tdl.client.ProcessingRules;
import tdl.client.abstractions.UserImplementation;
import tdl.client.serialization.JsonRpcRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static befaster.runner.ChallengeServerClient.DONE_ENDPOINT;
import static befaster.runner.ChallengeServerClient.START_ENDPOINT;
import static befaster.runner.CredentialsConfigFile.readFromConfigFile;
import static befaster.runner.RoundManagement.displayAndSaveDescription;
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
        if(!isRecordingSystemOk()) {
            System.out.println("Please run `record_screen_and_upload` before continuing.");
            return;
        }

        if (useExperimentalFeature()) {
            readActionFromCommandLine();
        } else {
            readRunnerActionFromArgs(args);
        }

    }

    private void readActionFromCommandLine() {
        try {
            ChallengeServerClient challengeServerClient = startUpAndTestChallengeServerClient();

            BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
            String line = buffer.readLine().trim();

            readAndExecuteAction(line, challengeServerClient);
        } catch (UnsupportedEncodingException e) {
            LOG.error("Could not encode the URL - badly formed URL?", e);
        } catch (IOException e) {
            LOG.error("Could not read user input.", e);
        }  catch (UnirestException e) {
            LOG.error("Something went wrong with communicating with the server. Try again.", e);
        } catch (ConfigNotFoundException e) {
            LOG.error("Cannot find tdl_journey_id, needed to communicate with the server. Add this to the credentials.config.", e);
        } catch (ChallengeServerClient.ServerErrorException e) {
            LOG.error("Server experienced an error. Try again.", e);
        } catch (ChallengeServerClient.OtherServerException e) {
            LOG.error("Client threw an unexpected error.", e);
        } catch (ChallengeServerClient.ClientErrorException e) {
            LOG.error("The client sent something the server didn't expect.", e);
        }
    }

    private void readAndExecuteAction(String line, ChallengeServerClient challengeServerClient) throws IOException, UnirestException, ChallengeServerClient.ServerErrorException, ChallengeServerClient.ClientErrorException, ChallengeServerClient.OtherServerException {
        if (line.equals(DONE_ENDPOINT)) {
            executeRunnerAction(RunnerAction.deployToProduction);
        }

        String response = challengeServerClient.sendAction(line);
        System.out.println(response);
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();

        if (line.equals(START_ENDPOINT)) {
            String roundDescription = challengeServerClient.getRoundDescription();
            JsonRpcRequest jsonRpcRequest = gson.fromJson(roundDescription, JsonRpcRequest.class);
            displayAndSaveDescription(jsonRpcRequest.getParams()[0], jsonRpcRequest.getParams()[1]);
        }
    }

    private void readRunnerActionFromArgs(String[] args) {
        RunnerAction runnerAction = extractActionFrom(args).orElse(defaultRunnerAction);
        executeRunnerAction(runnerAction);
    }

    private void executeRunnerAction(RunnerAction runnerAction) {
        System.out.println("Chosen action is: "+runnerAction.name());

        Client client = new Client.Builder()
                .setHostname(hostname)
                .setUniqueId(username)
                .create();

        ProcessingRules processingRules = new ProcessingRules();
        UserImplementation userImplementation1 = p -> displayAndSaveDescription(p[0], p[1]);
        processingRules
                .on("display_description")
                .call(userImplementation1)
                .then(publish());

        solutions.forEach((methodName, userImplementation) -> processingRules
                        .on(methodName)
                        .call(userImplementation)
                        .then(runnerAction.getClientAction()));

        client.goLiveWith(processingRules);
        RecordingSystem.notifyEvent(RoundManagement.getLastFetchedRound(), runnerAction.getShortName());
    }

    private ChallengeServerClient startUpAndTestChallengeServerClient() throws ConfigNotFoundException, UnsupportedEncodingException, UnirestException {
            String journeyId = readFromConfigFile("tdl_journey_id");
            boolean disableColours = Boolean.parseBoolean(readFromConfigFile("disable_colours", "false"));
            ChallengeServerClient challengeServerClient = new ChallengeServerClient(hostname, journeyId, disableColours);
            System.out.println(challengeServerClient.getJourneyProgress());
            System.out.println(challengeServerClient.getAvailableActions());
            return challengeServerClient;
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


    private boolean isRecordingSystemOk() {
        boolean requireRecording = Boolean.parseBoolean(readFromConfigFile("tdl_require_rec", "true"));

        //noinspection SimplifiableIfStatement
        if (requireRecording) {
            return RecordingSystem.isRunning();
        } else {
            return true;
        }
    }

    private boolean useExperimentalFeature() {
        return Boolean.parseBoolean(readFromConfigFile("tdl_enable_experimental", "false"));
    }
}
