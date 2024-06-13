package jp.co.solxyz.jsn.springbootadvincedexam.app.admin.book.api;

import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.book.json.BookDetail;
import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.book.service.BookManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BookManagementApiControllerTest {

    @InjectMocks
    private BookManagementApiController controller;

    @Mock
    private BookManagementService service;

    @Mock
    private BindingResult bindingResult;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("書籍登録が正常に行われ、200レスポンスが返る")
    public void shouldAddBookSuccessfully() {
        BookDetail form = new BookDetail();
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<List<ObjectError>> response = controller.addBook(form, bindingResult);

        verify(service, times(1)).addBook(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("書籍登録時にバリデーションエラーがある場合、bad requestが返る")
    public void shouldFailToAddBookDueToValidationError() {
        BookDetail form = new BookDetail();
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<List<ObjectError>> response = controller.addBook(form, bindingResult);

        verify(service, times(0)).addBook(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("書籍更新が正常に行われ、200レスポンスが返る")
    public void shouldUpdateBookSuccessfully() {
        BookDetail form = new BookDetail();
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<List<ObjectError>> response = controller.updateBook(form, bindingResult);

        verify(service, times(1)).updateBook(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("書籍更新時にバリデーションエラーがある場合、bad requestが返る")
    public void shouldFailToUpdateBookDueToValidationError() {
        BookDetail form = new BookDetail();
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<List<ObjectError>> response = controller.updateBook(form, bindingResult);

        verify(service, times(0)).updateBook(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("書籍削除が正常に行われ、200レスポンスが返る")
    public void shouldDeleteBookSuccessfully() {
        String isbn = "978-4-7741-9234-3";
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<List<ObjectError>> response = controller.deleteBook(isbn);

        verify(service, times(1)).deleteBook(isbn);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("書籍削除時にバリデーションエラーがある場合、bad requestが返る")
    public void shouldFailToDeleteBookDueToValidationError() throws Exception {
        String isbn = "invalid isbn";

        mockMvc.perform(delete("/api/admin/management/book/{isbn}", isbn))
                .andExpect(status().isBadRequest());

        verify(service, never()).deleteBook(isbn);
    }

    @Test
    @DisplayName("書籍登録時にIllegalArgumentExceptionが発生する")
    public void shouldFailToAddBookDueToIllegalArgumentException() {
        BookDetail form = new BookDetail();
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new IllegalArgumentException("test error") {
        }).when(service).addBook(form);

        ResponseEntity<List<ObjectError>> response = controller.addBook(form, bindingResult);

        verify(service, times(1)).addBook(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().get(0)).isEqualTo("test error");
    }

    @Test
    @DisplayName("書籍登録時にDataAccessExceptionが発生する")
    public void shouldFailToAddBookDueToDataAccessException() {
        BookDetail form = new BookDetail();
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DataAccessException("test") {
        }).when(service).addBook(form);

        ResponseEntity<List<ObjectError>> response = controller.addBook(form, bindingResult);

        verify(service, times(1)).addBook(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().stream().map(ObjectError::getDefaultMessage).toList().get(0)).isEqualTo(
                "書籍情報の登録に失敗しました。");
    }

    @Test
    @DisplayName("書籍更新時にIllegalArgumentExceptionが発生する")
    public void shouldFailToUpdateBookDueToIllegalArgumentException() {
        BookDetail form = new BookDetail();
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new IllegalArgumentException("test error")).when(service).updateBook(form);

        ResponseEntity<List<ObjectError>> response = controller.updateBook(form, bindingResult);

        verify(service, times(1)).updateBook(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().get(0)).isEqualTo("test error");
    }

    @Test
    @DisplayName("書籍更新時にNoSuchElementExceptionが発生する")
    public void shouldFailToUpdateBookDueToNoSuchElementException() {
        BookDetail form = new BookDetail();
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new NoSuchElementException("test error")).when(service).updateBook(form);

        ResponseEntity<List<ObjectError>> response = controller.updateBook(form, bindingResult);

        verify(service, times(1)).updateBook(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().get(0)).isEqualTo("test error");
    }

    @Test
    @DisplayName("書籍更新時にOptimisticLockingFailureExceptionが発生する")
    public void shouldFailToUpdateBookDueToOptimisticLockingFailureException() {
        BookDetail form = new BookDetail();
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new OptimisticLockingFailureException("test error")).when(service).updateBook(form);

        ResponseEntity<List<ObjectError>> response = controller.updateBook(form, bindingResult);

        verify(service, times(1)).updateBook(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().get(0)).isEqualTo("test error");
    }

    @Test
    @DisplayName("書籍更新時にDataAccessExceptionが発生する")
    public void shouldFailToUpdateBookDueToDataAccessException() {
        BookDetail form = new BookDetail();
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DataAccessException("test") {
        }).when(service).updateBook(form);

        ResponseEntity<List<ObjectError>> response = controller.updateBook(form, bindingResult);

        verify(service, times(1)).updateBook(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().get(0)).isEqualTo(
                "書籍情報の更新に失敗しました。");
    }

    @Test
    @DisplayName("書籍削除時にIllegalStateExceptionが発生する")
    public void shouldFailToDeleteBookDueToIllegalStateException() {
        String isbn = "978-4-7741-9234-3";
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new IllegalStateException("test error")).when(service).deleteBook(isbn);

        ResponseEntity<List<ObjectError>> response = controller.deleteBook(isbn);

        verify(service, times(1)).deleteBook(isbn);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().get(0)).isEqualTo("test error");
    }

    @Test
    @DisplayName("書籍削除時にNoSuchElementExceptionが発生する")
    public void shouldFailToDeleteBookDueToNoSuchElementException() {
        String isbn = "978-4-7741-9234-3";
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new NoSuchElementException("test error")).when(service).deleteBook(isbn);

        ResponseEntity<List<ObjectError>> response = controller.deleteBook(isbn);

        verify(service, times(1)).deleteBook(isbn);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().get(0)).isEqualTo("test error");
    }

    @Test
    @DisplayName("書籍削除時にDataAccessExceptionが発生する")
    public void shouldFailToDeleteBookDueToDataAccessException() {
        String isbn = "978-4-7741-9234-3";
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DataAccessException("test") {
        }).when(service).deleteBook(isbn);

        ResponseEntity<List<ObjectError>> response = controller.deleteBook(isbn);

        verify(service, times(1)).deleteBook(isbn);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().get(0)).isEqualTo(
                "書籍情報の削除に失敗しました。");
    }
}
