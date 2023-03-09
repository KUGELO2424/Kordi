package pl.kucharski.Kordi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KordiApplication {

	public static void main(String[] args) {
		SpringApplication.run(KordiApplication.class, args);
	}

}
