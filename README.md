# fargate-java-sample
By Using AWS Fargate, ECS, and ECR to demonstrate how to leverage Serveless containers to run Java Spring application.


## Pre-Requesties

1. Make sure that Docker is installed on your laptop or environment which you like.
2. Install amazon-ecr-credential-helper - https://github.com/awslabs/amazon-ecr-credential-helper
3. Put the docker-credential-ecr-login in in your path
4. Create a docker repository on AWS ECR
```
aws ecr create-repository --repository-name fargate/spring-petclinic
```
5. build and test locally without any error
```
mvn clean test build
```
6. build and push a docker image to ECR
```
mvn compile jib:build
```