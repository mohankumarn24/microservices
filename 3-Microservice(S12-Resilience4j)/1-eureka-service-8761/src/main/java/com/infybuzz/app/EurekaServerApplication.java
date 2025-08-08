package com.infybuzz.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}

}

/*
http://localhost:8761/
http://localhost:9090/student-service/api/student/getById/1
	Pass Header: Authorization: ABCD
http://localhost:9090/student-service/actuator/health


- Make address-service down (Health will still display status as UP till user enters below url 10 times) and check health
- Hit url 10 Times: http://localhost:9090/student-service/api/student/getById/1 and check health
- Health goes to DOWN state and waits for 30 seconds in DOWN state. Then, Health goes to UNKNOWN state
- Hit url 5 Times in UNKNOWN state when address-service is down: http://localhost:9090/student-service/api/student/getById/1
- Health again goes to DOWN state, waits 30 seconds in DOWN state and again goes to UNKNOWN state
- In UNKNOWN state (make address-service up), if service becomes up and if it is successful for 5 times, it goes to UP state. If unsuccessful, it goes to DOWN state, waits 30 seconds, and again comes back to UNKNOWN state and repeats
 */