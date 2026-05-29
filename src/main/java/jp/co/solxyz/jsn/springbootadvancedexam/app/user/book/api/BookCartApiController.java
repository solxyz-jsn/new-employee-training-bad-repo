package jp.co.solxyz.jsn.springbootadvancedexam.app.user.book.api;

import jp.co.solxyz.jsn.springbootadvancedexam.app.user.book.json.CartIsbn;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.session.Cart;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.session.CartSession;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * カートAPIコントローラ
 */
@RestController
@RequestMapping("/api/book/cart")
@Slf4j
public class BookCartApiController {

    /**
     * カートセッション
     */
    private final CartSession cartSession;

    /**
     * コンストラクタ
     * @param cartSession カートセッション
     */
    public BookCartApiController(CartSession cartSession) {
        this.cartSession = cartSession;
    }

    /**
     * カートに追加
     * @param cartIsbn カート情報
     * @return レスポンス
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> add(@Nullable @RequestBody CartIsbn cartIsbn) {
        if (cartIsbn == null || cartIsbn.getIsbn() == null) {
            log.info("ISBN is null");
            return ResponseEntity.badRequest().build();
        }

        Cart cart = new Cart();
        cart.setIsbn(cartIsbn.getIsbn());
        cartSession.addCart(cart);
        return ResponseEntity.ok(cartSession.getCartList().size());
    }

    /**
     * カートから削除
     * @param cartIsbn カート情報
     * @return レスポンス
     */
    @DeleteMapping
    public ResponseEntity<Void> delete(@Nullable @RequestBody CartIsbn cartIsbn) {
        if (cartIsbn == null || cartIsbn.getIsbn() == null) {
            log.info("ISBN is null");
            return ResponseEntity.badRequest().build();
        }

        cartSession.removeCart(cartIsbn.getIsbn());
        return ResponseEntity.ok().build();
    }

}
