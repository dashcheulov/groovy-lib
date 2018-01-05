#!/usr/bin/groovy
def call(Closure body) {
  try {
    body.this.iow.notifyBuild body.this.currentBuild
    body()
    body.this.currentBuild.result = "SUCCESS"
  } catch (e) {
    body.this.currentBuild.result = "FAILURE"
    throw e
  } finally {
    body.this.iow.notifyBuild currentBuild
  }
}
