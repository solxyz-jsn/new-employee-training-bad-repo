package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返却書籍情報
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnBookIsbn {
    /** ISBN */
    private String isbn;
}

