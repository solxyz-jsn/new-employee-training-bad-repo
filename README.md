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

この課題では、PDF「ソルクシーズスタンダード概説」の演習に沿って、可読性を高めるための小さなリファクタリングを行います。
パッケージ名やファイル名は解答例に合わせていますが、課題として改善すべき実装は残しています。

### リファクタリング（1）Javadoc の記述

- 対象: `src/main/java/jp/co/solxyz/jsn/springbootadvancedexam/application/book/catalog/BookListUseCase.java`
- クラス、フィールド、コンストラクタ、メソッドの役割が読み手に伝わるように Javadoc を記述してください。
- PDF 上の旧対象: `/app/user/book/service/BookListService.java`

### リファクタリング（2）命名規則に従う

- 対象: `src/main/java/jp/co/solxyz/jsn/springbootadvancedexam/application/book/cart/BookCartUseCase.java`
- 意味が不明確な名前、抽象的すぎる名前、タイプミス、不適切な短縮を修正してください。
- 例: `co`、`cars`、`bim`、`blm`、`temp`、`bList`、`cBL`、`iLis`
- PDF 上の旧対象: `/app/user/book/service/BookCartService.java`

### リファクタリング（3）単一責任の原則に沿った Controller の整理

- 対象: `src/main/java/jp/co/solxyz/jsn/springbootadvancedexam/presentation/user/book/controller/BookController.java`
- 1 つの Controller に書籍一覧、カート、返却の処理がまとまっています。単一責任の原則に沿って、機能ごとの Controller に分割してください。
- PDF 上の旧対象: `/app/user/book/controller/BookController.java`

### リファクタリング（4）学んだことを活かした総合リファクタリング

- 対象:
  - `src/main/java/jp/co/solxyz/jsn/springbootadvancedexam/application/book/lending/BookLendingUseCase.java`
  - `src/main/java/jp/co/solxyz/jsn/springbootadvancedexam/component/book/lending/BookLendingManager.java`
  - `src/main/java/jp/co/solxyz/jsn/springbootadvancedexam/presentation/user/book/controller/BookController.java`
- リファクタリング（3）で Controller を分割した場合は、作成した `BookCartController.java` や `BookReturnController.java` も対象に含めてください。
- 命名、Javadoc、DRY、単一責任、マジックナンバー、現在日時への直接依存、例外処理、テストしやすさを見直してください。
- 例: `getBook`、`henkyaku`、`uid`、`i`、`blm`、`LocalDateTime.now()`、`UUID.randomUUID()`、`14`
- PDF 上の旧対象: `/app/user/book/service/BookLendingService.java`

## 進め方

最初に `./gradlew test` が成功することを確認してください。
その後、1 つの課題ごとに小さく変更し、テストを実行しながら進めます。

一部のファイル名や package は解答例と同じものになっていますが、メソッド名、変数名、責務分担、コメントの内容は課題として改善できる状態になっています。
