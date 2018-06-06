

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
            gitLib = load "git_push_ssh.groovy"    
        }

        stage('Build and test jar') 
        {
            echo 'Building jar file...'
            container('java')
            {
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
                sh "helm package spring-chart/ --version 1.0.${env.BUILD_NUMBER} -d docs/"
                sh "helm repo index docs --url https://eli-skaronea.github.io/springEndpoint/"

                // echo 'Pushing helm package to repo...'
                // withCredentials([usernamePassword(credentialsId: 'git-credentials', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) 
                // {
                //     sh("git tag -a v1.0.${env.BUILD_NUMBER} -m 'Jenkins pushed helm package v1.0.${env.BUILD_NUMBER}'")
                //     sh('git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/eli-skaronea/springEndpoint.git --tags')
                // }
                // echo 'Updating services in helm package...'
                // sh "helm upgrade --install spring spring-chart/ --set ImageTag=v1.0.${env.BUILD_NUMBER}"

            }
        }

        // stage('Push helm package')
        // {
            
        //     sh "git checkout master"
        //     sh "git config user.name 'eli-skaronea'"
        //     sh "git config user.email 'eli.skaronea@gmail.com'"
        //     sh "git add docs/"
        //     sh "git commit -m 'Jenkins pushed spring-boot-1.0.${env.BUILD_NUMBER}'"
        //     sh "git push origin master"
        // }

        // stage('Deploy helm package')
        // {
        //     echo 'Updating services in helm deployment...'
        //     sh "helm upgrade --install spring spring-chart/ --set ImageTag=v1.0.${env.BUILD_NUMBER}"
        // }
        //Test commen

    }

    // node 
    // {
    //     // This step utilizes the Workspace Cleanup Plugin: https://wiki.jenkins.io/display/JENKINS/Workspace+Cleanup+Plugin
    //     step([$class: 'WsCleanup'])  
    // }

    node 
    {
    sshagent (credentials: ['git-ssh']) 
        {
            // "git add", "git commit", and "git push" your changes here. You may have to cd into the repo directory like I did here because the current working directory is the parent directory to the directory that contains the actual repo, created by "git clone" earlier in this Jenkinsfile.
            sh("git add docs/")
            sh("git commit -m 'Jenkins push'")
            sh('git push git@github.com:eli-skaronea/springEndpoint.git)')
        }
    }   
    
}