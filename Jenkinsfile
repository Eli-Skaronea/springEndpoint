

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
            sh "mkdir tmp"
            sh "cd tmp"
            sh "git clone https://github.com/eli-skaronea/helm-charts.git"
            sh "cd .."
            //sh "git remote rename origin upstream"
            //sh "git remote add helm-repo https://github.com/eli-skaronea/helm-charts.git"

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
                sh """
                    helm package spring-chart/ --version 1.0-${env.BUILD_NUMBER} -d tmp/helm-charts/docs/
                    helm package spring-chart/ --version 1.0-latest -d tmp/helm-charts/docs/
                    helm repo index tmp/helm-charts/docs --url https://eli-skaronea.github.io/helm-charts/
                   """ 


            }
        }

        stage('Push helm package')
        {
            sh "cd tmp"
            // sh "ls"
            // sh "pwd"
            // sh "cp -R /home/jenkins/workspace/Build-Pipeline/helm-charts /home/jenkins/workspace/Build-Pipeline/tmp"
            // sh "cd tmp"
            // sh "git clone https://github.com/eli-skaronea/helm-charts.git"
            withCredentials([usernamePassword(credentialsId: 'git-credentials', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) 
                {
                    //sh("git tag -a v1.0.${env.BUILD_NUMBER} -m 'Jenkins pushed helm package v1.0.${env.BUILD_NUMBER}'")
                    sh """
                        git config user.name 'eli-skaronea'
                        git config user.email 'eli.skaronea@gmail.com'
                        git add .
                        git commit -m 'Jenkins has packaged and pushed spring-chart-v1.1-${env.BUILD_NUMBER} and latest'
                        
                        git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/eli-skaronea/helm-charts.git HEAD:master
                       """
                }
            
        }

    }

    
}