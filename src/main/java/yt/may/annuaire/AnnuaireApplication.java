package yt.may.annuaire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) // TODO : to modify when needed
public class AnnuaireApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnnuaireApplication.class, args);
	}

}
