import java.io.FileNotFoundException;
import java.net.*;

public class TaskDriver {
    public static void main(String[] args) throws MalformedURLException, URISyntaxException, FileNotFoundException {
        Task task = new Task();
        task.run();
    }
}
