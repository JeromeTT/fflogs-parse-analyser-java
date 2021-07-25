import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class LogCollection {
    private List<String> logList = new ArrayList<>();

    public LogCollection(URI logsPath) {
        readLogFromFile(logsPath);
    }
    public void readLogFromFile(URI logsPath){
        File file = new File(logsPath);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String logLine = scanner.nextLine();
                logList.add(logLine);
            }
        } catch (FileNotFoundException e){
            Console.print("Error reading log file.");
            e.printStackTrace();
        }
    }

    public List<String> getLogList(){
        return Collections.unmodifiableList(logList);
    }
}
