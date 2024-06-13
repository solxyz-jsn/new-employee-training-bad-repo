package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.util;

import jp.co.solxyz.jsn.springbootadvincedexam.util.BookUtility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BookUtilityTest {

    @Test
    @DisplayName("正常なISBNのフォーマット")
    public void shouldFormatIsbnSuccessfully() {
        String isbn = "9781234567890";
        String formattedIsbn = BookUtility.getFormattedISBN(isbn);
        assertThat(formattedIsbn).isEqualTo("978-1-2345-6789-0");
    }

    @Test
    @DisplayName("ISBNが13桁ではない場合")
    public void shouldThrowExceptionWhenIsbnIsNot13Digits() {
        String isbn = "1234567890";
        assertThatThrownBy(() -> BookUtility.getFormattedISBN(isbn))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("引数には13桁の数字のみが使用できます");
    }

    @Test
    @DisplayName("ISBNが数字ではない場合")
    public void shouldThrowExceptionWhenIsbnIsNotNumeric() {
        String isbn = "978123456789X";
        assertThatThrownBy(() -> BookUtility.getFormattedISBN(isbn))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("引数には13桁の数字のみが使用できます");
    }
}
