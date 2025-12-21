pipeline {
    agent any

    stages {
        stage('Build Services') {
            steps {
                dir('grpc-server') {
                    sh './mvnw clean package -DskipTests'
                }

                dir('simple-notification-service') {
                    sh './mvnw clean package -DskipTests'
                }

                dir('steam-audit') {
                    sh './mvnw clean package -DskipTests'
                }

                dir('steammicro') {
                    sh './mvnw clean package -DskipTests'
                }
            }
        }
    }

    post {
        success {
            echo 'All services built successfully!'
        }
        failure {
            echo 'Build failed for one or more services.'
        }
    }
}