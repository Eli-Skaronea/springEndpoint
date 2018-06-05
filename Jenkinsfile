

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
        }

        stage('Build and test jar') 
        {
            echo 'Building jar file...'
            container('java'){
                gradle 'build --quiet'
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
                passwordVariable: 'DOCKER_HUB_PASSWORD']]) {
                sh "docker login -u ${DOCKER_HUB_USER} -p ${DOCKER_HUB_PASSWORD}"
                }
                sh "docker push eskaronea/spring_endpoint:v1.0.${env.BUILD_NUMBER}"
                //sh "docker push eskaronea/spring_endpoint:latest"
            }   
        }

        stage('Deploying services') 
        {
            container('helm')
            {
                
                echo 'Linting helm package...'
                sh "helm lint spring-chart/"

                echo 'Updating services in helm package...'
                sh "helm upgrade --install spring spring-chart/ --set ImageTag=v1.0.${env.BUILD_NUMBER}"

            }
        }
        //Test commen

    }
}