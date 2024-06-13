package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.api;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.json.ReturnBookIsbn;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service.BookLendingService;
import jp.co.solxyz.jsn.springbootadvincedexam.security.MyUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookReturnApiControllerTest {

    @InjectMocks
    private BookReturnApiController bookReturnApiController;

    @Mock
    private BookLendingService bookReturnService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("有効なリクエストの場合、200レスポンスを返す")
    void shouldReturnBookWhenValidRequest() {
        String userId = "user1";
        String isbn = "9784774192359";

        ReturnBookIsbn returnBookIsbn = new ReturnBookIsbn();
        returnBookIsbn.setIsbn(isbn);
        MyUserDetails userDetails = mock(MyUserDetails.class);
        when(userDetails.getUserId()).thenReturn(userId);

        ResponseEntity<Void> response = bookReturnApiController.returnBook(returnBookIsbn, userDetails);

        verify(bookReturnService, times(1)).henkyaku(userId, isbn);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("ReturnBookInfoがnullの場合、BadRequestを返す")
    void shouldReturnBadRequestWhenReturnBookInfoIsNull() {
        String userId = "user1";
        String isbn = "9784774192359";
        MyUserDetails userDetails = mock(MyUserDetails.class);

        ResponseEntity<Void> response = bookReturnApiController.returnBook(null, userDetails);

        verify(bookReturnService, never()).henkyaku(userId, isbn);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("ISBNがnullの場合、BadRequestを返す")
    void shouldReturnBadRequestWhenIsbnIsNull() {
        ReturnBookIsbn returnBookIsbn = new ReturnBookIsbn();
        returnBookIsbn.setIsbn(null);
        MyUserDetails userDetails = mock(MyUserDetails.class);

        ResponseEntity<Void> response = bookReturnApiController.returnBook(returnBookIsbn, userDetails);

        verify(bookReturnService, never()).henkyaku(anyString(), anyString());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("userDetailsがnullの場合、BadRequestを返す")
    void shouldReturnBadRequestWhenUserDetailsIsNull() {
        ReturnBookIsbn returnBookIsbn = new ReturnBookIsbn();
        returnBookIsbn.setIsbn("9784774192359");

        ResponseEntity<Void> response = bookReturnApiController.returnBook(returnBookIsbn, null);

        verify(bookReturnService, never()).henkyaku(anyString(), anyString());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("NoSuchElementExceptionがスローされた場合、BadRequestを返す")
    void shouldReturnBadRequestWhenNoSuchElementExceptionIsThrown() {
        String isbn = "9784774192359";
        ReturnBookIsbn returnBookIsbn = new ReturnBookIsbn();
        returnBookIsbn.setIsbn(isbn);

        MyUserDetails userDetails = mock(MyUserDetails.class);
        String userId = "user1";

        when(userDetails.getUserId()).thenReturn(userId);
        doThrow(NoSuchElementException.class).when(bookReturnService).henkyaku(userId, isbn);

        ResponseEntity<Void> response = bookReturnApiController.returnBook(returnBookIsbn, userDetails);

        verify(bookReturnService, times(1)).henkyaku(userId, isbn);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("DataIntegrityViolationExceptionがスローされた場合、BadRequestを返す")
    void shouldReturnBadRequestWhenDataIntegrityViolationExceptionIsThrown() {
        String isbn = "9784774192359";
        ReturnBookIsbn returnBookIsbn = new ReturnBookIsbn();
        returnBookIsbn.setIsbn(isbn);

        MyUserDetails userDetails = mock(MyUserDetails.class);
        String userId = "user1";

        when(userDetails.getUserId()).thenReturn(userId);
        doThrow(DataIntegrityViolationException.class).when(bookReturnService).henkyaku(userId, isbn);

        ResponseEntity<Void> response = bookReturnApiController.returnBook(returnBookIsbn, userDetails);

        verify(bookReturnService, times(1)).henkyaku(userId, isbn);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
