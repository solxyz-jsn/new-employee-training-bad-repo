package jp.co.solxyz.jsn.springbootadvincedexam.app.user.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UUIDGeneratorTest {

    @Test
    @DisplayName("ユーザーIDが正常に生成される")
    void shouldGenerateUserIdSuccessfully() {
        String userId = UUIDGenerator.generateUserId();

        assertThat(userId).isNotNull();
        assertThat(userId).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }
}
