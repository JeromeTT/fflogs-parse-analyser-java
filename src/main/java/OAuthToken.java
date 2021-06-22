import java.time.Instant;

public class OAuthToken {
    private String tokenType;
    private int expiresIn;
    private String accessToken;
    private int creationDate;

    public OAuthToken(String tokenType, Integer expiresIn, String accessToken) {
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.accessToken = accessToken;
        this.creationDate = (int) Instant.now().toEpochMilli() / 1000;

    }

    /**
     * Returns the access token.
     * @return The access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Returns whether the access token is expired.
     * @return Whether the access token is past the expiration date.
     */
    public boolean isExpired() {
        return Instant.now().toEpochMilli() / 1000 - creationDate > expiresIn;
    }

    @Override
    public String toString() {
        return String.join(",", tokenType, String.valueOf(expiresIn) , accessToken);
    }
}
