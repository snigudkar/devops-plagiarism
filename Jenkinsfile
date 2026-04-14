// pipeline {
//     agent any

//     options {
//         buildDiscarder(logRotator(numToKeepStr: '10'))
//         timestamps()
//         timeout(time: 30, unit: 'MINUTES')
//     }

//     stages {
//         stage('Checkout') {
//             steps {
//                 echo '==== Checking out source code ===='
//                 checkout scm
//             }
//         }

//         stage('Build') {
//             steps {
//                 echo '==== Building application ===='
//                 sh 'mvn clean install -DskipTests'
//             }
//         }

//         stage('Test') {
//             steps {
//                 echo '==== Running unit tests ===='
//                 sh 'mvn test'
//             }
//         }

//         stage('Code Quality Analysis') {
//             steps {
//                 echo '==== Running SonarQube analysis ===='
//                 // Uncomment when SonarQube is configured
//                 // sh 'mvn clean verify sonar:sonar -Dsonar.projectKey=auth-module'
//             }
//         }

//         stage('Package') {
//             steps {
//                 echo '==== Packaging application ===='
//                 sh 'mvn package -DskipTests'
//             }
//         }

//         stage('Build Docker Image') {
//             steps {
//                 echo '==== Building Docker image ===='
//                 script {
//                     sh 'docker build -t auth-module:${BUILD_NUMBER} .'
//                     sh 'docker tag auth-module:${BUILD_NUMBER} auth-module:latest'
//                 }
//             }
//         }

//         stage('Deploy to Dev') {
//             when {
//                 branch 'develop'
//             }
//             steps {
//                 echo '==== Deploying to Dev environment ===='
//                 script {
//                     sh 'docker run -d --name auth-module-dev-${BUILD_NUMBER} -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev auth-module:${BUILD_NUMBER}'
//                 }
//             }
//         }

//         stage('Deploy to Prod') {
//             when {
//                 branch 'main'
//             }
//             steps {
//                 echo '==== Deploying to Production environment ===='
//                 script {
//                     sh 'docker run -d --name auth-module-prod-${BUILD_NUMBER} -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod auth-module:${BUILD_NUMBER}'
//                 }
//             }
//         }
//     }

//     post {
//         always {
//             echo '==== Running post-build tasks ===='
//             junit 'target/surefire-reports/*.xml'
//             cleanWs()
//         }
//         success {
//             echo '==== Pipeline executed successfully ===='
//         }
//         failure {
//             echo '==== Pipeline failed ===='
//         }
//     }
// }
pipeline {
    agent any

    tools {
        jdk 'jdk17'   // 👈 CRITICAL: force Java 17
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {

        stage('Checkout') {
            steps {
                echo '==== Checking out source code ===='
                checkout scm
            }
        }

        stage('Verify Java Version') {
            steps {
                echo '==== Verifying Java version ===='
                bat 'echo JAVA_HOME=%JAVA_HOME%'
                bat 'java -version'
            }
        }

        stage('Build') {
            steps {
                echo '==== Building application ===='
                bat 'mvn clean install -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo '==== Running unit tests ===='
                bat 'mvn test'
            }
        }

        stage('Code Quality Analysis') {
            steps {
                echo '==== Running SonarQube analysis ===='
                // bat 'mvn clean verify sonar:sonar -Dsonar.projectKey=auth-module'
            }
        }

        stage('Package') {
            steps {
                echo '==== Packaging application ===='
                bat 'mvn package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo '==== Building Docker image ===='
                script {
                    bat 'docker build -t auth-module:%BUILD_NUMBER% .'
                    bat 'docker tag auth-module:%BUILD_NUMBER% auth-module:latest'
                }
            }
        }

        stage('Deploy to Dev') {
            when {
                branch 'develop'
            }
            steps {
                echo '==== Deploying to Dev environment ===='
                script {
                    bat '''
                    docker rm -f auth-module-dev || exit 0
                    docker run -d --name auth-module-dev -p 8080:8080 ^
                    -e SPRING_PROFILES_ACTIVE=dev auth-module:%BUILD_NUMBER%
                    '''
                }
            }
        }

        stage('Deploy to Prod') {
            when {
                branch 'main'
            }
            steps {
                echo '==== Deploying to Production environment ===='
                script {
                    bat '''
                    docker rm -f auth-module-prod || exit 0
                    docker run -d --name auth-module-prod -p 8080:8080 ^
                    -e SPRING_PROFILES_ACTIVE=prod auth-module:%BUILD_NUMBER%
                    '''
                }
            }
        }
    }

    post {
        always {
            echo '==== Running post-build tasks ===='
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
            cleanWs()
        }
        success {
            echo '==== Pipeline executed successfully ===='
        }
        failure {
            echo '==== Pipeline failed ===='
        }
    }
}