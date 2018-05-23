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
        sh "docker kill spring_endpoint-latest"
        sh "docker rm spring_endpoint-latest" 
        sh "docker run -d -p 9000:9000 --name spring_endpoint-latest eskaronea/spring_endpoint"
    }
    
}