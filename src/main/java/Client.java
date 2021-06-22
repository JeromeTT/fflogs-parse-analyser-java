
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client {
    private String clientID;
    private String clientSecret;
    private OAuthToken clientToken;

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
     * @param directory directory of the file containing a singular comma separated
     *             line containing the client ID and client secret.
     */
    public Client(String directory) {
        try {
            File credentialsFile = new File(directory);
            Scanner reader = new Scanner(credentialsFile);
            String credentials = reader.nextLine();
            String[] splitCredentials = credentials.split(",");
            this.clientID = splitCredentials[0];
            this.clientSecret = splitCredentials[1];
            System.out.println("Successfully created client with provided resource URL.");
        } catch (FileNotFoundException e) {
            System.out.println("Error with reading credentials");
            e.printStackTrace();
        }
    }

    /**
     * Generates a new access token using the stored client information.
     * @param tokenURL The URL where access tokens are issued.
     */
    public void generateNewClientToken(URL tokenURL) {
        try {
            // Establish HTTP connection
            HttpURLConnection connection = (HttpURLConnection) tokenURL.openConnection();

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
            System.out.println("Successfully generated new client token");
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Loads the client token information from the given text file.
     * @param directory the location of the text file containing the client token information.
     */
    public void setClientToken(String directory) throws FileNotFoundException {
        File clientTokenFile = new File(directory);
        Scanner reader = new Scanner(clientTokenFile);
        String clientToken = reader.nextLine();
        String[] splitClientToken = clientToken.split(",");
        this.clientToken = new OAuthToken(splitClientToken[0], Integer.parseInt(splitClientToken[1]), splitClientToken[2]);
        System.out.println("Successfully loaded client token information");
    }

    /**
     * Tries to load the client token information from the given resource before generating a new client token.
     * @param tokenURL the URL where the token is retrieved.
     * @param directory the location of the text file containing the client token information.
     */
    public void setClientToken(URL tokenURL, String directory) throws FileNotFoundException{
        if (!isTokenValid()) {
            try {
                setClientToken(directory);
            } catch (NoSuchElementException | ArrayIndexOutOfBoundsException e){
                System.out.println("Invalid saved client token, generating new client token.");
                // Generate a new client token if it is not invalid
                generateNewClientToken(tokenURL);
                saveClientToken(directory);
            }
        }
    }

    public void saveClientToken(String directory) {
        try {
            File file = new File(directory);
            FileWriter fw = new FileWriter(file, false);
            fw.write(clientToken.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the client access token.
     * @return the client access token.
     */
    public String getClientToken() {
        if (isTokenValid()) {
            return clientToken.getAccessToken();
        }
        return null;
    }

    /**
     * Returns a boolean value on whether the token is valid.
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
