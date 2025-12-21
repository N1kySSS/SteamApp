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
                echo 'Building all services with Maven...'
                
                // Сборка grpc-server
                sh 'mvn -f grpc-server/pom.xml clean package -DskipTests'
                
                // Сборка simple-notification-service
                sh 'mvn -f simple-notification-service/pom.xml clean package -DskipTests'
                
                // Сборка statistics-service
                sh 'mvn -f statistics-service/pom.xml clean package -DskipTests'
                
                // Сборка steamapi
                sh 'mvn -f steamapi/pom.xml clean package -DskipTests'
                
                // Сборка steam-audit
                sh 'mvn -f steam-audit/pom.xml clean package -DskipTests'
                
                // Сборка SteamEvents
                sh 'mvn -f SteamEvents/pom.xml clean package -DskipTests'
                
                // Сборка steammicro
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
