
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.*;
import java.util.*;

public class TaskDriver {
    public static void main(String[] args) throws MalformedURLException, URISyntaxException {
        Task task = new Task();
        task.run();
    }
}
