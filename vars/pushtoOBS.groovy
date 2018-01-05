#!/usr/bin/groovy
/**
 * Template of stage of pushing to iow OBS
 * Definition in a pipeline for example
 *   pushtoOBS {
 *     def package = PACKAGE
 *     def srcDir = 'deb_dist'
 *     def credentialsId = 'oscrc'
 *     def project = 'iponweb:iowops:testing'
 *   }
 */
import static net.iponweb.Utilities.changeLogMsg

def call(Closure body) {
    def config = [credentialsId: 'oscrc', project: 'iponweb:iowops:testing', oscrcFile: '/home/jenkins/.oscrc']
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    
    stage('Push to OBS') {
      withCredentials([string(credentialsId: config.credentialsId, variable: 'OSCRC')]) {
        writeFile file: config.oscrcFile, text: OSCRC.replace("\\n", "\n")
        String apiurl = OSCRC.find(/(?!\[)https?:\/\/[\w.]*(?=\])/)
      }
      dir('obs') {
        sh "osc -A ${apiurl} init '${config.project}'"
        try { sh "osc -A ${apiurl} co '${config.package}' && rm ${PACKAGE}/*" }
        catch(e) { sh "osc -A ${apiurl} mkpac '${config.package}'" }
        sh "cp -v ../${config.src_dir}/* '${config.package}/'"
        dir(config.package) {
          sh "osc -A ${apiurl} ar"
          sh "osc -A ${apiurl} ci -m '${env.BUILD_TAG}\n\n${changeLogMsg(currentBuild.changeSets)}'"
        }
      }
    }   
}
