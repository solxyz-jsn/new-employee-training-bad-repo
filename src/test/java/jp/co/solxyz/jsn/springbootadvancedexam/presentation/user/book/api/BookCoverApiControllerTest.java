package jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.api;

import jp.co.solxyz.jsn.springbootadvancedexam.application.book.catalog.BookCoverUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class BookCoverApiControllerTest {

    @InjectMocks
    private BookCoverApiController bookCoverApiController;

    @Mock
    private BookCoverUseCase bookCoverUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("ISBNが指定された場合、書影URLのマップが返される")
    void shouldReturnCoverUrlsWhenIsbnIsSpecified() {
        List<String> expectedIsbnList = List.of("9784873117904", "9784873119380");
        Map<String, String> coverUrls = Map.of(
                "9784873117904", "https://cover.openbd.jp/9784873117904.jpg",
                "9784873119380", "https://cover.openbd.jp/9784873119380.jpg");
        when(bookCoverUseCase.getCoverUrls(expectedIsbnList)).thenReturn(coverUrls);

        ResponseEntity<Map<String, String>> response =
                bookCoverApiController.getCoverUrls("9784873117904, 9784873119380,9784873117904");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(coverUrls);
        verify(bookCoverUseCase, times(1)).getCoverUrls(expectedIsbnList);
    }

    @Test
    @DisplayName("ISBNが空の場合、BadRequestが返される")
    void shouldReturnBadRequestWhenIsbnIsEmpty() {
        ResponseEntity<Map<String, String>> response = bookCoverApiController.getCoverUrls(" , ");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(bookCoverUseCase);
    }

    @Test
    @DisplayName("ISBNの形式が不正な場合、BadRequestが返される")
    void shouldReturnBadRequestWhenIsbnFormatIsInvalid() {
        ResponseEntity<Map<String, String>> response = bookCoverApiController.getCoverUrls("9784873117904, invalid");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(bookCoverUseCase);
    }

    @Test
    @DisplayName("ISBNが上限件数を超える場合、BadRequestが返される")
    void shouldReturnBadRequestWhenIsbnCountExceedsLimit() {
        String isbns = IntStream.range(0, 21)
                .mapToObj(index -> String.format("978487311%04d", index))
                .collect(Collectors.joining(","));

        ResponseEntity<Map<String, String>> response = bookCoverApiController.getCoverUrls(isbns);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(bookCoverUseCase);
    }

    @Test
    @DisplayName("重複を含むISBN指定でも上限件数を超える場合、BadRequestが返される")
    void shouldReturnBadRequestWhenRawIsbnCountExceedsLimit() {
        String isbns = IntStream.range(0, 21)
                .mapToObj(index -> "9784873117904")
                .collect(Collectors.joining(","));

        ResponseEntity<Map<String, String>> response = bookCoverApiController.getCoverUrls(isbns);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(bookCoverUseCase);
    }
}
