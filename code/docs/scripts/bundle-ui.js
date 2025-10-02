#!/usr/bin/env node
/**
 * Bundle the extracted Antora UI folder into ui/ui-bundle.zip, injecting overrides first.
 * This script is cross-platform and avoids relying on bash/zip.
 */
const fs = require('fs');
const path = require('path');
const archiver = require('archiver');

const root = path.resolve(__dirname, '..');
const uiDir = path.join(root, 'ui');
const extractDir = path.join(uiDir, 'ui-bundle-extract');
const supplementalDir = path.join(uiDir, 'supplemental', 'partials');
const outZip = path.join(uiDir, 'ui-bundle.zip');

function copyIfExists(src, dest) {
  if (fs.existsSync(src)) {
    fs.copyFileSync(src, dest);
  }
}

function injectOverrides() {
  const targets = ['header-content.hbs', 'footer-content.hbs', 'footer-scripts.hbs', 'nav-explore.hbs'];
  for (const f of targets) {
    const from = path.join(supplementalDir, f);
    const to = path.join(extractDir, 'partials', f);
    if (fs.existsSync(from) && fs.existsSync(path.dirname(to))) {
      fs.copyFileSync(from, to);
      console.log(`Injected override: ${f}`);
    }
  }
}

async function zipDir(srcDir, destZip) {
  await fs.promises.mkdir(path.dirname(destZip), { recursive: true });
  if (fs.existsSync(destZip)) fs.unlinkSync(destZip);
  return new Promise((resolve, reject) => {
    const output = fs.createWriteStream(destZip);
    const archive = archiver('zip', { zlib: { level: 9 } });
    output.on('close', () => resolve());
    archive.on('error', err => reject(err));
    archive.pipe(output);
    archive.directory(srcDir + '/', false);
    archive.finalize();
  });
}

(async () => {
  try {
    if (!fs.existsSync(extractDir)) {
      console.error(`Missing extracted UI folder: ${extractDir}`);
      process.exit(1);
    }
    injectOverrides();
    await zipDir(extractDir, outZip);
    console.log(`Created ${path.relative(root, outZip)}`);
  } catch (err) {
    console.error(err);
    process.exit(1);
  }
})();
