pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code...'
                checkout scm
            }
        }

        stage('Build All Services') {
            agent {
                docker {
                    image 'maven:3.9-eclipse-temurin-21'
                    args '-v $HOME/.m2:/root/.m2' // кэш Maven зависимостей
                }
            }
            steps {
                echo 'Building all services with Maven...'
                sh 'mvn -f grpc-server/pom.xml clean package -DskipTests'
                sh 'mvn -f simple-notification-service/pom.xml clean package -DskipTests'
                sh 'mvn -f statistics-service/pom.xml clean package -DskipTests'
                sh 'mvn -f steamapi/pom.xml clean package -DskipTests'
                sh 'mvn -f steam-audit/pom.xml clean package -DskipTests'
                sh 'mvn -f SteamEvents/pom.xml clean package -DskipTests'
                sh 'mvn -f steammicro/pom.xml clean package -DskipTests'
            }
        }
    }

    post {
        success {
            echo 'Build completed successfully!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}
