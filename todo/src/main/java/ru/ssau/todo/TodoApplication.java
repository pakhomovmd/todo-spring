package ru.ssau.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class TodoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoApplication.class, args);
		System.out.println(new BCryptPasswordEncoder().encode("123admin"));
		System.out.println(new BCryptPasswordEncoder().encode("123user"));
	}

}
