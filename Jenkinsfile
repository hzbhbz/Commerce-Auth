pipeline {

  environment {
    registry = "hzbhbz/Commerce"
    dockerImage = ""
  }

  agent any

  stages {

    stage('Checkout Source') {
      steps {
        echo "Checkout Source START"
        git 'https://github.com/hzbhbz/Commerce-Auth.git'
        echo "Checkout Source END"
      }
    }

    stage('Build image') {
      steps{
        script {
          echo "Build image START $BUILD_NUMBER"
          sh "docker build --no-cache -t 192.168.100.12/bb-edu/commerce-auth:$BUILD_NUMBER ."
          echo "Build image END"
        }
      }
    }

    stage('Push Image') {
      environment {
               registryCredential = 'harbor'
           }
      steps{
        script {
          echo "Push Image START"
          sh "docker login 192.168.100.12 -u admin -p Unipoint11"
          sh "docker push 192.168.100.12/bb-edu/commerce-auth:$BUILD_NUMBER"
          }
        echo "Push Image END"
      }
    }
    

    stage('Deploy App') {
      steps {
        script {
          echo "Deploy App START"
          sh "/usr/local/bin/kubectl --kubeconfig=/home/jenkins/acloud-client.conf apply -f auth_deployment_v2.yaml"
          sh "/usr/local/bin/kubectl --kubeconfig=/home/jenkins/acloud-client.conf set image deployments/commerce-auth auth-container=192.168.100.12/bb-edu/commerce-auth:$BUILD_NUMBER -n bb-edu"
          echo "Deploy App END"
        }
      }
    }

  }
}
