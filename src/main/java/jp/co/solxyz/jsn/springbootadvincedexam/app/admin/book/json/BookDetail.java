package jp.co.solxyz.jsn.springbootadvincedexam.app.admin.book.json;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理画面で扱う書籍情報リクエストモデル
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDetail {

    /**
     * ISBN
     */
    @NotEmpty(message = "ISBNは必須です")
    @Pattern(regexp = "\\d{13}", message = "ISBNは13桁の数字である必要があります")
    private String isbn;

    /**
     * タイトル
     */
    @NotEmpty(message = "タイトルは必須です")
    @Size(max = 100, message = "タイトルは100文字以内である必要があります")
    private String title;

    /**
     * 著者
     */
    @NotEmpty(message = "著者名は必須です")
    @Size(max = 100, message = "著者名は100文字以内である必要があります")
    private String author;

    /**
     * 出版社
     */
    @NotEmpty(message = "出版社は必須です")
    @Size(max = 100, message = "出版社は100文字以内である必要があります")
    private String publisher;

    /**
     * 説明
     */
    @Size(max = 500, message = "説明は500文字以内である必要があります")
    private String description;

    /**
     * 在庫数
     */
    @Min(value = 0, message = "在庫数は0以上である必要があります")
    @Digits(integer = 10, fraction = 0, message = "在庫数は数字である必要があります")
    private int stock;
}
