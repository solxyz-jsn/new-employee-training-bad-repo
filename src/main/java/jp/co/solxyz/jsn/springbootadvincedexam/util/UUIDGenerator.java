package jp.co.solxyz.jsn.springbootadvincedexam.util;

import java.util.UUID;

/**
 * UUID生成クラス
 */
public class UUIDGenerator {

    /**
     * ユーザIDを生成する
     * @return UUID
     */
    public static String generateUserId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

}
