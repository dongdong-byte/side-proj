package kim.onbidproperty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableAsync
@SpringBootApplication
public class OnBidpropertyApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnBidpropertyApplication.class, args);
    }

}
