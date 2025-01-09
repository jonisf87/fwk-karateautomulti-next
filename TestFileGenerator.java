/**
 * TestFileGenerator is a utility program to dynamically create Karate tests.
 * It guides the user through a series of inputs to generate directories, test files,
 * JSON data files, and configuration updates required for automated API testing.
 */

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class TestFileGenerator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Step 0: Presentation of the tool
        System.out.println("***************************************");
        System.out.println("Welcome to Karate Test Generator!");
        System.out.println("Answer the following questions to create your test");

        // Step 0.1: Ask for Jira ID
        System.out.println("***************************************");
        System.out.println("Do you have any Jira ID? (yes/no)");
        String jiraAnswer = scanner.nextLine().trim().toLowerCase();
        String jiraId = "";
        if (jiraAnswer.equals("yes") || jiraAnswer.equals("y")) {
            System.out.println("Write the ID (don't include @):");
            jiraId = scanner.nextLine().trim();
        }
        
        // Step 1: Choose API
        System.out.println("***************************************");
        String selectedApi = chooseApi(scanner);
        System.out.println("Selected API: " + selectedApi);

        // Step 1.1: Choose authentication method
        System.out.println("***************************************");
        System.out.println("Choose your authentication method: ");
        String[] authMethods = {"basic", "bearer"};
        for (int i = 0; i < authMethods.length; i++) {
            System.out.println((i + 1) + ". " + authMethods[i]);
        }
        int authChoice = getUserChoice(scanner, authMethods.length);
        String selectedAuth = authMethods[authChoice - 1];

        // Step 2: Choose method
        System.out.println("***************************************");
        System.out.println("Choose method:");
        String[] methods = {"get", "post", "put", "update", "delete"};
        for (int i = 0; i < methods.length; i++) {
            System.out.println((i + 1) + ". " + methods[i]);
        }
        int methodChoice = getUserChoice(scanner, methods.length);
        String selectedMethod = methods[methodChoice - 1];

        // Step 3: Write path
        System.out.println("***************************************");
        System.out.println("Write the path of the endpoint:");
        String path = scanner.nextLine().trim();
        String folderName = selectedMethod + capitalize(path.replace("/", "_"));
        
        // Step 3.1: Confirm or modify folder name
        System.out.println("***************************************");
        System.out.println("Generated folder name for the test: " + folderName);
        System.out.println("Is this folder name okay? (yes/no)");
        String folderNameResponse = scanner.nextLine().trim().toLowerCase();
        if (folderNameResponse.equals("no") || folderNameResponse.equals("n")) {
            System.out.println("Please enter a new folder name:");
            folderName = scanner.nextLine().trim();
        }

        // Create folder
        String baseDir = "karate/src/test/java/tests/" + selectedApi + "/" + folderName;
        Path testDir = Paths.get(baseDir);
        try {
            Files.createDirectories(testDir);
        } catch (IOException e) {
            System.err.println("Error creating folder: " + e.getMessage());
            System.exit(1);
        }

        // Step 4.1: Ask for parameters
        System.out.println("***************************************");
        System.out.println("Are there parameters? (yes/no)");
        String paramAnswer = scanner.nextLine().trim().toLowerCase();
        boolean hasParams = paramAnswer.equals("yes") || paramAnswer.equals("y");

        List<String> paramNames = new ArrayList<>();
        StringBuilder paramsBuilder = new StringBuilder();

        if (hasParams) {
            while (true) {
                System.out.println("Write parameter name:");
                String param = scanner.nextLine().trim();
                if (!param.isEmpty()) {
                    paramNames.add(param);
                    paramsBuilder.append("    And param ").append(param).append(" = req.params.").append(param).append("\n");
                } else {
                    System.out.println("Parameter name cannot be empty.");
                    continue;
                }

                System.out.println("Are there more parameters? (yes/no)");
                String moreParams = scanner.nextLine().trim().toLowerCase();
                if (moreParams.equals("no") || moreParams.equals("n")) {
                    break;
                } else if (!moreParams.equals("yes") && !moreParams.equals("y")) {
                    System.out.println("Invalid answer. Assuming there are no more parameters.");
                    break;
                }
            }
        }
        
        // 4.2 Ask for body
        System.out.println("***************************************");
        System.out.println("Is there a body? (yes/no)");
        String bodyAnswer = scanner.nextLine().trim().toLowerCase();
        boolean hasBody = bodyAnswer.equals("yes") || bodyAnswer.equals("y");
        StringBuilder bodyBuilder = new StringBuilder();
        
        if(hasBody){
            paramsBuilder.append("    And request req.body\n");
        }
        
        // Step 5: Create files
        String jsonFileName = baseDir + "/testData/" + "200.json";
        String featureFileName = baseDir + "/" + folderName + ".feature";

        try {
            // Create the testData directory if it doesn't exist
            Path testDataDir = Paths.get(baseDir + "/testData");
            Files.createDirectories(testDataDir);

            // Build the JSON with all parameters in all files
            StringBuilder jsonParams = new StringBuilder();
            jsonParams.append("    \"params\": {\n");
            for (int i = 0; i < paramNames.size(); i++) {
                jsonParams.append("      \"").append(paramNames.get(i)).append("\": \"Introduce value\"");
                if (i < paramNames.size() - 1) {
                    jsonParams.append(",\n");
                } else {
                    jsonParams.append("\n");
                }
            }
            jsonParams.append("    },");
            
            // Build the JSON with body
            StringBuilder jsonBody = new StringBuilder();
            jsonBody.append("    \"body\": {");
            if(hasBody){
                jsonBody.append(" \"key\":  \"Introduce vale\" ");
            }
            jsonBody.append("}");

            // Create the main JSON file with all parameters
            FileWriter jsonFile = new FileWriter(jsonFileName);
            jsonFile.write("{\n" +
                    "  \"request\": {\n" +
                    "    \"headers\": {},\n" +
                    jsonParams.toString() + "\n" +
                    jsonBody.toString() + "\n" +
                    "  },\n" +
                    "  \"expectedResponse\": {\n" +
                    "    \"status\": 200,\n" +
                    "    \"body\": {}\n" +
                    "  }\n" +
                    "}");
            jsonFile.close();

            // Create additional JSON files for each specific parameter
            for (String param : paramNames) {
                String paramSpecificJsonFile = baseDir + "/testData/" + "400_param_" + param + ".json";
                FileWriter paramJsonFile = new FileWriter(paramSpecificJsonFile);
                paramJsonFile.write("{\n" +
                        "  \"request\": {\n" +
                        "    \"headers\": {},\n" +
                        jsonParams.toString() + "\n" +
                        "    \"body\": {}\n" +
                        "  },\n" +
                        "  \"expectedResponse\": {\n" +
                        "    \"status\": 400,\n" +
                        "    \"body\": {}\n" +
                        "  }\n" +
                        "}");
                paramJsonFile.close();
            }

            // When creating the .feature file, modify based on the authentication method
            FileWriter featureFile = new FileWriter(featureFileName);
            if (selectedAuth.equals("basic")) {
                // Basic authentication logic
                featureFile.write("@" + folderName + " @" + selectedApi + " @regression\n" +
                        "Feature: Test " + folderName + " endpoint\n" +
                        (jiraId.isEmpty() ? "  @JiraId\n" : "  @" + jiraId + "\n") +
                        "  Scenario Outline: " + folderName + "\n" +
                        "    * def testData = read('<testDataFile>')\n" +
                        "    * def req = testData.request\n" +
                        "    * def expResponse = testData.expectedResponse\n" +
                        "\n" +
                        "    Given url urls." + selectedApi + "Url\n" +
                        "    And path \"" + path + "\"\n" +
                        paramsBuilder.toString() +  // Include all parameters
                        bodyBuilder.toString() + //Include body
                        "    When method " + selectedMethod.toUpperCase() + "\n" +
                        "    Then match responseStatus == expResponse.status\n" +
                        "    And match response contains deep expResponse.body\n" +
                        "\n" +
                        "    Examples:\n" +
                        "      | testDataFile                                                    |\n" +
                        "      | classpath:tests/" + selectedApi + "/" + folderName + "/testData/" + "200.json |\n");

            } else if (selectedAuth.equals("bearer")) {
                // Bearer token authentication logic
                featureFile.write("@" + folderName + " @" + selectedApi + " @regression\n" +
                        "Feature: Test " + folderName + " endpoint\n" +
                        (jiraId.isEmpty() ? "  @JiraId\n" : "  @" + jiraId + "\n") +
                        "  Scenario Outline: " + folderName + "\n" +
                        "    * def testData = read('<testDataFile>')\n" +
                        "    * def req = testData.request\n" +
                        "    * def expResponse = testData.expectedResponse\n" +
                        "\n" +
                        "    Given url urls." + selectedApi +"Url\n" +
                        "    And path \"auth/login\"\n" +
                        "    And request { username: '#(credentials." + selectedApi + "Username)', password: '#(credentials." + selectedApi +"Password)' }\n" +
                        "    When method post\n" +
                        "    Then status 200\n" +
                        "    And def token = response.accessToken\n" +
                        "\n" +
                        "    Given url urls." + selectedApi + "Url\n" +
                        "    And path \"" + path + "\"\n" +
                        paramsBuilder.toString() +  // Include all parameters
                        bodyBuilder.toString() + //Include body
                        "    When method " + selectedMethod.toUpperCase() + "\n" +
                        "    Then match responseStatus == expResponse.status\n" +
                        "    And match response contains deep expResponse.body\n" +
                        "\n" +
                        "    Examples:\n" +
                        "      | testDataFile                                                    |\n" +
                        "      | classpath:tests/" + selectedApi + "/" + folderName + "/testData/" + "200.json |\n");
            }

            // Add additional JSON files to the .feature file
            for (String param : paramNames) {
                featureFile.write("      | classpath:tests/" + selectedApi + "/" + folderName + "/testData/" + "400_param_" + param + ".json |\n");
            }
            featureFile.close();

        } catch (IOException e) {
            System.err.println("Error creating files: " + e.getMessage());
            System.exit(1);
        }


        // Step 8: Show success message
        System.out.println("***************************************");
        System.out.println("Successful test creation!!");
        System.out.println("You can find your test in: " + testDir.toAbsolutePath());
    }
    
    /**
     * Helper method to let the user choose an API from the available options.
     *
     * @param scanner The Scanner instance for user input.
     * @return The selected API name.
     */
    private static String chooseApi(Scanner scanner) {
        String baseApiDir = "karate/src/test/java/tests/";
        List<String> apiFolders = new ArrayList<>();

        // Read directories
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(baseApiDir),
                path -> Files.isDirectory(path) && path.getFileName().toString().contains("Api"))) {
            for (Path path : stream) {
                apiFolders.add(path.getFileName().toString());
            }
        } catch (IOException e) {
            System.err.println("Error reading API directories: " + e.getMessage());
        }

        // Present options
        if (!apiFolders.isEmpty()) {
            System.out.println("Is your API one of the following? Choose a number:");
            for (int i = 0; i < apiFolders.size(); i++) {
                System.out.println((i + 1) + ". " + apiFolders.get(i));
            }
            System.out.println((apiFolders.size() + 1) + ". No, I need to create it.");

            int choice = getUserChoice(scanner, apiFolders.size() + 1);
            if (choice <= apiFolders.size()) {
                return apiFolders.get(choice - 1);
            }
        }

        // Add new API
        System.out.println("***************************************");
        System.out.println("You chose No. Please enter a new API name (must end with 'Api'):");
        String newApi = scanner.nextLine().trim();
        if (!newApi.endsWith("Api")) {
            System.out.println("Invalid name. Appending 'Api' automatically.");
            newApi += "Api";
        }

        Path newApiDir = Paths.get(baseApiDir, newApi);
        try {
            Files.createDirectories(newApiDir);
        } catch (IOException e) {
            System.err.println("Error creating new API directory: " + e.getMessage());
            System.exit(1);
        }

        // Ask if the user wants to add URLs and credentials
        System.out.println("***************************************");
        System.out.println("Do you want to add URLs and credentials for the new API? (yes/no)");
        String addConfigResponse = scanner.nextLine().trim().toLowerCase();
        if (addConfigResponse.equals("yes") || addConfigResponse.equals("y")) {
            handleConfigFiles(scanner, newApi);
        }
        return newApi;
    }

    /**
     * Method to handle the selection or creation of configuration files.
     *
     * This method performs the following operations:
     * 1. Collects existing configuration files from the specified directory.
     *    - Filters files that start with "config-" and end with ".yml".
     * 2. Displays a list of available configuration files to the user.
     * 3. Provides an option for the user to create a new configuration file.
     * 4. Based on the user's choice:
     *    - Selects an existing configuration file for updating.
     *    - Creates a new configuration file with a user-specified name and initializes it with default content.
     * 5. Calls the `updateConfigFile` method to add or modify API details in the selected or newly created file.
     *
     * @param scanner The Scanner object for user input.
     * @param newApi  The name of the API to be added or updated in the configuration file.
     */
    private static void handleConfigFiles(Scanner scanner, String newApi) {
        String configBaseDir = "karate/src/test/java";
        List<Path> configFiles = new ArrayList<>();

        // Collect existing config files
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(configBaseDir),
                path -> path.getFileName().toString().startsWith("config-") && path.toString().endsWith(".yml"))) {
            for (Path path : stream) {
                configFiles.add(path);
            }
        } catch (IOException e) {
            System.err.println("Error reading config files: " + e.getMessage());
        }

        System.out.println("Available config files:");
        for (int i = 0; i < configFiles.size(); i++) {
            System.out.println((i + 1) + ". " + configFiles.get(i).getFileName().toString());
        }
        System.out.println((configFiles.size() + 1) + ". Create a new config file.");

        int configChoice = getUserChoice(scanner, configFiles.size() + 1);
        Path selectedConfig;

        if (configChoice <= configFiles.size()) {
            selectedConfig = configFiles.get(configChoice - 1);
        } else {
            System.out.println("***************************************");
            System.out.println("- Creating new environment -");
            System.out.println("Enter the name of the new environment:");
            String newEnvName = scanner.nextLine().trim();
            String newConfigFileName = "config-" + newEnvName + ".yml";
            selectedConfig = Paths.get(configBaseDir, newConfigFileName);
            System.out.println(" - config-" + newEnvName + ".yml file for new " + newEnvName + "environment created -");
            try {
                Files.write(selectedConfig, "urls:\ncredentials:\n".getBytes());
            } catch (IOException e) {
                System.err.println("Error creating new config file: " + e.getMessage());
                return;
            }
        }

        updateConfigFile(scanner, selectedConfig, newApi);
    }

    /**
     * Method to update the selected configuration file with new API details.
     *
     * This method performs the following steps:
     * 1. Reads the contents of the specified configuration file.
     * 2. Prompts the user to input a new API URL and sanitizes the input.
     * 3. Adds the new API URL to the "urls" section of the configuration.
     * 4. Offers the user an option to add credentials for the API.
     *    - Prompts for the credential name and value.
     *    - Inserts the credentials into the "credentials" section of the configuration.
     * 5. Saves the updated configuration back to the file.
     * 6. Asks the user if they want to edit another configuration file.
     *
     * Error handling ensures the user is notified if an issue occurs while reading or writing the file.
     *
     * @param scanner   The Scanner object for user input.
     * @param configFile The path to the configuration file being updated.
     * @param newApi     The name of the API to be added to the configuration.
     */
    private static void updateConfigFile(Scanner scanner, Path configFile, String newApi) {
        try {
            List<String> lines = Files.readAllLines(configFile);

            // Add URL
            System.out.println("***************************************");
            System.out.println("Enter the URL for the new API (raw URL, no quotes or double qoutes, ends with /):");
            String newUrl = scanner.nextLine().trim();

            // Remove single and double quotes from the input
            newUrl = newUrl.replace("\"", "").replace("'", "");

            String urlLine = "  " + newApi + "Url: \"" + newUrl + "\"";
            int urlsIndex = lines.indexOf("urls:") + 1;
            lines.add(urlsIndex, urlLine);

            // Ask for credentials
            while (true) {
                System.out.println("***************************************");
                System.out.println("Do you want to add credentials for this API? (yes/no)");
                String addCredentials = scanner.nextLine().trim().toLowerCase();
                if (addCredentials.equals("no") || addCredentials.equals("n")) {
                    break;
                }

                System.out.println("Enter the name of the credential (e.g.: Username, Password):");
                String credentialName = scanner.nextLine().trim();
                // Capitalize the first letter if it is not already
                if (!credentialName.isEmpty()) {
                    credentialName = credentialName.substring(0, 1).toUpperCase() + credentialName.substring(1);
                }
                System.out.println("Enter the content of the credential:");
                String credentialContent = scanner.nextLine().trim();
                String credentialLine = "  " + newApi + credentialName + ": \"" + credentialContent + "\"";
                int credentialsIndex = lines.indexOf("credentials:") + 1;
                lines.add(credentialsIndex, credentialLine);
            }

            Files.write(configFile, lines);
            System.out.println("Config file updated successfully.");

            // Ask if user wants to edit another file
            System.out.println("***************************************");
            System.out.println("Do you want to edit another config file? (yes/no)");
            String editAnother = scanner.nextLine().trim().toLowerCase();
            if (editAnother.equals("yes") || editAnother.equals("y")) {
                handleConfigFiles(scanner, newApi);
            }
        } catch (IOException e) {
            System.err.println("Error updating config file: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to validate and retrieve user choice as a number within a range.
     *
     * @param scanner The Scanner instance for user input.
     * @param max     The maximum valid choice number.
     * @return The validated user choice as an integer.
     */
    private static int getUserChoice(Scanner scanner, int max) {
        int choice = -1;
        while (choice < 1 || choice > max) {
            System.out.print("Enter a number between 1 and " + max + ": ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        }
        return choice;
    }
    
    /**
     * Helper method to capitalize the first letter of a string.
     *
     * @param str The string to capitalize.
     * @return The capitalized string.
     */
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
