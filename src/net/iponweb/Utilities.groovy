#!/usr/bin/env groovy
package net.iponweb
class Utilities {
  static def notifyBuild(script, String buildStatus) {
    buildStatus =  buildStatus ?: 'SUCCESS'
    def subject = "${script.env.JOB_NAME} - #${script.env.BUILD_NUMBER} ${buildStatus}"
    def colorCode = '#00AA00'
    def changeLogMsg = 'Changes:\n'
    if (script.currentBuild.changeSets.size() >= 1) {
        def entries = script.currentBuild.changeSets[0].items
        for (int j = 0; j < entries.length; j++) {
            changeLogMsg = changeLogMsg + "- ${entries[j].msg} [${entries[j].author}]\n"
        }
    }
    long bt = (System.currentTimeMillis() - script.currentBuild.startTimeInMillis) / 1000
    long btm = bt/60
    long bts = bt%60
    def btime = String.format("%d min %02d sec", btm, bts)
    def summary = "${subject} after ${btime} (<${script.env.BUILD_URL}|Open>)\n${changeLogMsg}"
    if (buildStatus == 'STARTED') {
      colorCode = '#EEEE00'
      summary = "${subject} (<${script.env.BUILD_URL}|Open>)"
    } else if (buildStatus == 'FAILURE') {
      colorCode = '#AA0000'
    }
    script.slackSend (color: colorCode, message: summary)
  }
}
