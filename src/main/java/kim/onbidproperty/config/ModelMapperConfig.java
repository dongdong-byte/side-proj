package kim.onbidproperty.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper(){
       ModelMapper  modelMapper = new ModelMapper();
       modelMapper.getConfiguration()
               .setMatchingStrategy(MatchingStrategies.STRICT) // 가장 중요
               .setFieldMatchingEnabled(true) // 필드 매칭 허용
               .setSkipNullEnabled(true) // null 덮어쓰기 방지
               .setAmbiguityIgnored(true);  // 모호한 매핑 무시
//               .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)->3.x버젼에서는 사용안함
//       null 값 건너 뛰기
        return modelMapper;



    }
}
