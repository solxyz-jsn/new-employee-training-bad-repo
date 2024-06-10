package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.CartBookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.component.book.BookLendingManager;
import jp.co.solxyz.jsn.springbootadvincedexam.component.book.BookInventoryManager;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import jp.co.solxyz.jsn.springbootadvincedexam.session.dto.Cart;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// TODO: リファクタリング（２）
/**
 * 書籍カートサービス
 */
@Service
public class BookCartService {

    /**
     * 書籍サービスビジネスロジック
     */
    // TODO: bimをみんなが分かりやすい変数名にしよう
    private final BookInventoryManager bim;

    /**
     * 書籍貸出ビジネスロジック
     */
    // TODO: blmをみんなが分かりやすい変数名にしよう
    private final BookLendingManager blm;

    /**
     * コンストラクタ
     * @param b1 書籍サービスビジネスロジック
     * @param b2 書籍貸出ビジネスロジック
     */
    // TODO: メソッドの引数を分かりやすい変数名にしよう
    public BookCartService(BookInventoryManager b1, BookLendingManager b2) {
        this.bim = b1;
        this.blm = b2;
    }

    /**
     * カート情報取得
     * @param cars カート情報
     * @return カート書籍情報
     */
    // TODO: メソッドの引数のタイポを修正しよう
    public List<CartBookModel> getCartList(List<Cart> cars) {
        // TODO: tempではなく、わかりやすい変数名にしよう
        List<String> temp = new ArrayList<>();
        for (Cart c : cars) temp.add(c.getIsbn());
        // TODO: bListではなく、わかりやすい変数名にしよう
        List<Book> bList = bim.getBooksByIsbn(temp);
        // TODO: cBLではなく、わかりやすい変数名にしよう
        List<CartBookModel> cBL = new ArrayList<>();
        // TODO: bではなく、わかりやすい変数名にしよう
        for (Book b : bList) cBL.add(new CartBookModel(b.getIsbn(), b.getTitle(), b.getAuthor(), b.getPublisher()));

        return cBL;
    }

    /**
     * 書籍の貸出処理
     * @param uid ユーザID
     * @param cars カート情報
     * @return 借りれなかった書籍一覧
     */
    // TODO: coではなく、わかりやすいメソッド名にしよう
    // TODO: メソッドの引数のタイポを修正しよう
    // TODO: メソッドの引数のuidを修正しよう
    public List<Book> co(String uid, List<Cart> cars) {
        // TODO: iLisではなく、わかりやすい変数名にしよう
        List<String> iLis = new ArrayList<>();
        // TODO: cではなく、わかりやすい変数名にしよう
        for (Cart c : cars) iLis.add(c.getIsbn());

        return blm.checkout(uid, iLis);
    }
}
