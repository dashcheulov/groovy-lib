#!/usr/bin/groovy
/**
 * Pipeline for building python scripts to deb and pushing to OBS
 * Definition in Jenkinsfile for example
 * iowStdebBuild {
 *   project = 'iponweb:iowops:testing'
 *   stdebArgs = '--with-python2=True --with-python3=False'
 * }
 */

def call(Closure body) {
    def config = [ project: 'iponweb:iowops:testing', stdebArgs: '--with-python2=True --with-python3=True' ]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    
    buildPython {
        withNotification {
            stage('Checkout') {
                checkout scm
                def tag = sh script: 'hg log --limit 1 --template "{latesttag}"', returnStdout: true
                sh "hg up ${tag} && rm -r .hg"
                config.version = tag.find(/[\d.]+/)
                config.packageName = env.JOB_BASE_NAME + "_" + config.version
                sh "sed -i 's/version=.*/version='\\''${config.version}'\\'',/' setup.py" //override version
            }
            stage('Build') {
                sh "python3 setup.py --command-packages=stdeb.command sdist_dsc ${config.stdebArgs}"
            }
            pushtoOBS {
                packageName = 'python-' + config.packageName
                project = config.project
            }
        }
    }  
}
