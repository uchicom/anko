# CLAUDE.md — anko プロジェクト

## プロジェクト概要

**anko** はビジネス向けWebアプリケーションのためのBuildlessフレームワーク。

- バックエンドのDTOをSingle Source of Truthとして、バリデーションルールをフロントエンドに自動伝播する
- フロントエンドのビルドプロセス（npm, webpack, viteなど）は使用しない
- Java + Vanilla JavaScript + HTML + CSS で構成
- `template`タグによるDOMの直接書き換えで画面遷移（バーチャルDOM不使用）

## 技術スタック

| カテゴリ | 技術 |
|---|---|
| バックエンド | Java |
| フロントエンド | Vanilla JavaScript / HTML / CSS |
| ビルドツール | Maven |
| ORM | iciql |
| 単体テスト | JUnit |
| カバレッジ | JaCoCo |
| 静的解析 | Error Prone |
| フォーマッタ | Spotless |

## アーキテクチャ

```
Browser (HTML / CSS / Vanilla JS)
    └─ JSON fetch
        └─ ApiServlet         # リクエストルーティング・JSONハンドリング
            └─ API Layer      # リクエスト固有のロジック
                └─ Service Layer  # ビジネスロジック
                    └─ DAO / ORM (iciql)  # 永続化
```

バリデーション伝播:

```
JsServlet
    └─ DTO + Validator（アノテーション）
        └─ validator.js（サーバー起動時に自動生成）
            └─ ブラウザのlocalStorageにキャッシュ
```

## ディレクトリ構成

```
anko/
    src/
        main/
            java/          # Javaソースコード
            resources/     # 設定ファイル
        test/              # バックエンドテスト
            java/
            resources/
    www/                   # フロントエンド（HTML / CSS / JS）
        test/              # フロントエンドテスト
    database/              # ローカルデータベース
        sql/               # SQLスクリプト
    pom.xml
```

## Mavenコマンド

```bash
# サーバー起動
mvn exec:java "-Dexec.mainClass=com.uchicom.pj.Main"

# フォーマット適用
mvn spotless:apply

# テスト実行
mvn verify

# フルビルド（通常はこれを使う）
mvn spotless:apply clean compile verify
```

## コーディング規約

### Java

- フォーマットは Spotless に従う。手動でスタイルを変えない
- Error Prone の警告はエラーとして扱う。警告を残したままコミットしない
- DTOのバリデーションは `jakarta.validation.constraints` アノテーションで定義する
- `@Form("フォーム名")` アノテーションでDTOとフォームを紐付ける
- フィールドはpublicで定義する

```java
@Form("login")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginDto {
    @NotBlank(message = "必須です。")
    public String id;
}
```

### JavaScript / HTML / CSS

- フロントエンドビルドツール禁止（ES modules, TypeScript, バンドラー不可）
- バリデーションロジックをJSに手書きしない（validator.jsが自動生成するため）
- グローバルな状態管理ライブラリは使用しない

### ORM（iciql）

- DBアクセスはDAO層に閉じる
- JsServletからDBアクセスを行わない

## テスト方針

- 単体テストは JUnit で書く
- コードを追加・変更した場合は対応するテストを追加する
- フロントエンドテスト: `www/test/ut.htm`（単体）、`www/test/it.htm`（バックエンドとの結合）

## やってはいけないこと

- フロントエンドにバリデーションロジックを手書きする
- npm / webpack / vite などフロントエンドビルドツールを導入する
- React / Vue / Angular などのフロントエンドフレームワークを導入する
- DTOのバリデーション定義をDTO以外の場所に書く
- Error Proneの警告を無視する
- Spotlessのフォーマットを手動で変える
- DBアクセスをJsServletや上位レイヤーに書く

## H2データベース操作

```bash
java -cp ~/.m2/repository/com/h2database/h2/2.4.240/2.4.240.jar org.h2.tools.RunScript \
    -url "jdbc:h2:./database/pj;AUTO_COMPACT_FILL_RATE=0;CIPHER=AES" \
    -user pj \
    -password "pj pj" \
    -script ./database/sql/create.sql \
    -showResults
```
