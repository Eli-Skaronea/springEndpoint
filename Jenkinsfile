

def gradle(command)
    {
        sh "./gradlew ${command}"
    }

podTemplate(label: 'mypod', containers: 
    [
    containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true)
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

        stage('Build jar') 
        {
            echo 'Building jar file...'
            gradle 'build'
        }

       stage('Build docker image') 
       {
            echo 'Building docker image...'
            container('docker')
            {
                sh "docker build -t eskaronea/spring_endpoint:${env.BUILD_NUMBER} ."
                //app = docker.build("eskaronea/spring_endpoint")
                echo 'Pushing docker image to docker hub...'
                // docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials')
                // {
                withCredentials([[$class: 'UsernamePasswordMultiBinding',
                credentialsId: 'docker-hub-credentials',
                usernameVariable: 'DOCKER_HUB_USER',
                passwordVariable: 'DOCKER_HUB_PASSWORD']]) {
                sh """
                docker login -u ${DOCKER_HUB_USER} -p ${DOCKER_HUB_PASSWORD}
                "docker push eskaronea/spring_endpoint"
                """
                }
                // app.push("${env.BUILD_NUMBER}")
                // app.push("latest")
                // }
                echo 'Updating services on spring_stack...'
                // sh 'docker stack deploy -c docker-compose.yml spring_stack'
            }   
        }

        // stage('Push docker image') 
        // {
        //     echo 'Pushing docker image to docker hub...'
        //     docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials')
        //     {
        //         app.push("${env.BUILD_NUMBER}")
        //        app.push("latest")
        //     }
        // }

        // stage('Deploying services') 
        // {
        //     echo 'Updating services on spring_stack...'
        //     sh 'docker stack deploy -c docker-compose.yml spring_stack'

        // }

    }
}