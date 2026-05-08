// Script post-generación: elimina módulo 'code' si no se solicitó.
// El binding típico del archetype expone: request, project, archetypeArtifactId, properties
import groovy.xml.*

def outputDir = (binding.hasVariable('request') && request?.outputDirectory) ? new File(request.outputDirectory, request.artifactId) : new File('.').canonicalFile
def props = binding.hasVariable('properties') ? properties : [:]

def toBool = { val ->
    if(val == null) return null
    def s = val.toString().trim().toLowerCase()
    if(s in ['true','false']) return s == 'true'
    return null
}

def includeCodeModule = null
// 1. Map de properties estándar
includeCodeModule = toBool(props['includeCodeModule'])
// 2. request.properties (algunas versiones del plugin exponen aquí)
if(includeCodeModule == null && binding.hasVariable('request')){
    def rp = request?.properties
    if(rp instanceof Map){
        includeCodeModule = toBool(rp['includeCodeModule'])
    }
}
// 3. System property (por si se pasó -DincludeCodeModule=true)
if(includeCodeModule == null){
    includeCodeModule = toBool(System.getProperty('includeCodeModule'))
}
// 4. archetype.properties generado
if(includeCodeModule == null){
    def propFile = new File(outputDir, 'archetype.properties')
    if(propFile.exists()){
        def p = new Properties()
        propFile.withInputStream { p.load(it) }
        includeCodeModule = toBool(p.getProperty('includeCodeModule'))
    }
}
// 5. Default final
if(includeCodeModule == null){
    includeCodeModule = false
}

if(!includeCodeModule){
    def pomFile = new File(outputDir, 'pom.xml')
    if(pomFile.exists()){
        def text = pomFile.getText('UTF-8')
        def parser = new XmlParser(false, false)
        def projectNode = parser.parseText(text)
        def modulesNode = projectNode.modules?.getAt(0)
        if(modulesNode){
            def codeModule = modulesNode.module.find { it.text() == 'code' }
            if(codeModule){
                modulesNode.remove(codeModule)
                def sw = new StringWriter()
                def printer = new XmlNodePrinter(new PrintWriter(sw))
                printer.preserveWhitespace = true
                printer.print(projectNode)
                pomFile.write(sw.toString(), 'UTF-8')
                println "[post-generate] Removido módulo 'code' por includeCodeModule=false"
            }
        }
    }
    def codeDir = new File(outputDir, 'code')
    if(codeDir.exists()){
        codeDir.deleteDir()
        println "[post-generate] Eliminada carpeta 'code'"
    }
} else {
    println "[post-generate] Manteniendo módulo 'code' (includeCodeModule=true)"
}

def gitignoreFile = new File(outputDir, '.gitignore')
if (!gitignoreFile.exists()) {
    gitignoreFile.write('''.idea/
.vscode/
.classpath
.project
.settings/
*.iml

target/
*/target/

.DS_Store
''', 'UTF-8')
    println "[post-generate] Generado fichero '.gitignore'"
}

// Fallback: si por alguna razón el filtrado no sustituyó ${groupId}, ${artifactId}, ${version}
// en los POM hijos, sustituirlos manualmente usando los valores reales del POM padre.
try {
    def parentPom = new File(outputDir, 'pom.xml')
    // Intentar obtener la versión de Karate y el package suministrados
        def kVersion = props['karateVersion'] ?: (binding.hasVariable('request') ? request?.properties?.get('karateVersion') : null) ?: System.getProperty('karateVersion') ?: '2.0.2'
    def pkgValue = props['package'] ?: (binding.hasVariable('request') ? request?.properties?.get('package') : null) ?: System.getProperty('package')
    if(parentPom.exists()){
        def parentTxt = parentPom.getText('UTF-8')
        def parentXml = new XmlParser(false,false).parseText(parentTxt)
        def pGroupId = parentXml.groupId?.text() ?: parentXml.parent?.groupId?.text()
        def pArtifactId = parentXml.artifactId?.text()
        def pVersion = parentXml.version?.text() ?: parentXml.parent?.version?.text()
        [new File(outputDir,'e2e/karate/pom.xml'), new File(outputDir,'code/pom.xml'),
         new File(outputDir,'e2e/karate/src/test/java'), new File(outputDir,'code/src/main/java'),
         new File(outputDir,'code/src/test/java')].each { f ->
            if(f.isDirectory()){ // recorrer .java si hay placeholders package
                f.eachFileRecurse { ff ->
                    if(ff.name.endsWith('.java')){
                        def rawJava = ff.getText('UTF-8')
                        if(rawJava.contains('${package}') && pkgValue){
                            ff.write(rawJava.replace('${package}', pkgValue),'UTF-8')
                            println "[post-generate] Fallback aplicado en ${ff.path} (package reemplazado)."
                        }
                    }
                }
                return
            }
            if(f.exists()){
                def raw = f.getText('UTF-8')
                if(raw.contains('${groupId}') || raw.contains('${artifactId}') || raw.contains('${version}') || raw.contains('${karateVersion}') || (raw.contains('${package}') && pkgValue)){
                    def replaced = raw
                        .replace('${groupId}', pGroupId ?: '')
                        .replace('${artifactId}', pArtifactId ?: '')
                        .replace('${version}', pVersion ?: '')
                        .replace('${karateVersion}', kVersion)
                        .replace('${package}', pkgValue ?: '')
                    f.write(replaced,'UTF-8')
                    println "[post-generate] Fallback aplicado en ${f.path} (placeholders reemplazados)."
                }
            }
        }
    }
} catch(Exception ex){
    println "[post-generate][WARN] Error en fallback de reemplazo de placeholders: ${ex.message}"
}

// Final guard: fail fast if any unresolved placeholders remain in generated files.
def unresolvedPattern = ~/\$\{(groupId|artifactId|version|package|karateVersion)\}/
def unresolvedFiles = []
outputDir.eachFileRecurse { f ->
    if (f.isFile() && (f.name.endsWith('.xml') || f.name.endsWith('.java') || f.name.endsWith('.feature') || f.name.endsWith('.yml') || f.name.endsWith('.yaml'))) {
        if (f.getText('UTF-8') =~ unresolvedPattern) {
            unresolvedFiles << f.path
        }
    }
}
if (unresolvedFiles) {
    def msg = "[post-generate][ERROR] Unresolved placeholders found in generated files:\n" + unresolvedFiles.collect { "  - $it" }.join('\n')
    throw new RuntimeException(msg)
}
println "[post-generate] All placeholders resolved successfully."
