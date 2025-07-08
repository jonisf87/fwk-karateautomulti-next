const fs = require('fs');
const path = require('path');

// Directory containing individual JSON reports
const reportsDir = 'target/karate-reports';
const outputFile = 'target/cucumber-result.json';

// Read all JSON files from the reports directory
fs.readdir(reportsDir, (err, files) => {
  if (err) throw err;

  const jsonReports = files
    .filter(file => file.endsWith('.json'))
    .map(file => {
      const filePath = path.join(reportsDir, file);
      return JSON.parse(fs.readFileSync(filePath, 'utf8'));
    });

  // Combine all JSON reports into one
  const combinedReport = jsonReports.reduce((acc, report) => {
    acc.push(...report);
    return acc;
  }, []);

  // Write the combined report to a single JSON file
  fs.writeFileSync(outputFile, JSON.stringify(combinedReport, null, 2), 'utf8');
  console.log(`Combined report written to ${outputFile}`);
});
