package ${package}.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.karatelabs.core.Runner;
import io.karatelabs.core.SuiteResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

final class KarateExecutionSpec {

  private static final Path REPORT_DIRECTORY = Path.of("target", "karate-reports");
  private static final String REPORT_NAME = "Example Corp API testing";
  private static final Pattern TAG_PATTERN = Pattern.compile("--tags\\s+([^\\s]+)");

  @Test
  void runKarateSuite() throws IOException, InterruptedException {
    String env = System.getProperty("karate.env", "");
    String[] tags = resolveTags(System.getProperty("karate.options", ""));

    Runner.Builder runner =
        Runner.path("classpath:tests")
            .outputDir(REPORT_DIRECTORY)
            .outputHtmlReport(true)
            .outputCucumberJson(true)
            .outputJunitXml(true);
    if (!env.isBlank()) {
      runner = runner.karateEnv(env);
    }
    if (tags.length > 0) {
      runner = runner.tags(tags);
    }

    SuiteResult result = runner.parallel(5);

    generateReport(result.getReportDir().toString());

    assertTrue(result.isPassed(), String.join(System.lineSeparator(), result.getErrors()));
  }

  private String[] resolveTags(String karateOptions) {
    List<String> tags = new ArrayList<>();
    Matcher matcher = TAG_PATTERN.matcher(karateOptions);
    while (matcher.find()) {
      for (String tag : matcher.group(1).split(",")) {
        if (!tag.isBlank()) {
          tags.add(tag.trim());
        }
      }
    }
    if (!tags.isEmpty()) {
      return tags.toArray(String[]::new);
    }
    return new String[0];
  }

  private void generateReport(String karateOutputPath) {
    Collection<File> jsonFiles =
        FileUtils.listFiles(new File(karateOutputPath), new String[] {"json"}, true);
    List<String> jsonPaths = new ArrayList<>(jsonFiles.size());
    jsonFiles.forEach(file -> jsonPaths.add(file.getAbsolutePath()));

    Configuration config = new Configuration(new File("target"), REPORT_NAME);
    ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
    reportBuilder.generateReports();
  }
}
