

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
        }

        stage('Build and test jar') 
        {
            echo 'Building jar file...'
            container('java')
            {
                gradle 'clean test'
                gradle 'build'
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
                sh "helm init --client-only"

                echo 'Linting helm package...'
                sh "helm lint spring-consumer/"

                echo 'Releasing helm chart'
                //Default values.yaml is the values for a consumer app.
                sh "helm upgrade --install consumer -f helm-charts/spring-app/values.yaml helm-charts/spring-app"


                //echo 'Packaging helm chart...'
                //sh """
                //    helm package spring-consumer/ --version 1.0-${env.BUILD_NUMBER} -d helm-charts/docs/
                //    helm package spring-consumer/ --version 1.0-latest -d helm-charts/docs/
                //    helm repo index helm-charts/docs --url https://eli-skaronea.github.io/helm-charts/
                //   """ 
                //archiveArtifacts 'helm-charts/docs/index.yaml'
                //archiveArtifacts "helm-charts/docs/spring-consumer-1.0-${env.BUILD_NUMBER}.tgz"
                //archiveArtifacts 'helm-charts/docs/spring-consumer-1.0-latest.tgz'


            }
        }


    }

    
}