import com.google.gson.JsonObject;

public class GraphQLQuery {
    /**
     * Returns a JsonObject of the GraphQL Query to obtain the API call rate limits.
     * @return Returns a JsonObject of the GraphQL Query to obtain the API call rate limits.
     */
    static JsonObject rateLimitQuery(){
        String query = """
        {
          rateLimitData {
            limitPerHour
            pointsResetIn
            pointsSpentThisHour
          }
        }
        """;
        return toJson(query);
    }

    static JsonObject reportInfoQuery(String code) {
        String query = String.format("""
        {
          reportData{
            report(code: "%s"){
              startTime
              fights{
                id
                bossPercentage
                fightPercentage
                encounterID
                lastPhase
                lastPhaseIsIntermission
                startTime
                endTime
              }
            }
          }
        }
        """, code);
        return toJson(query);

    }

    /**
     * Converts a string to json.
     * @param query The query in string format.
     * @return JsonObject form of the query.
     */
    static JsonObject toJson(String query) {
        JsonObject obj = new JsonObject();
        obj.addProperty("query", query);
        return obj;
    }
}
