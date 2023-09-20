package splitter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecretSantaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecretSantaApplication.class, args);
    }
}
//    group create BOBTEAM (Frank, Bob)
//    group create AGROUP (Bob, -Bob)
//    purchase Ann coffee 12.00 (Chuck, Ann, Bob)
//    balance close (Bob, Ann)
