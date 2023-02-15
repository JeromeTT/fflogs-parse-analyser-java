import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class LogCollection {
    private List<String> logList = new ArrayList<>();

    public LogCollection(String logsPath) {
        readLogFromFile(logsPath);
    }
    public void readLogFromFile(String logsPath){
        InputStream stream = getClass().getResourceAsStream(logsPath);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            while (reader.ready()) {
                String logLine = reader.readLine();
                String[] splitted = logLine.split("/", 0);
                logList.add(splitted[splitted.length - 1]);
            }
        } catch (FileNotFoundException e){
            Console.print("Error reading log file.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getLogList(){
        return Collections.unmodifiableList(logList);
    }
}
