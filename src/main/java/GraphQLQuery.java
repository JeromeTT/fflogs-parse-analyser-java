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
    static JsonObject reportDebuffQuery(String code, Integer debuff) {
        String query = String.format("""
        {   
          reportData{
            report(code: "%s"){
              events(fightIDs: [1,2,10], dataType: Debuffs, abilityID: 1003428, filterExpression: "encounterPhase = 2", limit: 10000),{
                data
              }
            }
          }
        }
    """, code, debuff) ;
    return toJson(query);
    }
    static JsonObject reportDebuffQuery(String code, Integer debuff, Integer phase) {
        String query = String.format("""
        {
            reportData{
                report(code: "%s"){
                    events(startTime: 0, endTime: 100000000, dataType: Debuffs, abilityID: %d, filterExpression: "encounterPhase = %d", limit: 10000),{
                        data
                    }
                }
            }
        }
    """, code, debuff, phase) ;
        return toJson(query);
    }

    static JsonObject reportInfoQuery(String code) {
        String query = String.format("""
        {
          reportData{
            report(code: "%s"){
              startTime
              masterData{
                actors(type: "player"){
                  name
                  id
                }
              }
              fights{
                id
                friendlyPlayers
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
