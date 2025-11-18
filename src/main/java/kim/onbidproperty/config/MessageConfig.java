package kim.onbidproperty.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import java.util.Map;

@Slf4j
@Configuration
public class MessageConfig {

    @Bean
    public Map <String ,Map<String,String>>messageSource() {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("messages.json");
try {
//    json-> map변환
    return  objectMapper.readValue(
            resource.getInputStream(),
            new TypeReference<>() {
            }
    );

}catch (IOException e){
    log.error("메세지 파일 읽기 실패 : {}", e.getMessage());
    return Map.of();
}



    }
}
