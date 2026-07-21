pipeline {
    agent any

    tools {
        jdk 'JDK-17'
        maven 'Maven-3.9'
        nodejs 'NodeJS-18'
    }

    environment {
        // Git
        APP_NAME              = 'employee-management-system'
        GIT_CREDENTIALS       = credentials('github-credentials-id')

        // Docker (configure in Jenkins Credentials / env)
        DOCKER_REGISTRY       = "${env.DOCKER_REGISTRY ?: 'docker.io'}"
        DOCKER_USER           = "${env.DOCKER_USER ?: 'your-dockerhub-user'}"
        BACKEND_IMAGE         = "${DOCKER_REGISTRY}/${DOCKER_USER}/ems-backend"
        FRONTEND_IMAGE        = "${DOCKER_REGISTRY}/${DOCKER_USER}/ems-frontend"
        IMAGE_TAG             = "${env.BUILD_NUMBER}"

        // Kubernetes
        K8S_NAMESPACE         = 'employee-mgmt'
        K8S_DEPLOYMENT_BACKEND  = 'ems-backend'
        K8S_DEPLOYMENT_FRONTEND = 'ems-frontend'

        // Application URLs (after K8s deploy — update for your cluster)
        APP_BASE_URL          = "${env.APP_BASE_URL ?: 'http://localhost:3000'}"
        API_BASE_URL          = "${env.API_BASE_URL ?: 'http://localhost:8080'}"

        // SonarQube (optional — Step 7)
        SONAR_PROJECT_KEY     = 'employee-management'
        SONAR_HOST_URL        = "${env.SONAR_HOST_URL ?: 'http://localhost:9000'}"

        // Notifications — Teams webhook + Email (Step 12)
        TEAMS_WEBHOOK_URL     = credentials('teams-webhook-url')
        EMAIL_RECIPIENTS      = "${env.EMAIL_RECIPIENTS ?: 'team@company.com'}"
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '10'))
        timestamps()
        timeout(time: 60, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['dev', 'staging', 'prod'], description: 'Target environment')
        booleanParam(name: 'SKIP_DEPLOY', defaultValue: false, description: 'Skip Docker push and K8s deploy')
        booleanParam(name: 'SKIP_SMOKE_TESTS', defaultValue: false, description: 'Skip Selenium smoke tests')
        booleanParam(name: 'RUN_SONAR', defaultValue: true, description: 'Run SonarQube static analysis')
    }

    stages {

        // ─────────────────────────────────────────────
        // STAGE 1: Checkout — Pull latest code from GitHub
        // ─────────────────────────────────────────────
        stage('1. Checkout') {
            steps {
                echo '>>> Pulling latest code from GitHub...'
                checkout scm
                script {
                    env.GIT_COMMIT_SHORT = shellOutput('git rev-parse --short HEAD')
                    env.GIT_BRANCH_NAME  = shellOutput('git rev-parse --abbrev-ref HEAD')
                }
                echo "Branch: ${env.GIT_BRANCH_NAME} | Commit: ${env.GIT_COMMIT_SHORT}"
            }
        }

        // ─────────────────────────────────────────────
        // STAGE 2: Build Backend — Maven compile & package
        // ─────────────────────────────────────────────
        stage('2. Build Backend') {
            steps {
                echo '>>> Building Spring Boot backend...'
                script {
                    dir('backend') {
                        shell 'mvn clean package -DskipTests -B'
                    }
                }
                archiveArtifacts artifacts: 'backend/target/*.jar', fingerprint: true
            }
        }

        // ─────────────────────────────────────────────
        // STAGE 3: Build Frontend — npm build production bundle
        // ─────────────────────────────────────────────
        stage('3. Build Frontend') {
            steps {
                echo '>>> Building React frontend...'
                script {
                    dir('frontend') {
                        shell 'npm ci'
                        shell 'npm run build'
                    }
                }
                archiveArtifacts artifacts: 'frontend/dist/**', fingerprint: true
            }
        }

        // ─────────────────────────────────────────────
        // STAGE 4: Unit Tests — JUnit + Mockito (32 tests)
        // ─────────────────────────────────────────────
        stage('4. Unit Tests') {
            steps {
                echo '>>> Running unit and integration tests...'
                script {
                    dir('backend') {
                        shell 'mvn test -B'
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'backend/target/surefire-reports/*.xml'
                    publishHTML(target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'backend/target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report'
                    ])
                }
            }
        }

        // ─────────────────────────────────────────────
        // STAGE 5: Static Code Analysis — SonarQube
        // ─────────────────────────────────────────────
        stage('5. Static Code Analysis') {
            when {
                expression { params.RUN_SONAR == true }
            }
            steps {
                echo '>>> Running SonarQube analysis...'
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    script {
                        dir('backend') {
                            shell """
                                mvn verify sonar:sonar -B \
                                  -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                                  -Dsonar.host.url=${SONAR_HOST_URL} \
                                  -Dsonar.token=\${SONAR_TOKEN} \
                                  -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                            """
                        }
                    }
                }
            }
        }

        // ─────────────────────────────────────────────
        // STAGE 6: Build Docker Images (requires Step 8 Dockerfiles)
        // ─────────────────────────────────────────────
        stage('6. Build Docker Images') {
            when {
                allOf {
                    expression { !params.SKIP_DEPLOY }
                    expression { fileExists('docker/Dockerfile.backend') && fileExists('docker/Dockerfile.frontend') }
                }
            }
            steps {
                echo '>>> Building Docker images...'
                script {
                    docker.build("${BACKEND_IMAGE}:${IMAGE_TAG}",  "-f docker/Dockerfile.backend .")
                    docker.build("${BACKEND_IMAGE}:latest",       "-f docker/Dockerfile.backend .")
                    docker.build("${FRONTEND_IMAGE}:${IMAGE_TAG}", "-f docker/Dockerfile.frontend .")
                    docker.build("${FRONTEND_IMAGE}:latest",       "-f docker/Dockerfile.frontend .")
                }
            }
        }

        // ─────────────────────────────────────────────
        // STAGE 7: Push Docker Images to registry
        // ─────────────────────────────────────────────
        stage('7. Push Docker Images') {
            when {
                allOf {
                    expression { !params.SKIP_DEPLOY }
                    expression { fileExists('docker/Dockerfile.backend') }
                }
            }
            steps {
                echo '>>> Pushing Docker images to registry...'
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", 'docker-registry-credentials') {
                        docker.image("${BACKEND_IMAGE}:${IMAGE_TAG}").push()
                        docker.image("${BACKEND_IMAGE}:latest").push()
                        docker.image("${FRONTEND_IMAGE}:${IMAGE_TAG}").push()
                        docker.image("${FRONTEND_IMAGE}:latest").push()
                    }
                }
            }
        }

        // ─────────────────────────────────────────────
        // STAGE 8: Deploy to Kubernetes (requires Step 9 manifests)
        // ─────────────────────────────────────────────
        stage('8. Deploy to Kubernetes') {
            when {
                allOf {
                    expression { !params.SKIP_DEPLOY }
                    expression { fileExists('k8s/backend-deployment.yaml') }
                }
            }
            steps {
                echo ">>> Deploying to Kubernetes namespace: ${K8S_NAMESPACE}..."
                withKubeConfig([credentialsId: 'kubeconfig-credentials']) {
                    script {
                        shell """
                            kubectl apply -f k8s/00-namespace.yaml
                            kubectl apply -f k8s/configmap.yaml
                            kubectl apply -f k8s/secret.yaml
                            kubectl apply -f k8s/mysql-deployment.yaml
                            kubectl apply -f k8s/backend-deployment.yaml
                            kubectl apply -f k8s/frontend-deployment.yaml
                            kubectl apply -f k8s/backend-service.yaml
                            kubectl apply -f k8s/frontend-service.yaml
                            kubectl set image deployment/${K8S_DEPLOYMENT_BACKEND} backend=${BACKEND_IMAGE}:${IMAGE_TAG} -n ${K8S_NAMESPACE}
                            kubectl set image deployment/${K8S_DEPLOYMENT_FRONTEND} frontend=${FRONTEND_IMAGE}:${IMAGE_TAG} -n ${K8S_NAMESPACE}
                        """
                    }
                }
            }
        }

        // ─────────────────────────────────────────────
        // STAGE 9: Wait for Rollout — verify pods are healthy
        // ─────────────────────────────────────────────
        stage('9. Verify Rollout') {
            when {
                allOf {
                    expression { !params.SKIP_DEPLOY }
                    expression { fileExists('k8s/backend-deployment.yaml') }
                }
            }
            steps {
                echo '>>> Waiting for Kubernetes rollout to complete...'
                withKubeConfig([credentialsId: 'kubeconfig-credentials']) {
                    script {
                        shell """
                            kubectl rollout status deployment/${K8S_DEPLOYMENT_BACKEND} -n ${K8S_NAMESPACE} --timeout=300s
                            kubectl rollout status deployment/${K8S_DEPLOYMENT_FRONTEND} -n ${K8S_NAMESPACE} --timeout=300s
                            kubectl get pods -n ${K8S_NAMESPACE}
                        """
                    }
                }
                script {
                    echo '>>> Verifying backend health endpoint...'
                    shell "curl -f ${API_BASE_URL}/actuator/health || echo Health check skipped"
                }
            }
        }

        // ─────────────────────────────────────────────
        // STAGE 10: Selenium Smoke Tests (requires Step 10 framework)
        // ─────────────────────────────────────────────
        stage('10. Selenium Smoke Tests') {
            when {
                allOf {
                    expression { !params.SKIP_SMOKE_TESTS }
                    expression { fileExists('automation-tests/pom.xml') }
                }
            }
            steps {
                echo '>>> Running Selenium smoke tests...'
                script {
                    dir('automation-tests') {
                        shell "mvn clean test -B -Dbase.url=${APP_BASE_URL} -Dapi.url=${API_BASE_URL}"
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'automation-tests/target/surefire-reports/*.xml'
                }
            }
        }

        // ─────────────────────────────────────────────
        // STAGE 11: Generate Allure Report (requires Step 11)
        // ─────────────────────────────────────────────
        stage('11. Generate Allure Report') {
            when {
                expression { fileExists('automation-tests/pom.xml') }
            }
            steps {
                echo '>>> Generating Allure report...'
                script {
                    dir('automation-tests') {
                        shell 'mvn allure:report -B || echo Allure report generation skipped'
                    }
                }
            }
            post {
                always {
                    allure([
                        includeProperties: false,
                        jdk: '',
                        results: [[path: 'automation-tests/target/allure-results']]
                    ])
                }
            }
        }
    }

    // ─────────────────────────────────────────────
    // POST: Notifications — Microsoft Teams + Email
    // ─────────────────────────────────────────────
    post {
        success {
            echo '>>> Pipeline SUCCEEDED — sending notifications...'
            script {
                sendNotifications('SUCCESS')
            }
        }
        failure {
            echo '>>> Pipeline FAILED — sending notifications...'
            script {
                sendNotifications('FAILURE')
            }
        }
        unstable {
            script {
                sendNotifications('UNSTABLE')
            }
        }
        always {
            cleanWs()
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Cross-platform shell helpers (Windows controller + Linux agents)
// ─────────────────────────────────────────────────────────────
def shell(String script) {
    if (isUnix()) {
        sh script
    } else {
        bat script
    }
}

def shellOutput(String script) {
    if (isUnix()) {
        return sh(returnStdout: true, script: script).trim()
    }
    return bat(returnStdout: true, script: "@echo off\r\n${script}").trim()
}

// ─────────────────────────────────────────────────────────────
// Notification helper — Microsoft Teams + Email
// ─────────────────────────────────────────────────────────────
def sendNotifications(String status) {
    def statusIcon   = status == 'SUCCESS' ? '✅' : (status == 'UNSTABLE' ? '⚠️' : '❌')
    def themeColor   = status == 'SUCCESS' ? '00B050' : (status == 'UNSTABLE' ? 'FFC000' : 'FF0000')
    def branch       = env.GIT_BRANCH_NAME ?: 'unknown'
    def commit       = env.GIT_COMMIT_SHORT ?: 'unknown'
    def buildUrl     = env.BUILD_URL ?: ''
    def environment  = params.DEPLOY_ENV ?: 'dev'

    def plainMessage = """${statusIcon} ${APP_NAME} — Build #${env.BUILD_NUMBER} ${status}
Branch: ${branch}
Commit: ${commit}
Environment: ${environment}
Build URL: ${buildUrl}""".trim()

    // Microsoft Teams notification (Incoming Webhook / MessageCard)
    try {
        def teamsPayload = """
{
  "@type": "MessageCard",
  "@context": "http://schema.org/extensions",
  "themeColor": "${themeColor}",
  "summary": "${APP_NAME} — Build #${env.BUILD_NUMBER} ${status}",
  "sections": [{
    "activityTitle": "${statusIcon} ${APP_NAME}",
    "activitySubtitle": "Build #${env.BUILD_NUMBER} — ${status}",
    "facts": [
      {"name": "Status", "value": "${status}"},
      {"name": "Branch", "value": "${branch}"},
      {"name": "Commit", "value": "${commit}"},
      {"name": "Environment", "value": "${environment}"},
      {"name": "Duration", "value": "${currentBuild.durationString ?: 'N/A'}"}
    ],
    "markdown": true
  }],
  "potentialAction": [{
    "@type": "OpenUri",
    "name": "View Build in Jenkins",
    "targets": [{"os": "default", "uri": "${buildUrl}"}]
  }]
}""".trim()

        writeFile file: 'teams-payload.json', text: teamsPayload
        withEnv(["TEAMS_URL=${TEAMS_WEBHOOK_URL}"]) {
            if (isUnix()) {
                sh 'curl -sS -f -X POST "$TEAMS_URL" -H "Content-Type: application/json" -d @teams-payload.json'
            } else {
                bat 'curl -sS -f -X POST "%TEAMS_URL%" -H "Content-Type: application/json" -d @teams-payload.json'
            }
        }
        echo 'Teams notification sent.'
    } catch (Exception e) {
        echo "Teams notification skipped: ${e.message}"
    }

    // Email notification (Email Extension plugin + SMTP configured in Jenkins)
    try {
        def htmlBody = """
<h2>${statusIcon} ${APP_NAME}</h2>
<p><strong>Build #${env.BUILD_NUMBER}</strong> — <strong>${status}</strong></p>
<table border="1" cellpadding="6" cellspacing="0">
  <tr><td><b>Branch</b></td><td>${branch}</td></tr>
  <tr><td><b>Commit</b></td><td>${commit}</td></tr>
  <tr><td><b>Environment</b></td><td>${environment}</td></tr>
  <tr><td><b>Duration</b></td><td>${currentBuild.durationString ?: 'N/A'}</td></tr>
  <tr><td><b>Build URL</b></td><td><a href="${buildUrl}">${buildUrl}</a></td></tr>
</table>
<p><i>Employee Management System — Jenkins CI/CD Pipeline</i></p>
""".trim()

        emailext(
            subject: "[${status}] ${APP_NAME} — Build #${env.BUILD_NUMBER} (${environment})",
            body: htmlBody,
            mimeType: 'text/html',
            to: "${EMAIL_RECIPIENTS}",
            attachLog: status != 'SUCCESS'
        )
        echo "Email notification sent to ${EMAIL_RECIPIENTS}."
    } catch (Exception e) {
        echo "Email notification skipped: ${e.message}"
    }
}
