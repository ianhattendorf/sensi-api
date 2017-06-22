#!/usr/bin/env groovy

pipeline {
    agent {
        label 'linux&&jdk8'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                sh 'mvn -B clean test-compile'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn -B verify -DskipITs' // TODO set up secret handling
            }
            post {
                always {
                    junit 'target/surefire-reports/TEST-*.xml'
                }
            }
        }
    }
}
