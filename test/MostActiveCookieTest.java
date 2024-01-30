import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_METHOD)
class MostActiveCookieTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private Path tempFilePath;

    @BeforeEach
    void setUp() throws IOException {
        tempFilePath = Files.createTempFile("test-log", ".csv");
        String testData = "AtY0laUfhglK3lC7,2018-12-09T14:19:00+00:00\n" +
                "SAZuXPGUrfbcn5UA,2018-12-09T10:13:00+00:00\n" +
                "AtY0laUfhglK3lC7,2018-12-09T06:19:00+00:00\n" +
                "SAZuXPGUrfbcn5UA,2018-12-08T22:03:00+00:00\n" +
                "4sMM2LxV07bPJzwf,2018-12-08T21:30:00+00:00\n";
        Files.writeString(tempFilePath, testData);
    }

    @Test
    void testProcessLogFileReturnsExpectedMap() {
        try{
            Map<String, Integer> cookieCount = MostActiveCookie.processLogFile(tempFilePath.toString(), LocalDate.parse("2018-12-09"));
            assertEquals(2, cookieCount.size());
            assertEquals(2, cookieCount.get("AtY0laUfhglK3lC7"));
            assertEquals(1, cookieCount.get("SAZuXPGUrfbcn5UA"));
            assertTrue(cookieCount.containsKey("SAZuXPGUrfbcn5UA"));
            assertTrue(cookieCount.containsKey("AtY0laUfhglK3lC7"));
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void testProcessLogFileThrowsErrorOnNoneExistentFile() {
        try{
            Map<String, Integer> cookieCount = MostActiveCookie.processLogFile("none", LocalDate.parse("2018-12-09"));
            fail();
        } catch (IOException ignored) {
        }

    }

    @Test
    void testOneMostActiveCookies() {
        Map<String, Integer> cookieCount = Map.of("AtY0laUfhglK3lC7", 2, "SAZuXPGUrfbcn5UA", 1);
        List<String> mostActiveCookies = MostActiveCookie.mostActiveCookies(cookieCount);


        assertEquals(mostActiveCookies.size(), 1);
        assertEquals(mostActiveCookies.get(0), "AtY0laUfhglK3lC7");
    }

    @Test
    void testMultipleMostActiveCookies() {
        Map<String, Integer> cookieCount = Map.of("AtY0laUfhglK3lC7", 2, "SAZuXPGUrfbcn5UA", 2);
        List<String> mostActiveCookies = MostActiveCookie.mostActiveCookies(cookieCount);


        assertEquals(mostActiveCookies.size(), 2);
        assertTrue(mostActiveCookies.containsAll(List.of(new String[]{"AtY0laUfhglK3lC7", "SAZuXPGUrfbcn5UA"})));
    }

    @Test
    void testMainWithInvalidArguments() {
        System.setOut(new PrintStream(outputStream));

        MostActiveCookie.main(new String[]{"-f", "cookie_log.csv"});
        String output = outputStream.toString();

        System.setOut(System.out);

        assertTrue(output.contains("Usage: ./[command] -f <filename> -d <date>"));
    }

    @Test
    void testMainWithInvalidDate() {
        System.setOut(new PrintStream(outputStream));

        MostActiveCookie.main(new String[]{"-f", tempFilePath.toString(), "-d", "invalidDate"});
        String output = outputStream.toString();

        System.setOut(System.out);

        assertTrue(output.contains("Invalid date format. Please use yyyy-MM-dd."));
    }

    @Test
    void testMainWithNoMatchingEntries() {
        System.setOut(new PrintStream(outputStream));

        MostActiveCookie.main(new String[]{"-f", tempFilePath.toString(), "-d", "2018-12-10"});
        String output = outputStream.toString();

        System.setOut(System.out);

        assertEquals("", output);
    }

    @Test
    void testValidMain() {
        System.setOut(new PrintStream(outputStream));

        MostActiveCookie.main(new String[]{"-f", tempFilePath.toString(), "-d", "2018-12-09"});
        String output = outputStream.toString();

        System.setOut(System.out);

        assertEquals("AtY0laUfhglK3lC7" + System.lineSeparator(), output);
    }
}
