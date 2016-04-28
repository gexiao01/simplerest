# simplerest

Light weighted Http+RequestMapping framework, supports simple RESTful apis，without needs for web containers (exp. tomcat)

Expected usage in
1.Monitor on process existence, configurations, etc
2.Smoke tests
3.Emergency operation, such as evicting certain caches
 
Employs Netty (for http) and Javassist (for RequestMapping) for a Spring-MVC like code style.
See demo at https://github.com/gexiao01/simplerest/blob/master/src/test/java/com/dp/simplerest/control/TestController.java

We have spring-bean dependency just for convenience. You can be easily reconstruct to get rid of Spring.
