package jp.co.solxyz.jsn.springbootadvincedexam.infra.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

public class BooleanToIntegerConverterTest {

    @InjectMocks
    private BooleanToIntegerConverter converter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("真の場合、1に変換する")
    void convertTrueToInteger() {
        Integer result = converter.convertToDatabaseColumn(true);
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("偽の場合、0に変換する")
    void convertFalseToInteger() {
        Integer result = converter.convertToDatabaseColumn(false);
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("nullの場合、0に変換する")
    void convertNullToInteger() {
        Integer result = converter.convertToDatabaseColumn(null);
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("1の場合、真に変換する")
    void convertOneToBoolean() {
        Boolean result = converter.convertToEntityAttribute(1);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("0の場合、偽に変換する")
    void convertZeroToBoolean() {
        Boolean result = converter.convertToEntityAttribute(0);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("nullの場合、偽に変換する")
    void convertNullToBoolean() {
        Boolean result = converter.convertToEntityAttribute(null);
        assertThat(result).isFalse();
    }
}
