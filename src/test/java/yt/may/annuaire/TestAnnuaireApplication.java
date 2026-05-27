package yt.may.annuaire;

import org.springframework.boot.SpringApplication;

public class TestAnnuaireApplication {

	public static void main(String[] args) {
		SpringApplication.from(AnnuaireApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
