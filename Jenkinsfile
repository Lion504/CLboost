pipeline {
    agent any

    environment {
        // Define your Docker Hub info here
        DOCKER_IMAGE = "tfabinader/sp1-inclass-assignment"
        // This ID must match the 'ID' you gave your credentials in Jenkins
        DOCKER_HUB_CREDS = 'docker-hub-creds'
        // Jenkins global environment variable (set in Manage Jenkins → Configure System)
        PATH = "${env.JMETER_HOME}/bin:${env.PATH}"
    }

    tools {
        // This name must match the "Name" you gave the JDK in Jenkins Global Tool Configuration (Java 21)
        jdk 'JDK21'
        maven 'Maven3'
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Code Coverage') {
            steps {
                sh 'mvn jacoco:report'
            }
        }
        stage('Publish Test Results') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }
        stage('Publish Coverage Report') {
            steps {
                jacoco()
            }
        }

        stage('Static Code Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar'
                }
            }
        }
        stage('Quality Gate') {
            steps {
                script {
                    def qg = waitForQualityGate()
                    if (qg.status != 'OK') {
                        error "SonarQube quality gate failed: ${qg.status}"
                    }
                }
            }
        }

        stage('Performance Test') {
            steps {
                // Run JMeter performance tests against locally running app instance
                sh '''
                    echo "Running JMeter performance tests..."
                    mkdir -p tests/performance/reports
                    jmeter -n -t tests/performance/clboost_performance.jmx -l tests/performance/result_${BUILD_NUMBER}.jtl -e -o tests/performance/report_${BUILD_NUMBER}
                '''
            }
            post {
                always {
                    // Archive raw JTL results
                    archiveArtifacts artifacts: 'tests/performance/result_*.jtl', allowEmptyArchive: true
                    // Archive HTML performance reports
                    archiveArtifacts artifacts: 'tests/performance/report_*/**/*', allowEmptyArchive: true
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Builds using the Dockerfile in your root directory
                    // We tag it with the Jenkins build number for versioning
                    sh "docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
                    sh "docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest"
                }
            }
        }
    }
}
