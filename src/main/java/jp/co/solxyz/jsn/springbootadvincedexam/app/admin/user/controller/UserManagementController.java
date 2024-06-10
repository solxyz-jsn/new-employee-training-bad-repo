package jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.controller;

import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.model.UserManagementModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.service.UserManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * ユーザ管理コントローラー
 */
@Controller
@RequestMapping("/admin/management/user")
@Slf4j
public class UserManagementController {

    /**
     * ユーザ管理サービス
     */
    private final UserManagementService userManagementService;

    /**
     * コンストラクタ
     * @param userManagementService ユーザ管理サービス
     */
    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    /**
     * ユーザ管理画面表示
     * @return ユーザ管理画面
     */
    @GetMapping
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("admin/user-management");
        List<UserManagementModel> displayedUsers = userManagementService.getAllUsers();

        mav.addObject("users", displayedUsers);
        return mav;
    }
}
