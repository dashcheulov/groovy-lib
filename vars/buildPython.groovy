#!/usr/bin/groovy
def call(Closure body) {
  podTemplate(label: 'build-python', cloud: 'Kubernetes', containers: [
    containerTemplate(name: 'build-python', image: 'artifactory.iponweb.net:5000/iowops/build-python:latest', ttyEnabled: true, command: 'cat', alwaysPullImage: false),
  ],
    imagePullSecrets: [ 'iow-artifactory' ]) {
    node('build-python') {
      container('build-python') {
        body()
      }
    }
  }
} 
