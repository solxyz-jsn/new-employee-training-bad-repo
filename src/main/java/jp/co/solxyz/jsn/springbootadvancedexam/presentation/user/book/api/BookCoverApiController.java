package jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.api;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * 書影取得APIコントローラ
 */
@RestController
@RequestMapping("/api/book/covers")
@Slf4j
public class BookCoverApiController {
    /** 一度に取得できるISBN数 */
    private static final int MAX_ISBN_COUNT = 20;

    /** ISBN形式 */
    private static final Pattern ISBN_PATTERN = Pattern.compile("^\\d{13}$");

    /**
     * ISBNに対応する書影URLを取得
     * @param isbn カンマ区切りのISBN
     * @return ISBNと書影URLのマップ
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> getCoverUrls(@RequestParam("isbn") String isbn) {
        var requestedIsbns = Arrays.stream(isbn.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
        if (requestedIsbns.isEmpty() || requestedIsbns.size() > MAX_ISBN_COUNT || requestedIsbns.stream().anyMatch(this::isInvalidIsbn)) {
            log.info("ISBN is invalid");
            return ResponseEntity.badRequest().build();
        }

        Map<String, String> coverUrls = requestedIsbns.stream()
                .distinct()
                .collect(Collectors.toMap(value -> value, value -> ""));
        return ResponseEntity.ok(coverUrls);
    }

    /**
     * ISBN形式が不正か判定する
     * @param isbn ISBN
     * @return 不正な場合true
     */
    private boolean isInvalidIsbn(String isbn) {
        return !ISBN_PATTERN.matcher(isbn).matches();
    }
}
