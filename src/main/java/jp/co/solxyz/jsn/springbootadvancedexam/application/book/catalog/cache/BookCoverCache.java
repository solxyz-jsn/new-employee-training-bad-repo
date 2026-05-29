package jp.co.solxyz.jsn.springbootadvancedexam.application.book.catalog.cache;

import org.springframework.stereotype.Component;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ISBNごとの書影URLキャッシュ
 */
@Component
public class BookCoverCache {

    /**
     * 書影なしを表すキャッシュ値
     */
    private static final String MISSING_COVER = "";

    /**
     * キャッシュできるISBN数
     */
    private static final int MAX_CACHE_SIZE = 500;

    /**
     * ISBNごとの書影URL
     */
    private final Map<String, String> coverUrls;

    /**
     * コンストラクタ
     */
    public BookCoverCache() {
        this(MAX_CACHE_SIZE);
    }

    /**
     * 最大キャッシュ数を指定して書影URLキャッシュを作成する
     * @param maxCacheSize キャッシュできるISBN数
     */
    public BookCoverCache(int maxCacheSize) {
        this.coverUrls = Collections.synchronizedMap(new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size() > maxCacheSize;
            }
        });
    }

    /**
     * 書影URLを取得
     * @param isbn ISBN
     * @return 書影URL
     */
    public @Nullable String get(String isbn) {
        return coverUrls.get(isbn);
    }

    /**
     * 書影URLを保存
     * @param isbn ISBN
     * @param coverUrl 書影URL
     */
    public void put(String isbn, String coverUrl) {
        coverUrls.put(isbn, coverUrl);
    }

    /**
     * 書影なしとして保存
     * @param isbn ISBN
     */
    public void putMissing(String isbn) {
        coverUrls.put(isbn, MISSING_COVER);
    }

    /**
     * 書影URLが空でないか判定
     * @param coverUrl 書影URL
     * @return 書影URLが空でなければtrue
     */
    public boolean hasCover(@Nullable String coverUrl) {
        return coverUrl != null && !coverUrl.isBlank();
    }
}
