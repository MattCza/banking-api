pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        COMPOSE_PROJECT_NAME = 'money-ci'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Backend') {
            steps {
                bat 'mvn -pl app-backend -am clean package -DskipTests'
            }
        }

        stage('Start Test Environment') {
            steps {
                bat 'docker compose down -v --remove-orphans'
                bat 'copy .env.example .env'
                bat 'docker compose up -d --build postgres-db app-backend'
                bat '''powershell -NoProfile -Command "$deadline = (Get-Date).AddMinutes(3); do { try { $response = Invoke-WebRequest -UseBasicParsing http://localhost:8080/actuator/health; if ($response.StatusCode -eq 200) { exit 0 } } catch { Start-Sleep -Seconds 5 } } while ((Get-Date) -lt $deadline); Write-Error 'app-backend did not become ready in time'; exit 1"'''
            }
        }

        stage('Run API Tests') {
            steps {
                bat 'mvn -pl testing-framework -am verify -Denv=local'
            }
        }
    }

    post {
        always {
            bat 'docker compose logs --no-color > docker-compose.log || exit /b 0'
            junit testResults: 'testing-framework/target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'docker-compose.log,testing-framework/target/surefire-reports/*', fingerprint: true
            bat 'docker compose down -v --remove-orphans || exit /b 0'
            cleanWs()
        }
    }
}
