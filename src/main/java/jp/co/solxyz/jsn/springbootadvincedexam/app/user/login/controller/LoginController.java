package jp.co.solxyz.jsn.springbootadvincedexam.app.user.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    /**
     * ログイン画面表示
     * @return ログイン画面
     */
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    /**
     * トップページにアクセス時にログイン画面へリダイレクト
     * @return ログイン画面
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

}
