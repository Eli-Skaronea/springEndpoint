

def gradle(command)
    {
        sh "./gradlew ${command}"
    }

podTemplate(label: 'mypod', containers: 
    [
    containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true),
    containerTemplate(name: 'java', image: 'openjdk:8', command: 'cat', ttyEnabled: true),
    containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl:v1.8.8', command: 'cat', ttyEnabled: true),
    containerTemplate(name: 'helm', image: 'lachlanevenson/k8s-helm:latest', command: 'cat', ttyEnabled: true)
    ],
  volumes: 
  [
    hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
  ]
) 

{
    node('mypod')
    {
        def app
        stage('Checkout')
        {
            echo 'Checking out project repo...'
            checkout scm
            sh "git clone https://github.com/eli-skaronea/helm-charts.git"
            sh "git remote rename origin upstream"
            sh "git remote add origin https://github.com/eli-skaronea/helm-charts.git"

        }

        stage('Build and test jar') 
        {
            echo 'Building jar file...'
            container('java')
            {
                gradle 'build'
                //gradle 'clean test'
            }

        }

       stage('Build docker image') 
       {
            echo 'Building docker image...'
            container('docker')
            {
                sh "docker build -t eskaronea/spring_endpoint:v1.0.${env.BUILD_NUMBER} ."
        
                echo 'Pushing docker image to docker hub...'
               
                withCredentials([[$class: 'UsernamePasswordMultiBinding',
                credentialsId: 'docker-hub-credentials',
                usernameVariable: 'DOCKER_HUB_USER',
                passwordVariable: 'DOCKER_HUB_PASSWORD']]) 
                {
                    sh "docker login -u ${DOCKER_HUB_USER} -p ${DOCKER_HUB_PASSWORD}"
                }
                sh "docker push eskaronea/spring_endpoint:v1.0.${env.BUILD_NUMBER}"
                sh "docker push eskaronea/spring_endpoint:latest"
            }   
        }

        stage('Building helm') 
        {
            container('helm')
            {
                echo 'Initialize helm'
                sh "helm init"

                echo 'Linting helm package...'
                sh "helm lint spring-chart/"

                echo 'Packaging helm chart...'
                sh "helm package spring-chart/ --version 1.0-${env.BUILD_NUMBER} -d helm-charts/docs/"
                sh "helm package spring-chart/ --version 1.0-latest -d helm-charts/docs/"
                sh "helm repo index helm-charts/docs --url https://eli-skaronea.github.io/helm-charts/"


            }
        }

        stage('Push helm package')
        {
            withCredentials([usernamePassword(credentialsId: 'git-credentials', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) 
                {
                    //sh("git tag -a v1.0.${env.BUILD_NUMBER} -m 'Jenkins pushed helm package v1.0.${env.BUILD_NUMBER}'")
                    sh "git config user.name 'eli-skaronea'"
                    sh "git config user.email 'eli.skaronea@gmail.com'"
                    //sh "git helm-repo fetch"
                    sh "git commit -am 'Jenkins has packaged and pushed spring-chart-v1.1-${env.BUILD_NUMBER} and latest'"
                    sh 'git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/eli-skaronea/helm-charts.git'
                }
            
        }

    }

    
}