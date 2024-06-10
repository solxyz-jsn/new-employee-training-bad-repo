package jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Table(name = "book_checkout_history")
@Data
@DynamicInsert
public class BookCheckoutHistory {
    @Id
    @Column(name = "rental_id")
    private String rentalId;
    @Column(name = "isbn")
    private String isbn;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "rental_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime rentalAt;
    @Column(name = "return_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime returnAt;
    @Version
    private long version;
}
