import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

public class Client {
    /**
     * String storing the client ID.
     */
    private String clientID;
    /**
     * String storing the client secret.
     */
    private String clientSecret;
    /**
     * The OAuth Token generated using the client ID and client secret.
     */
    private OAuthToken clientToken;
    private OAuthToken userToken;

    private URL serverTokenURL;
    private URL serverAPIURL;

    private String clientFilePath;
    /**
     * Generates a client given a client ID and client secret.
     * @param clientID The client ID.
     * @param clientSecret The client secret.
     */
    public Client(String clientID, String clientSecret) {
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        System.out.println("Successfully created client with provided client ID and client secret.");
    }

    /**
     * Generate a client given a file containing a client separated client ID and client secret.
     * @param clientFilePath url of the file containing a singular comma separated
     *             line containing the client ID and client secret.
     */

    public Client(String clientFilePath, URL serverTokenURL, URL serverAPIURL) {
        this.serverTokenURL = serverTokenURL;
        this.serverAPIURL = serverAPIURL;
        this.clientFilePath = clientFilePath;
        try {
            InputStream stream = getClass().getResourceAsStream(clientFilePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            if( reader.ready() ) {
                String[] splitCredentials = reader.readLine().split(",");
                String[] splitClientCredentials = reader.readLine().split(",");
                this.clientID = splitCredentials[0];
                this.clientSecret = splitCredentials[1];
                if (splitClientCredentials.length > 0) {
                    this.clientToken = new OAuthToken(Long.parseLong(splitClientCredentials[1]), splitClientCredentials[0]);
                }
            }
            getClientToken();
            Console.print("Successfully created client with provided client credentials file.");
        } catch (FileNotFoundException e) {
            Console.print("Error with reading client credentials.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a new access token using the stored client information.
     */
    public void generateNewClientToken() {
        try {
            // Establish HTTP connection
            HttpURLConnection connection = (HttpURLConnection) serverTokenURL.openConnection();

            // Set HTTP Properties
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", "Basic "+ Base64.getEncoder().encodeToString((clientID+':'+clientSecret).getBytes()));

            // Set data
            String data = "grant_type=client_credentials";
            byte[] out = data.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = connection.getOutputStream();
            stream.write(out);

            // Get input stream into json object
            InputStream response = connection.getInputStream();
            Scanner scanner = new Scanner(response, StandardCharsets.UTF_8);

            JsonObject obj = JsonParser.parseString(scanner.next()).getAsJsonObject();

            clientToken = new OAuthToken(obj.get("expires_in").getAsInt(), obj.get("access_token").getAsString());
            try {
                File csvFile = new File(clientFilePath);
                csvFile.createNewFile();
                System.out.println("STORING NEW CLIENT CREDENTIALS");
                FileWriter csvFileWriter = new FileWriter(csvFile);
                String line = String.join(",", clientID, clientSecret, clientToken.getAccessToken(), Long.toString(clientToken.getExpiryDate()));
                csvFileWriter.write(line);
                csvFileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Console.print("Successfully generated new client token from client credentials.");
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Makes a GraphQL Query to the API server.
     * @param graphQLQuery The GraphQL Query to send to the server.
     * @return Returns the JSON response as a JsonObject.
     */
    public JsonObject makeAPIRequest(JsonObject graphQLQuery){
        try {
            // Establish HTTP connection
            HttpURLConnection connection = (HttpURLConnection) serverAPIURL.openConnection();

            // Set HTTP Properties
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + getClientToken());

            // Set data
            OutputStream stream = connection.getOutputStream();
            stream.write(graphQLQuery.toString().getBytes(StandardCharsets.UTF_8));

            // Get input stream into json object
            InputStream response = connection.getInputStream();
            Scanner scanner = new Scanner(response, StandardCharsets.UTF_8);
            JsonObject obj = JsonParser.parseString(scanner.nextLine()).getAsJsonObject();
            connection.disconnect();

            return obj;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Queries for the rate limit data using the stored client information.
     * @return Returns a JsonObject containing the rate limit data.
     */
    public JsonObject getRateLimitData(){
        return makeAPIRequest(GraphQLQuery.rateLimitQuery());
    }

    /**
     * Returns a client token which is valid at the current time.
     * @return the client access token.
     */
    public String getClientToken() {
        if (!isTokenValid()) {
            System.out.println("Refreshed client token");
            generateNewClientToken();
        }
        return clientToken.getAccessToken();
    }

    /**
     * Returns a boolean value on whether the token is valid at the current time.
     * @return Returns a boolean value on whether the token is valid.
     */
    public boolean isTokenValid() {
        boolean isValid = false;
        if (clientToken != null) {
            isValid = !clientToken.isExpired();
        }
        return isValid;
    }

}
