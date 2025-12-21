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
            steps {
                echo 'Building all services...'
                sh 'mvn -f grpc-server/pom.xml clean package -DskipTests'
                sh 'mvn -f simple-notification-service/pom.xml clean package -DskipTests'
                sh 'mvn -f steam-audit/pom.xml clean package -DskipTests'
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
