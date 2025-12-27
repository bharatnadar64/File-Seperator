import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Task {
    public static void main(String[] args) {
        Map<String, Integer> ext = new HashMap<>();

        ext.put("txt", 120);
        ext.put("pdf", 45);
        ext.put("jpg", 200);
        ext.put("png", 150);
        ext.put("docx", 30);
        ext.put("mp4", 12);

     //  Print the map
     //   for (Map.Entry<String, Integer> entry : ext.entrySet()) {
     //       System.out.println(entry.getKey() + " -> " + entry.getValue());
     //    }

      // create logic to add these values in excel/csv file.
         String filePath = "file_extensions.csv";

        try (FileWriter writer = new FileWriter(filePath)) {

            // Header
            writer.append("Extension,Count\n");

            // Data
            for (Map.Entry<String, Integer> entry : ext.entrySet()) {
                writer.append(entry.getKey())
                      .append(",")
                      .append(String.valueOf(entry.getValue()))
                      .append("\n");
            }

            System.out.println("CSV file created successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
