# 新人研修課題 Spring Boot リファクタリング

このリポジトリは、新人研修で使う Spring Boot アプリケーションの課題用コードです。
図書の一覧表示、カート、貸出、返却、管理者向けの書籍管理、ユーザ管理を題材にしています。

コードには、研修で改善するための設計上の問題や命名の粗さを残しています。
ただし、実行環境や DB まわりは研修を始めやすいように更新済みです。

## 前提

- JDK 21
- Gradle Wrapper を使用するため、Gradle の事前インストールは不要

## 実行方法

```bash
./gradlew test
./gradlew bootRun
```

起動後、ブラウザで `http://localhost:8080` を開きます。

初期ユーザ:

| 権限 | メールアドレス | パスワード |
| --- | --- | --- |
| 一般ユーザ | `test@solxyz.co.jp` | `test` |
| 管理者 | `admin@solxyz.co.jp` | `test` |

ローカル DB は `./h2` 配下に作成されます。初期化し直す場合はアプリを停止してから `h2` ディレクトリを削除し、再度 `./gradlew bootRun` または `./gradlew test` を実行してください。

## 課題

1. `app` 配下の責務を整理し、画面/API の層とユースケースの層を分ける
2. `BookController` を書籍一覧、カート、返却の Controller に分割する
3. `BookListService`、`BookCartService`、`BookLendingService` の命名、責務、戻り値を見直す
4. presentation 層へ Entity を直接渡さず、画面/API 用の DTO や summary に変換する
5. `co`、`henkyaku`、`bim`、`blm` など、意図が伝わりにくい名前を直す
6. `LocalDateTime.now()`、`Instant.now()`、`UUID.randomUUID()` への直接依存を `Clock` や `UUIDGenerator` の注入に置き換える
7. 書影取得 API を外部サービス連携やキャッシュを含めて実装する
8. 重複した処理、過剰な例外処理、テストしづらい実装を整理する
9. 既存テストを通したまま、小さく安全にリファクタリングする

## 進め方

最初に `./gradlew test` が成功することを確認してください。
その後、1 つの課題ごとに小さく変更し、テストを実行しながら進めます。

完成形では、パッケージ構成が `presentation`、`application`、`component`、`infrastructure`、`common` の責務に沿って整理されている状態を目指します。
