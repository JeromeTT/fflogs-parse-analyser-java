
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Driver {
    public static void main(String[] args) throws MalformedURLException, URISyntaxException {
        // Change these for resource location
        String clientCredentialsFileName = "/credentials.txt";
        URL serverTokenLink = new URL("https://www.fflogs.com/oauth/token");
        URI clientCredentialsFilePath = Driver.class.getResource(clientCredentialsFileName).toURI();

        // Generate our client from the client credentials
        Client client = new Client(clientCredentialsFilePath, serverTokenLink);

        // Get our client token
        String token = client.getClientToken(60000000);

        System.out.println(token);
        System.out.println(client.isTokenValid(600));
    }
}
