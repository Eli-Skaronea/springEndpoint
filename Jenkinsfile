def gradle(command){
        sh "./gradlew ${command}"
    }

node{
    
    def app

    stage('Checkout'){
        echo 'Checking out project repo...'
        checkout scm    
    }

    stage('Build jar') {   
        echo 'Building jar file...'
        gradle 'build'
    }

    stage('Build docker image') {
        echo 'Building docker image...'
        app = docker.build("eskaronea/spring_endpoint")   
    }
        
    stage('Push docker image') {
        echo 'Pushing docker image to docker hub...'
        docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials'){
            app.push("${env.BUILD_NUMBER}")
            app.push("latest")
        }
    }

    stage('Deploy container') {   
        echo 'Running application in container...'   
        sh "docker run -d --name spring_endpoint-${env.BUILD_NUMBER} eskaronea/spring_endpoint"
    }
    
}