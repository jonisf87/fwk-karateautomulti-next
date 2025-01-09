package tests;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;  // Import Jackson ObjectMapper
import com.fasterxml.jackson.core.type.TypeReference; // Import TypeReference
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

class KarateRunnerTest {

    @Test
    void karateRunnerTest() {
        Results results = Runner.path("classpath:tests")
                .outputHtmlReport(true)
                .outputCucumberJson(true)
                .outputJunitXml(true)
                .parallel(5);
        System.out.println("Report directory: " + results.getReportDir());
        generateReport(results.getReportDir());
        runPostTestScript();
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }
    public static void generateReport(String karateOutputPath) {
        Collection<File> jsonFiles = FileUtils.listFiles(new File(karateOutputPath), new String[] {"json"}, true);
        List<String> jsonPaths = new ArrayList<>(jsonFiles.size());
        jsonFiles.forEach(file -> jsonPaths.add(file.getAbsolutePath()));
        Configuration config = new Configuration(new File("target"), "demo");
        ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
        reportBuilder.generateReports();
    }

    private void runPostTestScript() {
        System.out.println("Starting .json reports combination...");
        String reportsDir = "target/karate-reports";
        String outputFile = "target/cucumber-result.json";

        try {
            List<Map<String, Object>> combinedReport = new ArrayList<>();

            // Read all JSON files from the reports directory
            Collection<File> jsonFiles = FileUtils.listFiles(new File(reportsDir), new String[]{"json"}, true);
            ObjectMapper objectMapper = new ObjectMapper();

            for (File file : jsonFiles) {
                List<Map<String, Object>> jsonReport = objectMapper.readValue(file, new TypeReference<List<Map<String, Object>>>() {});
                combinedReport.addAll(jsonReport);
            }

            // Write the combined report to a single JSON file
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFile), combinedReport);
            System.out.println("Combined report written to " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
