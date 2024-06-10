package jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.api;

import jakarta.validation.groups.Default;
import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.json.AccountProfile;
import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.model.validation.OnUserCreation;
import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.model.validation.OnUserUpdate;
import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.service.UserManagementService;
import jp.co.solxyz.jsn.springbootadvincedexam.common.validation.anotation.UserId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * ユーザ管理APIコントローラー
 */
@RestController
@RequestMapping("/api/admin/management/user")
@Slf4j
public class UserManagementApiController {

    /**
     * ユーザ管理サービス
     */
    private final UserManagementService userManagementService;

    /**
     * コンストラクタ
     * @param userManagementService ユーザ管理サービス
     */
    public UserManagementApiController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    /**
     * ユーザ情報を登録する
     * @param userManagementForm ユーザ情報
     * @param bindingResult バインディング結果
     * @return レスポンスエンティティ
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ObjectError>> addUser(
            @Validated({ OnUserCreation.class, Default.class }) @RequestBody AccountProfile userManagementForm,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("バリデーションエラーが発生しました。");
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        try {
            userManagementService.addUser(userManagementForm);
        } catch (JpaSystemException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", "既に登録されているメールアドレスです。"));
            return ResponseEntity.badRequest().body(errors);
        } catch (DataAccessException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", "ユーザ情報の登録に失敗しました。"));
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * ユーザ情報を更新する
     * @param userManagementForm ユーザ情報
     * @param bindingResult バインディング結果
     * @return レスポンスエンティティ
     */
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ObjectError>> updateUser(
            @Validated({ OnUserUpdate.class, Default.class }) @RequestBody AccountProfile userManagementForm,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("バリデーションエラーが発生しました。");
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        try {
            userManagementService.updateUser(userManagementForm);
        } catch (NoSuchElementException | OptimisticLockingFailureException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", e.getMessage()));
            return ResponseEntity.badRequest().body(errors);
        } catch (JpaSystemException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", "既に登録されているメールアドレスです。"));
            return ResponseEntity.badRequest().body(errors);
        } catch (DataAccessException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", "ユーザ情報の更新に失敗しました。"));
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * ユーザ情報を削除する
     * @param  userId ユーザID情報
     * @return レスポンスエンティティ
     */
    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<List<ObjectError>> deleteUser(
            @UserId @PathVariable String userId) {

        try {
            userManagementService.deleteUser(userId);
        } catch (NoSuchElementException | IllegalStateException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", e.getMessage()));
            return ResponseEntity.badRequest().body(errors);
        } catch (DataAccessException e) {
            List<ObjectError> errors = new ArrayList<>();
            errors.add(new ObjectError("error", "ユーザ情報の削除に失敗しました。"));
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok().build();
    }
}
