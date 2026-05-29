package jp.co.solxyz.jsn.springbootadvancedexam.application.book.catalog;

import jp.co.solxyz.jsn.springbootadvancedexam.application.book.catalog.cache.BookCoverCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BookCoverUseCaseTest {

    private static final String OPENBD_GET_URL = "https://api.openbd.jp/v1/get?isbn=";

    private static final String ISBN = "9784873117904";

    private static final String COVER_URL = "https://cover.openbd.jp/9784873117904.jpg";

    private static final String OTHER_ISBN = "9784873119380";

    private static final String OTHER_COVER_URL = "https://cover.openbd.jp/9784873119380.jpg";

    private BookCoverUseCase bookCoverUseCase;

    private RestClient restClient;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestClient.Builder restClientBuilder = RestClient.builder()
                .baseUrl("https://api.openbd.jp");
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        restClient = restClientBuilder.build();
        bookCoverUseCase = createBookCoverUseCase();
    }

    @Test
    @DisplayName("書影URLを取得できた場合、ISBNごとにキャッシュされる")
    void shouldCacheCoverUrlWhenOpenBdReturnsCover() throws Exception {
        expectSuccess(ISBN, "[{\"summary\":{\"cover\":\"" + COVER_URL + "\"}}]");

        Map<String, String> actual = bookCoverUseCase.getCoverUrls(List.of(" " + ISBN + " ", ISBN));
        Map<String, String> cached = bookCoverUseCase.getCoverUrls(List.of(ISBN));

        assertThat(actual).containsExactly(Map.entry(ISBN, COVER_URL));
        assertThat(cached).containsExactly(Map.entry(ISBN, COVER_URL));
        mockServer.verify();
    }

    @Test
    @DisplayName("キャッシュ上限を超える場合、古いISBNが削除される")
    void shouldEvictOldestCoverWhenCacheLimitIsExceeded() throws Exception {
        bookCoverUseCase = createBookCoverUseCase(1);
        expectSuccess(ISBN, "[{\"summary\":{\"cover\":\"" + COVER_URL + "\"}}]");
        expectSuccess(OTHER_ISBN, "[{\"summary\":{\"cover\":\"" + OTHER_COVER_URL + "\"}}]");
        expectSuccess(ISBN, "[{\"summary\":{\"cover\":\"" + COVER_URL + "\"}}]");

        Map<String, String> first = bookCoverUseCase.getCoverUrls(List.of(ISBN));
        Map<String, String> second = bookCoverUseCase.getCoverUrls(List.of(OTHER_ISBN));
        Map<String, String> reloadedFirst = bookCoverUseCase.getCoverUrls(List.of(ISBN));

        assertThat(first).containsExactly(Map.entry(ISBN, COVER_URL));
        assertThat(second).containsExactly(Map.entry(OTHER_ISBN, OTHER_COVER_URL));
        assertThat(reloadedFirst).containsExactly(Map.entry(ISBN, COVER_URL));
        mockServer.verify();
    }

    @Test
    @DisplayName("キャッシュ済みISBNと未取得ISBNを同時に指定しても、キャッシュ済みの書影を返す")
    void shouldReturnCachedCoverWhenRequestMixesCachedAndMissingIsbn() throws Exception {
        bookCoverUseCase = createBookCoverUseCase(1);
        expectSuccess(ISBN, "[{\"summary\":{\"cover\":\"" + COVER_URL + "\"}}]");
        expectSuccess(OTHER_ISBN, "[{\"summary\":{\"cover\":\"" + OTHER_COVER_URL + "\"}}]");

        bookCoverUseCase.getCoverUrls(List.of(ISBN));
        Map<String, String> mixed = bookCoverUseCase.getCoverUrls(List.of(ISBN, OTHER_ISBN));

        assertThat(mixed).containsExactly(
                Map.entry(ISBN, COVER_URL),
                Map.entry(OTHER_ISBN, OTHER_COVER_URL));
        mockServer.verify();
    }

    @Test
    @DisplayName("openBDの一時的な失敗は書影なしとしてキャッシュしない")
    void shouldNotCacheMissingCoverWhenOpenBdTemporarilyFails() throws Exception {
        expectServerError(ISBN);
        expectSuccess(ISBN, "[{\"summary\":{\"cover\":\"" + COVER_URL + "\"}}]");

        Map<String, String> failed = bookCoverUseCase.getCoverUrls(List.of(ISBN));
        Map<String, String> recovered = bookCoverUseCase.getCoverUrls(List.of(ISBN));

        assertThat(failed).isEmpty();
        assertThat(recovered).containsExactly(Map.entry(ISBN, COVER_URL));
        mockServer.verify();
    }

    @Test
    @DisplayName("openBDが書影なしを返した場合、再取得しない")
    void shouldCacheMissingCoverWhenOpenBdReturnsNoCover() throws Exception {
        expectSuccess(ISBN, "[null]");

        Map<String, String> first = bookCoverUseCase.getCoverUrls(List.of(ISBN));
        Map<String, String> second = bookCoverUseCase.getCoverUrls(List.of(ISBN));

        assertThat(first).isEmpty();
        assertThat(second).isEmpty();
        mockServer.verify();
    }

    @Test
    @DisplayName("openBDへのリクエストが例外になった場合、書影なしとしてキャッシュしない")
    void shouldNotCacheMissingCoverWhenOpenBdRequestThrowsException() throws Exception {
        expectException(ISBN);
        expectSuccess(ISBN, "[{\"summary\":{\"cover\":\"" + COVER_URL + "\"}}]");

        Map<String, String> failed = bookCoverUseCase.getCoverUrls(List.of(ISBN));
        Map<String, String> recovered = bookCoverUseCase.getCoverUrls(List.of(ISBN));

        assertThat(failed).isEmpty();
        assertThat(recovered).containsExactly(Map.entry(ISBN, COVER_URL));
        mockServer.verify();
    }

    private BookCoverUseCase createBookCoverUseCase() {
        return createBookCoverUseCase(500);
    }

    private BookCoverUseCase createBookCoverUseCase(int maxCacheSize) {
        return new BookCoverUseCase(restClient, JsonMapper.builder().build(), new BookCoverCache(maxCacheSize));
    }

    private void expectSuccess(String isbn, String body) {
        mockServer.expect(requestTo(OPENBD_GET_URL + isbn))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));
    }

    private void expectServerError(String isbn) {
        mockServer.expect(requestTo(OPENBD_GET_URL + isbn))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private void expectException(String isbn) {
        mockServer.expect(requestTo(OPENBD_GET_URL + isbn))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withException(new IOException()));
    }
}
