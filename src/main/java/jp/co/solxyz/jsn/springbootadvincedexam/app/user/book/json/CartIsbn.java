package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * カート情報
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartIsbn {
    /** ISBN */
    private String isbn;
}
