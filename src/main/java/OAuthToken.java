import java.time.Instant;

public class OAuthToken {
    private String tokenType;
    private int expiresIn;
    private String accessToken;
    private long creationDate;
    public OAuthToken(String tokenType, Integer expiresIn, String accessToken) {
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.accessToken = accessToken;
        this.creationDate = Instant.now().toEpochMilli();

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
    public boolean isExpired(long duration) {
        return (Instant.now().toEpochMilli() - creationDate) > (expiresIn - duration) * 1000;
    }

    /**
     * Returns whether the access token is expired.
     * @return Whether the access token is past the expiration date.
     */
    public boolean isExpired() {
        return (Instant.now().toEpochMilli() - creationDate) > (expiresIn) * 1000;
    }

    @Override
    public String toString() {
        return String.join(",", tokenType, String.valueOf(expiresIn) , accessToken);
    }
}
