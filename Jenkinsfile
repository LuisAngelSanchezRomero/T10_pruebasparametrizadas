pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }

    options {
        timeout(time: 15, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('1. Checkout SCM') {
            steps {
                echo '=== Obteniendo codigo fuente desde el repositorio Git ==='
                checkout scm
            }
        }

        stage('2. Compilacion') {
            steps {
                echo '=== Compilando el proyecto de Gestion de Productos ==='
                sh 'mvn clean compile'
            }
        }

        stage('3. Pruebas Unitarias y Parametrizadas') {
            steps {
                echo '=== Ejecutando pruebas unitarias (JUnit 5 + Mockito) y pruebas parametrizadas (@ParameterizedTest) ==='
                sh 'mvn test'
            }
        }

        stage('4. Reporte y Validacion de Cobertura JaCoCo') {
            steps {
                echo '=== Generando reporte de cobertura JaCoCo y verificando umbral minimo del 80% ==='
                sh 'mvn jacoco:report jacoco:check'
            }
        }

        stage('5. Empaquetado') {
            steps {
                echo '=== Empaquetando artefacto JAR ==='
                sh 'mvn package -DskipTests'
            }
        }
    }

    post {
        always {
            echo '=== Publicando resultados de pruebas JUnit y reporte JaCoCo ==='
            // Publicar reporte de pruebas de JUnit
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

            // Publicar reporte HTML de cobertura JaCoCo
            publishHTML(target: [
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'Reporte Cobertura JaCoCo'
            ])
        }
        success {
            echo '====================================================='
            echo ' PIPELINE COMPLETADO EXITOSAMENTE - Calidad y Cobertura Aprobadas '
            echo '====================================================='
        }
        failure {
            echo '====================================================='
            echo ' ALERTA: Pipeline fallido debido a error en compilacion, pruebas o umbral de cobertura JaCoCo '
            echo '====================================================='
        }
    }
}
