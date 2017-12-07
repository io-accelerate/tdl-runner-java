package befaster.runner;

import befaster.runner.client.HttpClient;
import befaster.runner.client.CombinedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tdl.client.abstractions.UserImplementation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static befaster.runner.CredentialsConfigFile.readFromConfigFile;
import static befaster.runner.RoundManagement.saveDescription;
import static befaster.runner.RunnerAction.getNewRoundDescription;


public class ClientRunner {
    private final Logger LOG = LoggerFactory.getLogger(ClientRunner.class);
    private String hostname;
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

    public ClientRunner withSolutionFor(String methodName, UserImplementation solution) {
        solutions.put(methodName, solution);
        return this;
    }

    //~~~~~~~~ The entry point ~~~~~~~~~

    public void start(String[] args) {
        if (!RecordingSystem.isRecordingSystemOk()) {
            System.out.println("Please run `record_screen_and_upload` before continuing.");
            return;
        }
        System.out.println("Connecting to " + hostname);
        runApp(args);
    }

    private void runApp(String[] args) {
        String journeyId;
        try {
            journeyId = readFromConfigFile("tdl_journey_id");
        } catch (ConfigNotFoundException e) {
            LOG.error("Cannot find tdl_journey_id, needed to communicate with the server. Add this to the credentials.config.", e);
            return;
        }
        boolean useColours = Boolean.parseBoolean(readFromConfigFile("tdl_use_coloured_output", "true"));
        CombinedClient combinedClient = new CombinedClient(journeyId, useColours, hostname, username, solutions, System.out::println);

        try {
            boolean shouldContinue = combinedClient.checkStatusOfChallenge();
            if (shouldContinue) {
                String userInput = getUserInput(args);
                String roundDescription = combinedClient.executeUserAction(
                        x -> RecordingSystem.notifyEvent(RoundManagement.getLastFetchedRound(), x),
                        userInput,
                        p -> saveDescription(p[0], p[1])
                );
                RoundManagement.saveDescription(roundDescription, lastFetchedRound -> RecordingSystem.notifyEvent(lastFetchedRound, getNewRoundDescription.getShortName()));
            }
        } catch (IOException e) {
            LOG.error("Could not read user input.", e);
        } catch (HttpClient.ServerErrorException e) {
            LOG.error("Server experienced an error. Try again.", e);
        } catch (HttpClient.OtherCommunicationException e) {
            LOG.error("Client threw an unexpected error.", e);
        } catch (HttpClient.ClientErrorException e) {
            LOG.error("The client sent something the server didn't expect.");
            System.out.println(e.getResponseMessage());
        }
    }

    private String getUserInput(String[] args) throws IOException {
        return args.length > 0 ? args[0] : readInputFromConsole();
    }

    private String readInputFromConsole() throws IOException {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        return buffer.readLine();
    }
}
