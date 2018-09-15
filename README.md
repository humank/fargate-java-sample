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
4. put the docker-credential-ecr-login into $PATH
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

10. Until 2018-09-14, the Fargate CLI doesn't support multiple region usage, you need to clone the source code to make build yourself, take a look from here:
https://twitter.com/pahudnet/status/1040233349141295104

```
change directory to $GOPATH, wich contains 3 folders make you easy to check.

# ex: /Users/aaa/go --> $GOPATH
#/Users/aaa/go/bin
#/Users/aaa/go/pkg
#/Users/aaa/go/src

# git clone the fargate cli into the $GOPATH/src folder
cd /Users/aaa/go/src
git clone https://github.com/jpignata/fargate.git

cd /Users/aaa/go/src/fargate
rm Gopkg.lock
dep ensure -v
make build

#Once the make build process finish, you can copy the build bin file --> fargate to $GOROOT/bin

cp bin/fargate /Users/aaa/go/bin/
```

11. Create ECS Service in Fargate Mode

```
fargate service create <service name> [--cpu <cpu units>] [--memory <MiB>] [--port <port-expression>]
                                      [--lb <load-balancer-name>] [--rule <rule-expression>]
                                      [--image <docker-image>] [--env <key=value>] [--num <count>]
                                      [--task-role <task-role>] [--subnet-id <subnet-id>]
                                      [--security-group-id <security-group-id>]
```
More detail instructions are available at: 

https://github.com/jpignata/fargate

There several ways to run ecs services in fargate mode, if you would like to have the ability to do Blue/Green deployment strategy, that would be great to run ecs service behind the ALB(Application Load Balancer). Not only for the intent, imagine that you you have a lot of microservices and need to well-manage for service discovery, it's also the key to run behind the ALB. 

12. Create ALB from fargate cli
```
fargate lb create <load-balancer-name> --port <port-expression> [--certificate <certificate-name>]
                                       [--subnet-id <subnet-id>] [--security-group-id <security-group-id>]
```

For the LoadBalancer limitation, you need to specify at least 2 subnets to provision the ALB
```

#for example ~ 
fargate lb create kim-alb --port HTTP:80 \
    --subnet-id subnet-074e51c30471cab75 \
    --subnet-id subnet-085bf83625bd113cc \
    --subnet-id subnet-0bbdba02d35e95a4b \
    --security-group-id sg-0702741778429f7f3 \
    --region ap-northeast-1 
```

13. Create ECS Service with ALB

```
fargate service create <service name> [--cpu <cpu units>] [--memory <MiB>] [--port <port-expression>]
                                      [--lb <load-balancer-name>] [--rule <rule-expression>]
                                      [--image <docker-image>] [--env <key=value>] [--num <count>]
                                      [--task-role <task-role>] [--subnet-id <subnet-id>]
                                      [--security-group-id <security-group-id>]
                                      

#for example ~ 
fargate service create spring-petclinic \
    --region ap-northeast-1 --verbose \
    --port HTTP:8080 --lb kim-alb --num 3 \
    --image 584518143473.dkr.ecr.ap-northeast-1.amazonaws.com/fargate/spring-petclinic \
    --subnet-id subnet-074e51c30471cab75 \
    --subnet-id subnet-085bf83625bd113cc \
    --subnet-id subnet-0bbdba02d35e95a4b \
    --security-group-id sg-0702741778429f7f3
                                      
```

#Java Spring framework Application Lifecycle in ECS-Fargate

Considering ELB health check mechanism, if the application starting time period is higher than the ELB health check retry timeout...
Then you will find that all the ECS Service/Tasks are always unhealthy and draining.

![image of grace period](assets/service-health-check-grace-period.png)
    

#TODO

* Add the one-click cloudformation execution to create vpc (optional usage if you don't have)
* Add the architecture diagram to illustrate ecs/fargate java application serve behind ALB
* Add ECS Service Discovery integration with Route53
* Add the Blue/Green Deployment instruction with CodePipeline