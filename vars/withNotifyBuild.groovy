#!/usr/bin/groovy
def call(Closure body) {
  def g = body.delegate
  try {
    g.iow.notifyBuild g.currentBuild
    body()
    g.currentBuild.result = "SUCCESS"
  } catch (e) {
    g.currentBuild.result = "FAILURE"
    throw e
  } finally {
    g.iow.notifyBuild g.currentBuild
  }
}
