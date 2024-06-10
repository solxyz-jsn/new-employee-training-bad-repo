package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.api;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.json.ReturnBookIsbn;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service.BookLendingService;
import jp.co.solxyz.jsn.springbootadvincedexam.security.MyUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

/**
 * 書籍返却APIコントローラ
 */
@RestController
@RequestMapping("/api/book/return")
@Slf4j
public class BookReturnApiController {
    /**
     * 書籍返却サービス
     */
    private final BookLendingService bookReturnService;

    /**
     * コンストラクタ
     * @param bookReturnService 書籍返却サービス
     */
    public BookReturnApiController(BookLendingService bookReturnService) {
        this.bookReturnService = bookReturnService;
    }

    /**
     * 書籍の返却
     * @param returnBookIsbn 未返却書籍情報
     * @param userDetails ユーザ情報
     * @return レスポンス
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> returnBook(@RequestBody ReturnBookIsbn returnBookIsbn,
            @AuthenticationPrincipal MyUserDetails userDetails) {
        if (returnBookIsbn == null || returnBookIsbn.getIsbn() == null || userDetails == null) {
            log.info("ISBN or userDetails is null");
            return ResponseEntity.badRequest().build();
        }
        try {
            bookReturnService.henkyaku(userDetails.getUserId(), returnBookIsbn.getIsbn());
        } catch (DataIntegrityViolationException | NoSuchElementException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }
}
