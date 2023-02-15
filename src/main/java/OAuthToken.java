import java.time.Instant;

public class OAuthToken {
    private String accessToken;
    private long expiryDate;
    public OAuthToken(Integer expiresIn, String accessToken) {
        this.accessToken = accessToken;
        this.expiryDate = Instant.now().toEpochMilli() + (expiresIn * 1000) - 3600000;
    }

    public OAuthToken(Long expiryDate, String accessToken) {
        this.accessToken = accessToken;
        this.expiryDate = expiryDate;
    }

    /**
     * Returns the access token.
     * @return The access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Returns the expiry date.
     * @return Returns the expiry date.
     */
    public long getExpiryDate() { return expiryDate; }

    /**
     * Returns whether the access token is expired.
     * @return Whether the access token is past the expiration date.
     */
    public boolean isExpired() {
        return Instant.now().toEpochMilli() > this.expiryDate;
    }

    @Override
    public String toString() {
        return String.join(",", String.valueOf(this.expiryDate) , accessToken);
    }
}
