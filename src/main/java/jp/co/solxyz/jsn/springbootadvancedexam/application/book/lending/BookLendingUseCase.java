package jp.co.solxyz.jsn.springbootadvancedexam.application.book.lending;

import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.model.UnreturnedBookModel;
import jp.co.solxyz.jsn.springbootadvancedexam.component.book.lending.BookLendingManager;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

// TODO: リファクタリング（４）
// TODO: 今までの演習で学んだことを活かして、このクラスをリファクタリングしましょう
@Service
public class BookLendingUseCase {

    private final BookLendingManager blm;

    public BookLendingUseCase(BookLendingManager b1) {
        this.blm = b1;
    }

    public List<UnreturnedBookModel> getBook(String uid) {
        return blm.getUnreturnedBooksByUserId(uid);
    }

    public void henkyaku(String uid, String i) throws DataAccessException, NoSuchElementException {
        blm.returnBook(uid, i);
    }
}
