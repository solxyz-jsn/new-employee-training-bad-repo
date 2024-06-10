package jp.co.solxyz.jsn.springbootadvincedexam.infra.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class LocalDateTimeToInstantConverterTest {

    @InjectMocks
    private LocalDateTimeToInstantConverter converter;

    private final ZoneId ZONE_ID = ZoneId.of("Asia/Tokyo");

    private final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("LocalDateTimeをInstantに正常に変換する")
    void shouldConvertToDatabaseColumnSuccessfully() {
        LocalDateTime test = LOCAL_DATE_TIME;
        Instant instant = null;
        try {
            instant = converter.convertToDatabaseColumn(test);
        } catch (Exception e) {
            fail();
        }
        assertThat(instant).isEqualTo(test.atZone(ZONE_ID).toInstant());
    }

    @Test
    @DisplayName("nullをInstantに変換するとエポックタイム(1970-01-01T00:00:00Z)が返る")
    void shouldConvertNullToDatabaseColumn() {
        Instant instant = null;
        try {
            instant = converter.convertToDatabaseColumn(null);
        } catch (Exception e) {
            fail();
        }
        assertThat(instant).isEqualTo(Instant.EPOCH);
    }

    @Test
    @DisplayName("InstantをLocalDateTimeに正常に変換する")
    void shouldConvertToEntityAttributeSuccessfully() {
        Instant test = Instant.parse("2021-01-01T00:00:00Z");
        LocalDateTime localDateTime = null;
        try {
            localDateTime = converter.convertToEntityAttribute(test);
        } catch (Exception e) {
            fail();
        }
        assertThat(localDateTime).isEqualTo(LocalDateTime.ofInstant(test, ZONE_ID));
    }

    @Test
    @DisplayName("nullをLocalDateTimeに変換すると1970年1月1日0時0分が返る")
    void shouldConvertNullToEntityAttribute() {
        LocalDateTime localDateTime = null;
        try {
            localDateTime = converter.convertToEntityAttribute(null);
        } catch (Exception e) {
            fail();
        }
        assertThat(localDateTime).isEqualTo(LocalDateTime.of(1970, 1, 1, 0, 0));
    }
}
