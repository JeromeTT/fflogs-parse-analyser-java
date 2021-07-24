import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Task {
    private final URI logsPath;
    private URI outputLoc;
    private Client client;

    public Task() throws MalformedURLException, URISyntaxException {
        // Change these for resource location
        String clientCredentialsFileName = "/credentials.txt";
        URI clientCredentialsFilePath = TaskDriver.class.getResource(clientCredentialsFileName).toURI();
        URL serverTokenLink = new URL("https://www.fflogs.com/oauth/token");
        URL apiCallsLink = new URL("https://www.fflogs.com/api/v2/client");
        client = new Client(clientCredentialsFilePath, serverTokenLink, apiCallsLink);

        // File containing list of logs
        String logsFileName = "/logs.txt";
        logsPath = TaskDriver.class.getResource(logsFileName).toURI();
        outputLoc = new URI("file:/C:/Users/Jayden Zhang/Desktop/Computer Science/Projects/Output/csv.txt");
    }

    public void run(){
        List<String> logList = new ArrayList<>();
        // Load all the log IDs into logs
        File file = new File(logsPath);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] logLine = scanner.nextLine().split("/",5);
                String logID = logLine[4];
                logList.add(logID);
            }
        } catch (FileNotFoundException e){
            Console.print("Error reading log file.");
            e.printStackTrace();
        }

        List<String> csvLines = new ArrayList<>();
        csvLines.add("LogStartTime,PullNumber,PullDuration,BossPercentage,FightPercentage,LastPhase,LastPhaseIntermission,StartTime,EndTime");

        for (String id: logList) {

            JsonObject response = client.makeAPIRequest(GraphQLQuery.reportInfoQuery(id));
            JsonObject unwrap = response.get("data").getAsJsonObject()
                    .get("reportData").getAsJsonObject()
                    .get("report").getAsJsonObject();
            Date startDate = new Date(Long.parseLong(unwrap.get("startTime").toString()));

            JsonArray fights =  unwrap.get("fights").getAsJsonArray();
            for (JsonElement fight : fights) {
                JsonObject obj = fight.getAsJsonObject();
                if (obj.get("encounterID").toString().equals("1050")) {
                    String pullNumber = obj.get("id").toString();

                    Integer startTime = Integer.parseInt(obj.get("startTime").toString()) / 1000;
                    Integer endTime = Integer.parseInt(obj.get("endTime").toString()) / 1000;
                    Integer pullDuration = endTime - startTime;

                    String bossPercentage  = obj.get("bossPercentage").toString();
                    String fightPercentage  = obj.get("fightPercentage").toString();

                    String lastPhase = obj.get("lastPhase").toString();
                    String lastPhaseIntermission = obj.get("lastPhaseIsIntermission").toString();

                    String finalString = String.join(",", startDate.toString(), pullNumber, pullDuration.toString(),
                            bossPercentage, fightPercentage, lastPhase, lastPhaseIntermission, startTime.toString(), endTime.toString());

                    csvLines.add(finalString);
                }

            }
        }
        try {
            File csvFile = new File(outputLoc);
            csvFile.createNewFile();
            FileWriter csvFileWriter = new FileWriter(outputLoc.toString());
            for (String lines: csvLines) {
                csvFileWriter.write(lines);
                csvFileWriter.write("\n");

            }
            csvFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
