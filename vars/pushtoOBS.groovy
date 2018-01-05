#!/usr/bin/groovy
/**
 * Template of stage of pushing to iow OBS
 * Definition in a pipeline for example
 *   pushtoOBS {
 *     packageName = PACKAGE
 *     srcDir = 'deb_dist'
 *     credentialsId = 'oscrc'
 *     project = 'iponweb:iowops:testing'
 *   }
 */
import static net.iponweb.Utilities.changeLogMsg

def call(Closure body) {
    def config = [ credentialsId: 'oscrc', project: 'iponweb:iowops:testing', oscrcFile: '/home/jenkins/.oscrc', src_dir: 'deb_dist' ]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    
    stage('Push to OBS') {
      withCredentials([string(credentialsId: config.credentialsId, variable: 'OSCRC')]) {
        writeFile file: config.oscrcFile, text: OSCRC.replace("\\n", "\n")
        apiurl = OSCRC.find(/(?!\[)https?:\/\/[\w.]*(?=\])/)
      }
      dir('obs') {
        sh "osc -A ${apiurl} init '${config.project}'"
        try { sh "osc -A ${apiurl} co '${config.packageName}' && rm ${PACKAGE}/*" }
        catch(e) { sh "osc -A ${apiurl} mkpac '${config.packageName}'" }
        sh "cp -v ../${config.src_dir}/* '${config.packageName}/'"
        dir(config.package) {
          sh "osc -A ${apiurl} ar"
          sh "osc -A ${apiurl} ci -m '${env.BUILD_TAG}\n\n${changeLogMsg(currentBuild.changeSets)}'"
        }
      }
    }   
}
