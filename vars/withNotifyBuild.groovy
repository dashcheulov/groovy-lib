#!/usr/bin/groovy
def call(Closure body) {
  try {
    this.iow.notifyBuild this.currentBuild
    body()
    this.currentBuild.result = "SUCCESS"
  } catch (e) {
    this.currentBuild.result = "FAILURE"
    throw e
  } finally {
    this.iow.notifyBuild this.currentBuild
  }
}
