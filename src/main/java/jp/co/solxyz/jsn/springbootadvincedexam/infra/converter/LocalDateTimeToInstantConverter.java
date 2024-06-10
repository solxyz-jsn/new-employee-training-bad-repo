package jp.co.solxyz.jsn.springbootadvincedexam.infra.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

/**
 * LocalDateTimeとInstantの変換クラス
 */
@Converter(autoApply = true)
public class LocalDateTimeToInstantConverter implements AttributeConverter<LocalDateTime, Instant> {
    /**
     * LocalDateTimeをInstantに変換する
     * 引数がnullの場合、1970年1月1日0時0分を返す
     * @param attribute  LocalDateTime
     * @return UNIX時間
     */
    @Override
    public Instant convertToDatabaseColumn(LocalDateTime attribute) {
        return Optional.ofNullable(attribute)
                .map(attr -> attr.atZone(ZoneId.of("Asia/Tokyo")).toInstant())
                .orElse(Instant.EPOCH);
    }

    /**
     * InstantをLocalDateTimeに変換する
     * 引数がnullの場合、1970年1月1日0時0分を返す
     * @param dbData  UNIX時間
     * @return LocalDateTime
     */
    @Override
    public LocalDateTime convertToEntityAttribute(Instant dbData) {
        return Optional.ofNullable(dbData)
                .map(data -> LocalDateTime.ofInstant(data, ZoneId.of("Asia/Tokyo")))
                .orElse(LocalDateTime.of(1970, 1, 1, 0, 0));
    }
}
