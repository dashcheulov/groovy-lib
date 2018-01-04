#!/usr/bin/env groovy
package net.iponweb
class Utilities {
  static def notifyBuild(script, String buildStatus) {
    buildStatus =  buildStatus ?: 'SUCCESSFUL'
    def subject = "${script.env.JOB_NAME} - #${script.env.BUILD_NUMBER} ${buildStatus}"
    def summary = "${subject} (<${script.env.BUILD_URL}|Open>)"
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
    if (buildStatus == 'STARTED') {
      def color = 'YELLOW'
      def colorCode = '#EEEE00'
    } else if (buildStatus == 'SUCCESSFUL') {
      def color = 'GREEN'
      def colorCode = '#00AA00'
      summary = "${subject} after ${btime} (<${script.env.BUILD_URL}|Open>)\n${changeLogMsg}"
    } else {
      def color = 'RED'
      def colorCode = '#AA0000'
      summary = "${subject} after ${btime} (<${script.env.BUILD_URL}|Open>)\n${changeLogMsg}"
    }
    script.slackSend (color: colorCode, message: summary)
  }
}
