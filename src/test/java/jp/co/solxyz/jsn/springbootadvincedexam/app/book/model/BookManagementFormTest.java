package jp.co.solxyz.jsn.springbootadvincedexam.app.book.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.book.json.BookDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BookManagementFormTest {

    private final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();

    private final Validator VALIDATOR = FACTORY.getValidator();

    private final String OVER_101_CHARACTERS = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";

    private final String OVER_501_CHARACTERS = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";

    @Test
    @DisplayName("全てのフィールドが正しい場合、バリデーションエラーは発生しない")
    void shouldNotHaveValidationErrorsWhenAllFieldsAreCorrect() {
        BookDetail form = new BookDetail(
                "1234567890123",
                "Test Title",
                "Test Author",
                "Test Publisher",
                "Test Description",
                10
        );

        var violations = VALIDATOR.validate(form);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("ISBNがNullの場合、バリデーションエラーが発生する")
    void shouldHaveValidationErrorWhenIsbnIsNull() {
        BookDetail form = new BookDetail(
                null,
                "Test Title",
                "Test Author",
                "Test Publisher",
                "Test Description",
                10
        );

        var violations = VALIDATOR.validate(form);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting("message")
                .contains("ISBNは必須です")
                .doesNotContain("ISBNは13桁の数字である必要があります");
    }

    @Test
    @DisplayName("ISBNが13桁でない場合、バリデーションエラーが発生する")
    void shouldHaveValidationErrorWhenIsbnIsNot13Digits() {
        BookDetail form = new BookDetail(
                "123",
                "Test Title",
                "Test Author",
                "Test Publisher",
                "Test Description",
                10
        );

        var violations = VALIDATOR.validate(form);

        assertThat(violations).extracting("message")
                .contains("ISBNは13桁の数字である必要があります")
                .doesNotContain("ISBNは必須です");
    }

    @Test
    @DisplayName("タイトルが101文字以上の場合、バリデーションエラーが発生する")
    void shouldHaveValidationErrorWhenTitleIsMoreThan100Characters() {
        BookDetail form = new BookDetail(
                "1234567890123",
                OVER_101_CHARACTERS,
                "Test Author",
                "Test Publisher",
                "Test Description",
                10
        );

        var violations = VALIDATOR.validate(form);

        assertThat(violations).extracting("message")
                .contains("タイトルは100文字以内である必要があります");

    }

    @Test
    @DisplayName("著者名が101文字以上の場合、バリデーションエラーが発生する")
    void shouldHaveValidationErrorWhenAuthorIsMoreThan100Characters() {
        BookDetail form = new BookDetail(
                "1234567890123",
                "Test Title",
                OVER_101_CHARACTERS,
                "Test Publisher",
                "Test Description",
                10
        );

        var violations = VALIDATOR.validate(form);

        assertThat(violations).extracting("message")
                .contains("著者名は100文字以内である必要があります");
    }

    @Test
    @DisplayName("出版社が101文字以上の場合、バリデーションエラーが発生する")
    void shouldHaveValidationErrorWhenPublisherIsMoreThan100Characters() {
        BookDetail form = new BookDetail(
                "1234567890123",
                "Test Title",
                "Test Author",
                OVER_101_CHARACTERS,
                "Test Description",
                10
        );

        var violations = VALIDATOR.validate(form);

        assertThat(violations).extracting("message")
                .contains("出版社は100文字以内である必要があります");
    }

    @Test
    @DisplayName("説明が501文字以上の場合、バリデーションエラーが発生する")
    void shouldHaveValidationErrorWhenDescriptionIsMoreThan500Characters() {
        BookDetail form = new BookDetail(
                "1234567890123",
                "Test Title",
                "Test Author",
                "Test Publisher",
                OVER_501_CHARACTERS,
                10
        );

        var violations = VALIDATOR.validate(form);

        assertThat(violations).extracting("message")
                .contains("説明は500文字以内である必要があります");
    }

    @Test
    @DisplayName("在庫数が負の値の場合、バリデーションエラーが発生する")
    void shouldHaveValidationErrorWhenStockIsNegative() {
        BookDetail form = new BookDetail(
                "1234567890123",
                "Test Title",
                "Test Author",
                "Test Publisher",
                "Test Description",
                -1
        );

        var violations = VALIDATOR.validate(form);

        assertThat(violations).extracting("message")
                .contains("在庫数は0以上である必要があります");
    }

    @Test
    @DisplayName("在庫数が0の場合、バリデーションエラーは発生しない")
    void shouldNotHaveValidationErrorsWhenStockIsZero() {
        BookDetail form = new BookDetail(
                "1234567890123",
                "Test Title",
                "Test Author",
                "Test Publisher",
                "Test Description",
                0
        );

        var violations = VALIDATOR.validate(form);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("在庫数を除いたフィールドがNullの場合、バリデーションエラーが発生する")
    void shouldHaveValidationErrorWhenAllFieldsAreNull() {
        BookDetail form = new BookDetail(
                null,
                null,
                null,
                null,
                null,
                0
        );

        var violations = VALIDATOR.validate(form);

        assertThat(violations).extracting("message")
                .contains("ISBNは必須です")
                .contains("タイトルは必須です")
                .contains("著者名は必須です")
                .contains("出版社は必須です");
    }

    @Test
    @DisplayName("在庫数を除いたフィールドが空文字の場合、バリデーションエラーが発生する")
    void shouldHaveValidationErrorWhenAllFieldsAreEmpty() {
        BookDetail form = new BookDetail(
                "",
                "",
                "",
                "",
                "",
                0
        );

        var violations = VALIDATOR.validate(form);

        assertThat(violations).extracting("message")
                .contains("ISBNは必須です")
                .contains("タイトルは必須です")
                .contains("著者名は必須です")
                .contains("出版社は必須です");
    }

    @Test
    @DisplayName("すべてのフィールドにエラーがある場合、すべてのエラーメッセージが返される")
    void shouldReturnAllErrorMessagesWhenAllFieldsHaveErrors() {
        BookDetail form = new BookDetail(
                "",
                OVER_101_CHARACTERS,
                OVER_101_CHARACTERS,
                OVER_101_CHARACTERS,
                OVER_501_CHARACTERS,
                -1
        );

        var violations = VALIDATOR.validate(form);

        assertThat(violations).extracting("message")
                .contains("ISBNは必須です")
                .contains("ISBNは13桁の数字である必要があります")
                .contains("タイトルは100文字以内である必要があります")
                .contains("著者名は100文字以内である必要があります")
                .contains("出版社は100文字以内である必要があります")
                .contains("説明は500文字以内である必要があります")
                .contains("在庫数は0以上である必要があります");
    }
}
