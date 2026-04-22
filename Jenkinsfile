pipeline {
    agent any

    environment {
        // Docker Hub repository
        DOCKER_IMAGE = "timo2233/clboost"
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
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Performance Test') {
            steps {
                script {
                    // Build Docker image
                    sh "docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
                    sh "docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest"
                    
                    // Start container in background
                    def container = sh(
                        script: "docker run -d -p 8081:8080 --name clboost-test ${DOCKER_IMAGE}:${BUILD_NUMBER}",
                        returnStdout: true
                    ).trim()
                    
                    try {
                        // Wait for application to start
                        sh '''
                            echo "Waiting for application to start on port 8081..."
                            for i in {1..30}; do
                                if curl -s http://localhost:8081/actuator/health > /dev/null 2>&1; then
                                    echo "Application is up!"
                                    break
                                fi
                                echo "Waiting... ($i/30)"
                                sleep 2
                                if [ $i -eq 30 ]; then
                                    echo "Application did not start within timeout"
                                    exit 1
                                fi
                            done
                        '''
                        
                        // Run JMeter tests
                        sh '''
                            echo "Running JMeter performance tests..."
                            mkdir -p tests/performance/reports
                            jmeter -n -t tests/performance/clboost_performance.jmx \
                                -Jhost=localhost -Jport=8081 \
                                -l tests/performance/result_${BUILD_NUMBER}.jtl \
                                -e -o tests/performance/report_${BUILD_NUMBER}
                        '''
                    } finally {
                        // Clean up container regardless of test outcome
                        sh "docker stop ${container}"
                        sh "docker rm ${container}"
                    }
                }
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

        stage('Deploy to Docker Hub') {
            when {
                expression { currentBuild.result == null || currentBuild.result == 'SUCCESS' }
            }
            steps {
                script {
                    // Push both tags to Docker Hub using stored credentials
                    withDockerRegistry(credentialsId: DOCKER_HUB_CREDS) {
                        docker.push("${DOCKER_IMAGE}:${BUILD_NUMBER}")
                        docker.push("${DOCKER_IMAGE}:latest")
                    }
                }
            }
        }
    }
}
