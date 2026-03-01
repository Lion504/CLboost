pipeline {
    agent any

    environment {
        // Define your Docker Hub info here
        DOCKER_IMAGE = "tfabinader/sp1-inclass-assignment"
        // This ID must match the 'ID' you gave your credentials in Jenkins
        DOCKER_HUB_CREDS = 'docker-hub-creds'
    }

    tools {
        // This name must match the "Name" you gave the JDK in Jenkins Global Tool Configuration
        jdk 'JDK25'
        maven 'Maven3'
    }

    stages {
        stage('Checkout') {
            steps {
                // Added credentialsId for SSH authentication
                git url: 'git@github.com:TaysaAbinader/SoftwareProject1_Actvity1.git',
                    branch: 'main'
            }
        }
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

        stage('Push to Docker Hub') {
            steps {
                script {
                    // This block handles the 'docker login' and 'docker logout' automatically
                    withDockerRegistry([credentialsId: "${DOCKER_HUB_CREDS}", url: '']) {
                        sh "docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}"
                        sh "docker push ${DOCKER_IMAGE}:latest"
                    }
                }
            }
        }
    }
}
