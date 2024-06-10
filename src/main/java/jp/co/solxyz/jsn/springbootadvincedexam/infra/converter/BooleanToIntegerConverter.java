package jp.co.solxyz.jsn.springbootadvincedexam.infra.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * BooleanとIntegerの変換を行うコンバータ
 */
@Converter(autoApply = true)
public class BooleanToIntegerConverter implements AttributeConverter<Boolean, Integer> {

    /**
     * BooleanをIntegerに変換する
     * @param attribute 変換元のBoolean
     * @return 変換後のInteger
     */
    @Override
    public Integer convertToDatabaseColumn(Boolean attribute) {
        if (Boolean.TRUE.equals(attribute)) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * IntegerをBooleanに変換する
     *
     * @param dbData 変換元のInteger
     * @return 変換後のBoolean
     */
    @Override
    public Boolean convertToEntityAttribute(Integer dbData) {
        return dbData != null && dbData.equals(1);
    }
}
