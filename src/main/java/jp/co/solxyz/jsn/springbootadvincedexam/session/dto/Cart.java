package jp.co.solxyz.jsn.springbootadvincedexam.session.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Cart implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String isbn;
}
