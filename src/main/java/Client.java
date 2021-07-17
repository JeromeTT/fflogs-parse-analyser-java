
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
     * String storing the cliient ID.
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

    private URL serverLink;

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

    public Client(URI clientFilePath, URL serverTokenURL) {
        serverLink = serverTokenURL;
        try {
            File credentialsFile = new File(clientFilePath);
            Scanner reader = new Scanner(credentialsFile);
            String credentials = reader.nextLine();
            String[] splitCredentials = credentials.split(",");
            this.clientID = splitCredentials[0];
            this.clientSecret = splitCredentials[1];
            Console.print("Successfully created client with provided client crendentials file.");
        } catch (FileNotFoundException e) {
            Console.print("Error with reading client credentials.");
            e.printStackTrace();
        }
    }

    /**
     * Generates a new access token using the stored client information.
     */
    public void generateNewClientToken() {
        try {
            // Establish HTTP connection
            HttpURLConnection connection = (HttpURLConnection) serverLink.openConnection();

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
            InputStream inputStream = connection.getInputStream();
            Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8);

            JsonObject obj = JsonParser.parseString(scanner.next()).getAsJsonObject();

            clientToken = new OAuthToken(obj.get("token_type").getAsString(), obj.get("expires_in").getAsInt(), obj.get("access_token").getAsString());
            Console.print("Successfully generated new client token from client credentials.");
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns a client token which is valid at the current time.
     * @return the client access token.
     */
    public String getClientToken() {
        if (!isTokenValid()) {
            generateNewClientToken();
        }
        return clientToken.getAccessToken();
    }

    /**
     * Returns a client access token which is valid for the specified time.
     * @param duration the duration the token is expected to last for in seconds.
     * @return the client access token.
     */
    public String getClientToken(long duration) {
        if (!isTokenValid(duration)) {
            generateNewClientToken();
        }
        if (isTokenValid(duration)) {
            return clientToken.getAccessToken();
        } else {
            throw new IllegalArgumentException("Time exceeds the valid duration of the token");
        }
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

    /**
     * Returns a boolean value on whether the token will be valid for the specified duration.
     * @param duration the duration the token is required to be valid for.
     * @return Returns a boolean value on whether the token is valid.
     */
    public boolean isTokenValid(long duration) {
        boolean isValid = false;
        if (clientToken != null) {
            isValid = !clientToken.isExpired(duration);
        }
        return isValid;
    }

}
