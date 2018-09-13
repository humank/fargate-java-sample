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
8. add the jib build in the maven build lifecycle
You can also bind jib:build to a Maven lifecycle, such as package, by adding the following execution to your jib-maven-plugin definition:
```
<plugin>
  <groupId>com.google.com.tools</groupId>
  <artifactId>jib-maven-plugin</artifactId>
  ...
  <executions>
    <execution>
      <phase>package</phase>
      <goals>
        <goal>build</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```
Then, you can run this command : 
```
mvn package
```

Check for the running result:
```
[INFO] --- jib-maven-plugin:0.9.10:build (default) @ spring-petclinic ---
[WARNING] Base image 'gcr.io/distroless/java' does not use a specific image digest - build may not be reproducible
[INFO] 
[INFO] Containerizing application to 584518143473.dkr.ecr.ap-northeast-1.amazonaws.com/fargate/spring-petclinic...
[INFO] 
[INFO] Retrieving registry credentials for 584518143473.dkr.ecr.ap-northeast-1.amazonaws.com...
[INFO] Getting base image gcr.io/distroless/java...
[INFO] Building dependencies layer...
[INFO] Building resources layer...
[INFO] Building classes layer...
[INFO] Finalizing...
[INFO] 
[INFO] Container entrypoint set to [java, -cp, /app/resources/:/app/classes/:/app/libs/*, org.springframework.samples.petclinic.PetClinicApplication]
[INFO] 
[INFO] Built and pushed image as "Your-AWS-Account-ID".dkr.ecr.ap-northeast-1.amazonaws.com/"Your-ECR-Image"
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 39.797 s
[INFO] Finished at: 2018-09-10T12:14:59+08:00
[INFO] ------------------------------------------------------------------------

```
9. Install AWS Fargate CLI to simplify the instruction - http://somanymachines.com/fargate/
You can choose download the binary directly, or manual build it ony your laptop leverage the source : https://github.com/jpignata/fargate
