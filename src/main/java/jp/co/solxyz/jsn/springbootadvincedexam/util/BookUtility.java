package jp.co.solxyz.jsn.springbootadvincedexam.util;

/**
 * 書籍ユーティリティ
 */
public class BookUtility {

    /**
     * フォーマットされたISBNを取得する
     *
     * @param number 13桁の数字
     * @return ISBN形式でハイフン処理された文字列
     */
    public static String getFormattedISBN(String number) {
        if (!number.matches("\\d{13}")) {
            throw new IllegalArgumentException("引数には13桁の数字のみが使用できます: " + number);
        }

        return String.format("%s-%s-%s-%s-%s", number.substring(0, 3), number.charAt(3), number.substring(4, 8), number.substring(8, 12), number.substring(12));

    }
}
