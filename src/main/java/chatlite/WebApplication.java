package chatlite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/*
 * Based on https://github.com/hellokoding/registration-login-spring-hsql
 */

@SpringBootApplication
public class WebApplication extends SpringBootServletInitializer {
    
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WebApplication.class);
    }
	
    public static void main(String[] args) throws Exception {
    	SpringApplication app = new SpringApplication(WebApplication.class);
    	app.addListeners(new ApplicationPidFileWriter("chatlite.pid"));
    	app.run(args);
    }
}