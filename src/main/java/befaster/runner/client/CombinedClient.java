package befaster.runner.client;

import befaster.runner.RunnerAction;
import tdl.client.Client;
import tdl.client.ProcessingRules;
import tdl.client.abstractions.UserImplementation;

import java.util.Map;
import java.util.function.Consumer;

import static tdl.client.actions.ClientActions.publish;

public class CombinedClient {
    private HttpClient httpClient;
    private String hostname;
    private String username;
    private Map<String, UserImplementation> solutions;
    private Consumer<String> printer;

    public CombinedClient(String journeyId, boolean useColours, String hostname, String username, Map<String, UserImplementation> solutions, Consumer<String> printer) {
        this.hostname = hostname;
        this.username = username;
        this.solutions = solutions;
        this.printer = printer;
        httpClient = new HttpClient(hostname, journeyId, useColours);
    }

    public boolean checkStatusOfChallenge() throws HttpClient.ServerErrorException, HttpClient.OtherCommunicationException, HttpClient.ClientErrorException {
        String journeyProgress = httpClient.getJourneyProgress();
        printer.accept(journeyProgress);

        String availableActions = httpClient.getAvailableActions();
        printer.accept(availableActions);

        return !availableActions.contains("No actions available.");
    }

    public String executeUserAction(Consumer<String> deployCallback, String userInput, UserImplementation saveDescription) throws HttpClient.ServerErrorException, HttpClient.OtherCommunicationException, HttpClient.ClientErrorException {
        if (userInput.equals("deploy")) {
            deployToQueue(deployCallback, saveDescription);
        }
        return executeAction(userInput);
    }

    private void deployToQueue(Consumer<String> deployCallback, UserImplementation saveDescription) {
        RunnerAction runnerAction = RunnerAction.deployToProduction;

        Client client = new Client.Builder()
                .setHostname(hostname)
                .setUniqueId(username)
                .create();

        ProcessingRules processingRules = new ProcessingRules();

        // Debt - do we need this anymore?
        processingRules
                .on("display_description")
                .call(saveDescription)
                .then(publish());

        solutions.forEach((methodName, userImplementation) -> processingRules
                .on(methodName)
                .call(userImplementation)
                .then(runnerAction.getClientAction()));

        client.goLiveWith(processingRules);
        deployCallback.accept(runnerAction.getShortName());
    }

    private String executeAction(String userInput) throws HttpClient.ServerErrorException, HttpClient.OtherCommunicationException, HttpClient.ClientErrorException {
        String actionFeedback = httpClient.sendAction(userInput);
        printer.accept(actionFeedback);
        return httpClient.getRoundDescription();
    }
}
