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

    stage('Deploying services') {
        echo 'Updating services on spring_stack...'
        sh 'docker stack deploy -c docker-compose.yml spring_stack'
        bash 'xdg-open http://0.0.0.0:9000/greeting?name=User'
        bash 'xdg-open http://0.0.0.0:8000'
    }
    
}