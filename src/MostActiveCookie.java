import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MostActiveCookie {

    public static void main(String[] args) {
        // Parse command line parameters
        String filename = null;
        String dateStr = null;

        for (int i = 0; i < args.length; i++) {
            if ("-f".equals(args[i]) && i + 1 < args.length) {
                filename = args[i + 1];
            } else if ("-d".equals(args[i]) && i + 1 < args.length) {
                dateStr = args[i + 1];
            }
        }

        if (filename == null || dateStr == null) {
            System.out.println("Usage: ./[command] -f <filename> -d <date>");
            return;
        }

        // Parse date
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            return;
        }

        // Process the log file
        try {
            Map<String, Integer> cookieCount = processLogFile(filename, date);
            for(String cookie : mostActiveCookies(cookieCount)){
                System.out.println(cookie);
            };
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }

    static Map<String, Integer> processLogFile(String filename, LocalDate date) throws IOException {
        Map<String, Integer> cookieCount = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String cookie = parts[0].trim();
                    LocalDateTime timestamp = LocalDateTime.parse(parts[1].trim(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                    if (timestamp.toLocalDate().isEqual(date)) {
                        cookieCount.put(cookie, cookieCount.getOrDefault(cookie, 0) + 1);
                    } else if (timestamp.toLocalDate().isBefore(date)){
                        // Since cookies are sorted by timestamp, we can break when we move to the previous day
                        break;
                    }
                }
            }
        }

        return cookieCount;
    }

    static List<String> mostActiveCookies(Map<String, Integer> cookieCount) {
        int maxCount = 0;
        List<String> cookies = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : cookieCount.entrySet()) {
            int count = entry.getValue();
            if (count > maxCount) {
                maxCount = count;
            }
        }

        for (Map.Entry<String, Integer> entry : cookieCount.entrySet()) {
            if (entry.getValue() == maxCount) {
                cookies.add(entry.getKey());
            }
        }
        return cookies;
    }
}
