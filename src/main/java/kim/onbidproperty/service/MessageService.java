package kim.onbidproperty.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageService {

    private  final Map<String ,Map<String, String>> messages;
    public String getMessage(String category, String key){
        Map<String , String> categoryMessages = messages.get(category);
        if(categoryMessages == null){
            return "메세지를 찾을수 없습니다.";
        }

        return categoryMessages.getOrDefault(key, "메세지를 찾을수 없습니다.");
    }
}
