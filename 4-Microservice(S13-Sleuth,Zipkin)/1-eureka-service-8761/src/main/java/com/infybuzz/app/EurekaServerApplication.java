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
Steps:
	- Start Zipkin server: D:\dev\github\microservices\4-Microservice(S13-Sleuth,Zipkin)>java -jar zipkin-server-2.23.2-exec.jar
	- http://localhost:9411/zipkin/

	- Start Microservices (strictly) in below order:
	- eureka-server
    - api-gateway
	- student-service
    - address-service
    - address-service

    UI:
	- Zipkin					: http://localhost:9411/zipkin/
	- eureka-server				: http://localhost:8761/
    - student-service			: http://localhost:9090/student-service/api/student/getById/1
      Pass Header: [Authorization: ABCD]
    - student-service health	: http://localhost:9090/student-service/actuator/health
 */

/*
Circuit-Breaker:
- Make both address-service down (Health will still display status as UP till user enters below url 10 times) and check health
- Hit url 10 Times: http://localhost:9090/student-service/api/student/getById/1 and check health (Status changes from UP -> DOWN after 10 calls)
- After 10 calls, health goes to DOWN state and waits for 30 seconds in DOWN state. Then, health goes to UNKNOWN (Half-Open) state
- Hit url 5 Times (failure rate > 50%) in UNKNOWN state when address-service is down: http://localhost:9090/student-service/api/student/getById/1
- Health again goes to DOWN state, waits 30 seconds in DOWN state and again goes to UNKNOWN state
- In UNKNOWN state (make address-service up), if service becomes up and if it is successful for 3 (failure rate < 50%) times, it goes to UP state. If unsuccessful, it goes to DOWN state, waits 30 seconds, and again comes back to UNKNOWN state and repeats
*/

/*
Sleuth: (Check trace in console)

		   T1                  T1                     T1
Consumer  ---->  api-gateway  ---->  student-service ---->  address-service
                     S1                    S2                     S3

{service-Name, TraceId, SpanId}
TraceId is unique for a request across all services
SpanId is unique for a request within same service

Hit this URL once: http://localhost:9090/student-service/api/student/getById/1
Logs:
api-gateway:
INFO [api-gateway,b62f5f3c712fa88c,b62f5f3c712fa88c] 1712 --- [ctor-http-nio-5] com.infybuzz.app.CustomFilter            : Authorization = null
INFO [api-gateway,b62f5f3c712fa88c,76332af3d7c1b9fe] 1712 --- [ctor-http-nio-7] com.infybuzz.app.CustomFilter            : Authorization = null
INFO [api-gateway,b62f5f3c712fa88c,76332af3d7c1b9fe] 1712 --- [ctor-http-nio-7] com.infybuzz.app.CustomFilter            : Post Filter = 200 OK
INFO [api-gateway,b62f5f3c712fa88c,b62f5f3c712fa88c] 1712 --- [ctor-http-nio-5] com.infybuzz.app.CustomFilter            : Post Filter = 200 OK

student-service:
INFO [student-service,b62f5f3c712fa88c,0ce6bea0c4935422] 10848 --- [nio-8080-exec-9] com.infybuzz.service.StudentService      : Inside Student getById
INFO [student-service,b62f5f3c712fa88c,0ce6bea0c4935422] 10848 --- [nio-8080-exec-9] com.infybuzz.service.CommonService       : count = 32

address-service:
INFO [address-service,b62f5f3c712fa88c,e4f88de79f53ac04] 1492 --- [nio-8083-exec-8] com.infybuzz.service.AddressService      : Inside getById 1

Zipkin: (Check trace in UI)
Start Zipkin server: D:\dev\github\microservices\4-Microservice(S13-Sleuth,Zipkin)>java -jar zipkin-server-2.23.2-exec.jar
Access logs in UI: http://localhost:9411/zipkin/
Search by TraceId: b62f5f3c712fa88c
 */