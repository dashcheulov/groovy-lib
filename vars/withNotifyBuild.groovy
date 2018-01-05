#!/usr/bin/groovy
import net.iponweb.Utilities

def call(Closure body) {
  def iow = new Utilities(this)
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
