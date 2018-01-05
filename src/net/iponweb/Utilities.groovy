#!/usr/bin/env groovy
package net.iponweb
class Utilities implements Serializable {
  def g
  Utilities(script) {this.g = script}
  def notifyBuild(currentBuild) {
    String buildStatus = currentBuild.result ?: 'STARTED'
    String subject = "${g.env.JOB_NAME} - #${g.env.BUILD_NUMBER} ${buildStatus}"
    String colorCode = '#EEEE00'
    String summary = "${subject} (<${g.env.BUILD_URL}|Open>)"
    if (buildStatus != 'STARTED') {
      String changeLogMsg = 'Changes:\n'
      if (currentBuild.changeSets.size() >= 1) {
          def entries = currentBuild.changeSets[0].items
          for (int j = 0; j < entries.length; j++) {
              changeLogMsg = changeLogMsg + "- ${entries[j].msg} [${entries[j].author}]\n"
          }
      } else {
          changeLogMsg = 'No changes\n'
      }
      long bt = (System.currentTimeMillis() - currentBuild.startTimeInMillis) / 1000
      long btm = bt/60
      long bts = bt%60
      String btime = String.format("%d min %02d sec", btm, bts)
      summary = "${subject} after ${btime} (<${g.env.BUILD_URL}|Open>)\n${changeLogMsg}"
      colorCode = buildStatus == 'SUCCESS' ? '#00AA00' : '#AA0000'
    }
    g.slackSend (color: colorCode, message: summary)
  }
}
