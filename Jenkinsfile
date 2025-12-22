pipeline {
    agent any

    stages {
        stage('Build Services') {
            steps {
                dir('grpc-server') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
                }

                dir('simple-notification-service') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
                }

                dir('SteamEvents') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
                }

                dir('steamapi') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
                }

                dir('statistics-service') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
                }

                dir('steam-audit') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
                }

                dir('steammicro') {
                    sh 'chmod +x mvnw'
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