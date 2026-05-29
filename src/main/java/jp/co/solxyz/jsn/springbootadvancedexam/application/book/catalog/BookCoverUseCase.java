package jp.co.solxyz.jsn.springbootadvancedexam.application.book.catalog;

import jp.co.solxyz.jsn.springbootadvancedexam.application.book.catalog.cache.BookCoverCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 書影取得ユースケース
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookCoverUseCase {

    /**
     * openBD APIの書籍取得パス
     */
    private static final String OPENBD_GET_PATH = "/v1/get";

    /**
     * openBD API用RESTクライアント
     */
    private final RestClient openBdRestClient;

    /**
     * JSONマッパー
     */
    private final JsonMapper jsonMapper;

    /**
     * ISBNごとの書影URLキャッシュ
     */
    private final BookCoverCache bookCoverCache;

    /**
     * ISBNに対応する書影URLを取得
     * @param isbnList ISBNリスト
     * @return ISBNと書影URLのマップ
     */
    public Map<String, String> getCoverUrls(List<String> isbnList) {
        List<String> normalizedIsbns = isbnList.stream()
                .map(String::trim)
                .filter(isbn -> !isbn.isBlank())
                .distinct()
                .toList();

        Map<String, String> coverUrls = new LinkedHashMap<>();
        List<String> missingIsbns = findMissingIsbns(normalizedIsbns, coverUrls);
        if (!missingIsbns.isEmpty()) {
            fetchAndCacheCovers(missingIsbns);
        }

        putFetchedCoverUrls(normalizedIsbns, coverUrls);
        return coverUrls;
    }

    /**
     * キャッシュに結果がないISBNを抽出する
     * @param isbnList ISBNリスト
     * @param coverUrls 書影URLの結果マップ
     * @return キャッシュミスしたISBNリスト
     */
    private List<String> findMissingIsbns(List<String> isbnList, Map<String, String> coverUrls) {
        List<String> missingIsbns = new ArrayList<>();
        for (String isbn : isbnList) {
            if (!putCachedCoverUrl(isbn, coverUrls)) {
                missingIsbns.add(isbn);
            }
        }
        return missingIsbns;
    }

    /**
     * openBD取得後にキャッシュへ保存された書影URLを結果に反映する
     * @param isbnList ISBNリスト
     * @param coverUrls 書影URLの結果マップ
     */
    private void putFetchedCoverUrls(List<String> isbnList, Map<String, String> coverUrls) {
        for (String isbn : isbnList) {
            if (coverUrls.containsKey(isbn)) {
                continue;
            }
            putCachedCoverUrl(isbn, coverUrls);
        }
    }

    /**
     * キャッシュ済みの書影URLがあれば結果に追加する
     * @param isbn ISBN
     * @param coverUrls 書影URLの結果マップ
     * @return キャッシュにISBNの結果が存在すればtrue
     */
    private boolean putCachedCoverUrl(String isbn, Map<String, String> coverUrls) {
        String coverUrl = bookCoverCache.get(isbn);
        if (coverUrl == null) {
            return false;
        }
        if (bookCoverCache.hasCover(coverUrl)) {
            coverUrls.put(isbn, coverUrl);
        }
        return true;
    }

    /**
     * openBDから書影URLを取得してキャッシュする
     * @param isbnList 書影URLを取得するISBNリスト
     */
    private void fetchAndCacheCovers(List<String> isbnList) {
        try {
            String responseBody = openBdRestClient.get()
                    .uri(buildOpenBdPath(isbnList))
                    .retrieve()
                    .body(String.class);
            if (responseBody == null) {
                return;
            }
            JsonNode root = jsonMapper.readTree(responseBody);
            for (int index = 0; index < isbnList.size(); index++) {
                String isbn = isbnList.get(index);
                String coverUrl = readCoverUrl(root, index);
                if (coverUrl.isBlank()) {
                    bookCoverCache.putMissing(isbn);
                } else {
                    bookCoverCache.put(isbn, coverUrl);
                }
            }
        } catch (RestClientException e) {
            log.warn("openBDから書影を取得できませんでした。", e);
        } catch (JacksonException e) {
            log.warn("openBDのレスポンスを解析できませんでした。", e);
        }
    }

    /**
     * openBD APIの取得パスを作成する
     * @param isbnList 取得対象のISBNリスト
     * @return openBD APIの取得パス
     */
    private String buildOpenBdPath(List<String> isbnList) {
        String joinedIsbns = isbnList.stream()
                .map(isbn -> URLEncoder.encode(isbn, StandardCharsets.UTF_8))
                .collect(Collectors.joining(","));
        return OPENBD_GET_PATH + "?isbn=" + joinedIsbns;
    }

    /**
     * openBDレスポンスから指定位置の書影URLを読み取る
     * @param root openBDレスポンスのルートノード
     * @param index ISBNリスト上の位置
     * @return 書影URL。存在しない場合は空文字
     */
    private String readCoverUrl(JsonNode root, int index) {
        if (!root.isArray() || root.size() <= index || root.get(index).isNull()) {
            return "";
        }

        JsonNode cover = root.get(index).path("summary").path("cover");
        if (cover.isMissingNode() || cover.isNull() || cover.asString().isBlank()) {
            return "";
        }
        return cover.asString();
    }
}
