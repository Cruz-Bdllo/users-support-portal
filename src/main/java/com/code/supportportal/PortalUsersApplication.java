package com.code.supportportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

import static com.code.supportportal.constant.FileConstant.USER_FOLDER;

@SpringBootApplication
public class PortalUsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortalUsersApplication.class, args);
		new File(USER_FOLDER).mkdirs();
	}


}
