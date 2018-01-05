#!/usr/bin/groovy
def call(Closure body) {
  try {
    iow.notifyBuild currentBuild
    body()
    currentBuild.result = "SUCCESS"
  } catch (e) {
    currentBuild.result = "FAILURE"
    throw e
  } finally {
    iow.notifyBuild currentBuild
  }
}
