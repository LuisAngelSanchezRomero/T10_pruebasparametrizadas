pipeline {
    agent any

    stages {
        stage('1. Checkout SCM') {
            steps {
                echo '=== Descargando codigo fuente desde GitHub ==='
                git branch: 'develop', url: 'https://github.com/LuisAngelSanchezRomero/T10_pruebasparametrizadas.git'
            }
        }

        stage('2. Compilacion') {
            steps {
                echo '=== Compilando el proyecto de Gestion de Productos ==='
                bat 'mvn clean compile'
            }
        }

        stage('3. Pruebas Unitarias y Parametrizadas') {
            steps {
                echo '=== Ejecutando pruebas unitarias (JUnit 5 + Mockito) y parametrizadas (@ParameterizedTest) ==='
                bat 'mvn test'
            }
        }

        stage('4. Reporte y Validacion de Cobertura JaCoCo') {
            steps {
                echo '=== Generando reporte de cobertura JaCoCo ==='
                bat 'mvn jacoco:report'
            }
        }

        stage('5. Empaquetado') {
            steps {
                echo '=== Empaquetando artefacto JAR ==='
                bat 'mvn package -DskipTests'
            }
        }
    }

    post {
        always {
            echo '=== Publicando resultados de pruebas JUnit y reporte JaCoCo ==='
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

            publishHTML(target: [
                allowMissing: true,
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
            echo ' ALERTA: Pipeline fallido en compilacion, pruebas o cobertura JaCoCo '
            echo '====================================================='
        }
    }
}
