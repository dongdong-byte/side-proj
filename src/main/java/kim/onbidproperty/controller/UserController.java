package kim.onbidproperty.controller;

import jakarta.servlet.http.HttpSession;

import kim.onbidproperty.domain.User;
import kim.onbidproperty.dto.request.user.UserLoginRequest;  // 추가
import kim.onbidproperty.dto.request.user.UserRegisterRequest;
import kim.onbidproperty.dto.request.user.UserUpdateRequest;  // 추가
import kim.onbidproperty.dto.response.user.UserResponse;
import kim.onbidproperty.service.MessageService;
import kim.onbidproperty.service.UserService;

import org.modelmapper.ModelMapper;  // 임포트 추가
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final MessageService messageService;

    @GetMapping("/register")
    public String registerForm() {
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UserRegisterRequest request,
                           RedirectAttributes redirectAttributes) {
//    modelmapper로 변환
        User user = modelMapper.map(request, User.class);
        userService.register(user);
        redirectAttributes.addFlashAttribute("message",
                messageService.getMessage("user", "created"));
        return "redirect:/users/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "users/login";
    }
    @PostMapping("/login")
    public String login(@ModelAttribute UserLoginRequest request,
                        RedirectAttributes redirectAttributes ,
                        HttpSession session ,Model model) {
        try {
            User user = userService.login(request.getUsername(), request.getPassword());

            // 세션에 사용자 정보 저장
            session.setAttribute("loginUser", user);
            redirectAttributes.addFlashAttribute("message",
                    messageService.getMessage("user", "loginSuccess")); // (messages.json에 loginSuccess 추가 필요)

            return "redirect:/"; // 로그인 성공 시 메인 페이지로

        } catch (IllegalArgumentException e) {
            // 로그인 실패 시 (Service에서 예외 발생 시)
            // redirect 대신, 다시 로그인 폼을 보여주며 오류 메시지 전달
            model.addAttribute("message", e.getMessage());
            return "users/login"; // redirect가 아님!
        }

    }
    @PostMapping("/logout")
    public String logout(HttpSession session ,RedirectAttributes redirectAttributes){
        session.invalidate();
        redirectAttributes.addFlashAttribute("message", messageService.getMessage("user", "logoutSuccess"));
        return "redirect:/";

    }

    @GetMapping
    public String list(Model model) {
        List<UserResponse> users = userService.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponse.class))  // ModelMapper 사용
                .toList();
        model.addAttribute("users", users);
        return "users/list";
    }
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id ,Model model){
        User user = userService.findById(id);
        UserResponse response = UserResponse.from(user);  // ModelMapper 대신 from 사용
        model.addAttribute("user", response);
        return "users/detail";

    }
@GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model){
        User user = userService.findById(id);
    UserResponse response = UserResponse.from(user);  // ModelMapper 대신 from 사용
        model.addAttribute("user", response);
        return "users/edit";
    }
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @ModelAttribute UserUpdateRequest request,
                       RedirectAttributes redirectAttributes){
        User user = modelMapper.map(request, User.class);
        user.setId(id); // PathVariable의 id로 덮어쓰기 (안전성)
        userService.updateUser(user);
        redirectAttributes.addFlashAttribute("message",
                messageService.getMessage("user", "updated"));
        return "redirect:/users/" + id;
    }
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         RedirectAttributes redirectAttributes){
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("message",
                messageService.getMessage("user", "deleted"));
        return "redirect:/users";
    }
}
