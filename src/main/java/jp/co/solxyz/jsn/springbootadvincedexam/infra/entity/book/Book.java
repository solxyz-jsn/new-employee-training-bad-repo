package jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.converter.LocalDateTimeToInstantConverter;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 書籍エンティティ
 */
@Entity
@Table(name = "books")
@Data
public class Book implements Serializable {

    /**
     * シリアルバージョンUID
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ISBN
     */
    @Id
    private String isbn;

    /**
     * タイトル
     */
    private String title;

    /**
     * 著者
     */
    private String author;

    /**
     * 出版社
     */
    private String publisher;

    /**
     * 在庫数
     */
    private int stock;

    /**
     * 利用可能在庫数
     */
    private int availableStock;

    /**
     * 書籍説明
     */
    private String description;

    /**
     * 作成日時
     */
    @Column(columnDefinition = "TIMESTAMP")
    @Convert(converter = LocalDateTimeToInstantConverter.class)
    private LocalDateTime createdAt;

    /**
     * 更新日時
     */
    @Column(columnDefinition = "TIMESTAMP")
    @Convert(converter = LocalDateTimeToInstantConverter.class)
    private LocalDateTime updatedAt;
}
