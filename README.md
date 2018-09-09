# fargate-java-sample
By Using AWS Fargate, ECS, and ECR to demonstrate how to leverage Serveless containers to run Java Spring application.


## Insturction Steps

1. Make sure that Docker is installed on your laptop or environment which you like.
2. Integrate GoogleContainerTools/jib into your maven project
```
<plugin>
                <groupId>com.google.cloud.tools</groupId>
                <artifactId>jib-maven-plugin</artifactId>
                <version>0.9.10</version>
                <configuration>
                    <to>
                        <image>"aws-account-id".dkr.ecr.ap-northeast-1.amazonaws.com/"repo-name"</image>
                        <credHelper>ecr-login</credHelper>
                    </to>
                </configuration>
            </plugin>
```
3. Install amazon-ecr-credential-helper - https://github.com/awslabs/amazon-ecr-credential-helper
4. Put the docker-credential-ecr-login in in your path
5. Create a docker repository on AWS ECR
```
aws ecr create-repository --repository-name fargate/spring-petclinic
```
6. build and test locally without any error
```
mvn clean test build
```
7. build and push a docker image to ECR
```
mvn compile jib:build
```