#!/usr/bin/groovy
def call(Closure body) {
  try {
    body.getThisObject().iow.notifyBuild body.getThisObject().currentBuild
    body()
    body.getThisObject().currentBuild.result = "SUCCESS"
  } catch (e) {
    body.getThisObject().currentBuild.result = "FAILURE"
    throw e
  } finally {
    body.getThisObject().iow.notifyBuild body.getThisObject().currentBuild
  }
}
