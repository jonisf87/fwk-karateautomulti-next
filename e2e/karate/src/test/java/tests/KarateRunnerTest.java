package tests;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;  // Correct import for AfterAll in JUnit 5
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        Configuration config = new Configuration(new File("target"), "Logista API testing");
        ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
        reportBuilder.generateReports();
    }

    private void runPostTestScript() {
        System.out.println("Starting .json reports combination...");
        try {
            ProcessBuilder pb = new ProcessBuilder("node", "combine-reports.js");
            pb.directory(new File(".")); // Set to the directory where your script is located
            Process process = pb.start();
            process.waitFor();
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                System.err.println("Script execution failed with exit code " + exitCode);
            }
            System.out.println("Finished: you can check cucumber-result.json in target directory");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

