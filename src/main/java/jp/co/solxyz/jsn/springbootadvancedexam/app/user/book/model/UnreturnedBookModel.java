package jp.co.solxyz.jsn.springbootadvancedexam.app.user.book.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 未返却書籍
 */
@Data
public class UnreturnedBookModel {
    /** ISBN */
    private String isbn;
    /** タイトル */
    private String title;
    /** 著者 */
    private String author;
    /** 出版社 */
    private String publisher;
    /** 貸出日時 */
    private LocalDateTime rentalAt;
    /** 返却期限日時 */
    private LocalDateTime dueAt;
    /** 返却期限までの日数 */
    private long remainingDays;
    /** 返却期限が近いか */
    private boolean dueSoon;
    /** 返却期限を過ぎているか */
    private boolean overdue;
    /** 貸出ステータス表示名 */
    private String statusLabel;
}
