import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class Task {
    private String outputLoc;
    private Client client;
    private LogCollection logCollection;
    private String nameID;
    private String encounterID;


    /**
     * Initialises all the variables for our task.
     * @throws MalformedURLException Invalid URLs provided.
     * @throws URISyntaxException Invalid file paths provided.
     */
    public Task() throws MalformedURLException, URISyntaxException, FileNotFoundException {
        // Change these for resource location
        String fightName = "top";
        String logsNo = "1";

        switch(fightName + logsNo) {
            case "dsr1":
                nameID = "Tearsong Spellmoon";
                break;
            case "dsr2":
                nameID = "Mia Hisuteri";
                break;
            case "topKindred":
                nameID = "Peri Dot";
                break;
            case "topEthics":
                nameID = "Kur Rumi";
                break;
            default:
                nameID = "Minty Evergreen";
        }
        switch(fightName) {
            case "ucob":
                encounterID = "1060";
                break;
            case "uwu":
                encounterID = "1061";
                break;
            case "tea":
                encounterID = "1062";
                break;
            case "dsr":
                encounterID = "1065";
                break;
            case "top":
                encounterID = "1068";
                break;
            default:
                encounterID = null;
        }
        String clientCredentialsFilePath = "credentials.txt";
        URL serverTokenLink = new URL("https://www.fflogs.com/oauth/token");
        URL apiCallsLink = new URL("https://www.fflogs.com/api/v2/client");
        URL apiUserCallsLink = new URL("https://www.fflogs.com/api/v2/user");
        client = new Client(clientCredentialsFilePath, serverTokenLink, apiCallsLink);

        // File containing list of logs
        String logsFileName = fightName + logsNo + "logs.txt";
        logCollection = new LogCollection(logsFileName);

        // File containing the output directory
        outputLoc = new File(TaskDriver.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getPath();
        outputLoc = new File(outputLoc).getParentFile().getPath() + "/" + fightName + logsNo + ".csv";
        Console.print(outputLoc);
        Console.print("Initialized Task for: " + fightName + logsNo);
    }

    /**
     * Runs the main code of the program.
     */
    public void run(){
        List<String> logList = logCollection.getLogList();

        List<String> csvLines = new ArrayList<>();
        csvLines.add("LogStartTime,PullNumber,PullDuration,BossPercentage,FightPercentage,LastPhase,LastPhaseIntermission");

        if (encounterID.equals("1068")) {
            csvLines.set(0, csvLines.get(0) + ",P2Glitch,P3Defamation");
        }

        for (String id: logList) {
            Console.print("RETRIEVING LOG WITH ID: "+ id);
            JsonObject response = client.makeAPIRequest(GraphQLQuery.reportInfoQuery(id));
            JsonObject unwrap = response.get("data").getAsJsonObject()
                    .get("reportData").getAsJsonObject()
                    .get("report").getAsJsonObject();
            Long startDate = Long.parseLong(unwrap.get("startTime").toString());
            JsonArray players = unwrap.get("masterData").getAsJsonObject().get("actors").getAsJsonArray();
            ArrayList<Integer> mainPlayerIndex = new ArrayList<>();
            for (JsonElement player : players) {
                if (player.getAsJsonObject().get("name").getAsString().equals(nameID)) {
                    mainPlayerIndex.add(player.getAsJsonObject().get("id").getAsInt());
                }
            }
            JsonArray fights = unwrap.get("fights").getAsJsonArray();

            Set<Integer> P2RemoteGlitch1068 = new HashSet<>();
            Set<Integer> P2MidGlitch1068 = new HashSet<>();
            HashMap<Integer, Set<Integer>> P3Defamation1068 = new HashMap<>();
            HashMap<Integer, Set<Integer>> P3BlueRot1068 = new HashMap<>();
            if (encounterID.equals("1068")) {
                // Remote Glitch P2 List
                response = client.makeAPIRequest(GraphQLQuery.reportDebuffQuery(id, ABILITY.REMOTE_GLITCH.ID(), 2));
                unwrap = response.get("data").getAsJsonObject()
                        .get("reportData").getAsJsonObject()
                        .get("report").getAsJsonObject()
                        .get("events").getAsJsonObject();
                JsonArray events = unwrap.get("data").getAsJsonArray();
                for (JsonElement event : events) {
                    JsonObject obj = event.getAsJsonObject();
                    P2RemoteGlitch1068.add(Integer.parseInt(obj.get("fight").toString()));
                }
                // Mid Glitch P2 List
                response = client.makeAPIRequest(GraphQLQuery.reportDebuffQuery(id, ABILITY.MID_GLITCH.ID(), 2));
                unwrap = response.get("data").getAsJsonObject()
                        .get("reportData").getAsJsonObject()
                        .get("report").getAsJsonObject()
                        .get("events").getAsJsonObject();
                events = unwrap.get("data").getAsJsonArray();
                for (JsonElement event : events) {
                    JsonObject obj = event.getAsJsonObject();
                    P2MidGlitch1068.add(Integer.parseInt(obj.get("fight").toString()));

                }
                // TODO: BLUE/RED DEFAMATION P3
                // Get Defamations
                response = client.makeAPIRequest(GraphQLQuery.reportDebuffQuery(id, ABILITY.OVERFLOW_CODE_SMELL.ID(), 3));
                unwrap = response.get("data").getAsJsonObject()
                        .get("reportData").getAsJsonObject()
                        .get("report").getAsJsonObject()
                        .get("events").getAsJsonObject();
                events = unwrap.get("data").getAsJsonArray();
                for (JsonElement event : events) {
                    JsonObject obj = event.getAsJsonObject();
                    Set<Integer> targetSet = P3Defamation1068.computeIfAbsent(Integer.parseInt(obj.get("fight").toString()), k -> new HashSet<>());
                    targetSet.add(Integer.parseInt(obj.get("targetID").toString()));
                }
                for (Integer key: P3Defamation1068.keySet()){
                    System.out.println("Defamations: " +key + ": " + P3Defamation1068.get(key));
                }

                // Get Blue Rot
                response = client.makeAPIRequest(GraphQLQuery.reportDebuffQuery(id, ABILITY.PERFORMANCE_CODE_SMELL.ID(), 3));
                unwrap = response.get("data").getAsJsonObject()
                        .get("reportData").getAsJsonObject()
                        .get("report").getAsJsonObject()
                        .get("events").getAsJsonObject();
                events = unwrap.get("data").getAsJsonArray();
                for (JsonElement event : events) {
                    JsonObject obj = event.getAsJsonObject();
                    Set<Integer> targetSet = P3BlueRot1068.computeIfAbsent(Integer.parseInt(obj.get("fight").toString()), k -> new HashSet<>());
                    targetSet.add(Integer.parseInt(obj.get("targetID").toString()));
                }
                for (Integer key: P3BlueRot1068.keySet()){
                    System.out.println("Blue Rot: " +key + ": " + P3BlueRot1068.get(key));
                }
                System.out.println("Mid: " + P2MidGlitch1068);
                System.out.println("Remote: " + P2RemoteGlitch1068);


            }
            for (JsonElement fight : fights) {
                JsonObject obj = fight.getAsJsonObject();
                ArrayList<Integer> allPlayerIndexes = new ArrayList<>();
                for(JsonElement i : obj.getAsJsonArray("friendlyPlayers")) {
                    allPlayerIndexes.add(i.getAsInt());
                }
                allPlayerIndexes.retainAll(mainPlayerIndex);
                if (obj.get("encounterID").toString().equals(encounterID) & allPlayerIndexes.size() > 0) {
                    Integer pullNumber = Integer.parseInt(obj.get("id").toString());

                    Integer startTime = Integer.parseInt(obj.get("startTime").toString());
                    Integer endTime = Integer.parseInt(obj.get("endTime").toString());
                    Long newDate = startDate + startTime;

                    Integer pullDuration = endTime - startTime;

                    String bossPercentage  = obj.get("bossPercentage").toString();
                    String fightPercentage  = obj.get("fightPercentage").toString();

                    String lastPhase = obj.get("lastPhase").toString();
                    String lastPhaseIntermission = obj.get("lastPhaseIsIntermission").toString();
                    if (lastPhaseIntermission.equals("false")) {
                        lastPhaseIntermission = "0";
                    } else {
                        lastPhaseIntermission = "1";
                    }

                    String finalString = String.join(",", newDate.toString(), pullNumber.toString(), pullDuration.toString(),
                            bossPercentage, fightPercentage, lastPhase, lastPhaseIntermission);

                    if (encounterID.equals("1068")) {
                        if (P2RemoteGlitch1068.contains(pullNumber)){
                            finalString = String.join(",", finalString, "R");
                        } else if (P2MidGlitch1068.contains(pullNumber)){
                            finalString = String.join(",", finalString, "M");
                        } else {
                            finalString = String.join(",", finalString, "N");
                        }

                        if (P3BlueRot1068.get(pullNumber) == null) {
                            finalString = String.join(",", finalString, "N");
                        } else {
                            P3BlueRot1068.get(pullNumber).retainAll(P3Defamation1068.get(pullNumber));
                            if (P3BlueRot1068.get(pullNumber).size() > 0){
                                finalString = String.join(",", finalString, "B");
                            } else {
                                finalString = String.join(",", finalString, "R");
                            }
                        }
                    }
                    csvLines.add(finalString);
                    System.out.println(finalString);
                }

            }
        }
        try {
            File csvFile = new File(outputLoc);
            csvFile.createNewFile();
            FileWriter csvFileWriter = new FileWriter(outputLoc);
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
