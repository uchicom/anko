# AGENTS.md — anko プロジェクト AI開発ルール

このファイルはClaude、ChatGPT、Gemini、GitHub Copilotなど全AIツールの共通ルールです。
コードの生成・編集・レビューを行う際は必ずこのファイルの内容に従ってください。

---

## プロジェクト概要

**anko** はビジネス向けWebアプリケーションのためのBuildlessフレームワークです。

- バックエンドのDTOをSingle Source of Truthとして、バリデーションルールをフロントエンドに自動伝播します
- フロントエンドのビルドプロセス（npm, webpack, viteなど）は使用しません
- Java + Vanilla JavaScript + HTML + CSS で構成されています
- バーチャルDOMは使用せず、`template`タグによるDOMの直接書き換えにより画面遷移をします

---

## 技術スタック

| カテゴリ | 技術 |
|---|---|
| バックエンド言語 | Java |
| フロントエンド | Vanilla JavaScript / HTML / CSS |
| ビルドツール | Maven |
| ORM | iciql |
| 単体テスト | JUnit |
| カバレッジ | JaCoCo |
| 静的解析 | Error Prone |
| フォーマッタ | Spotless |

---

## アーキテクチャ

### レイヤー構成

```
Browser (HTML / CSS / Vanilla JS)
	└─ JSON fetch
		└─ ApiServlet         # リクエストルーティング・JSONハンドリング
			└─ API Layer      # リクエスト固有のロジック
				└─ Service Layer  # ビジネスロジック
					└─ DAO / ORM (iciql)  # 永続化
```

### バリデーション伝播

```
JsServlet
	└─ DTO + Validator（アノテーション）
		└─ validator.js（サーバー起動時に自動生成）
			└─ ブラウザのlocalStorageにキャッシュ
```

### 重要な設計方針

- バリデーションルールはDTOのアノテーションにのみ定義する（二重実装禁止）
- フロントエンドにバリデーションロジックを手書きしない
- フロントエンドビルド不要 — React/Vue/Angular/npm/webpackは使用しない
- DAO/ORMはAPIリクエスト処理のみで使用し、validator.js生成時はDBアクセスしない

---

## ディレクトリ構成

```
anko/
	src/
		main/
			java/          # Javaソースコード
			resources/     # 設定ファイル
		test/            # バックエンドテスト
			java/          # Javaテストソースコード
			resources/     # Javaテスト設定ファイル
	www/               # フロントエンド（HTML / CSS / JS）
		test/            # フロントエンドテスト
	database/          # ローカルデータベース
		sql/             # SQLスクリプト
	pom.xml            # Mavenビルド設定
```

---

## コーディング規約

### Java

- フォーマットは **Spotless** に従う。手動でスタイルを変えない
- **Error Prone** の警告はエラーとして扱う。警告を残したままコミットしない
- DTOのバリデーションは `jakarta.validation.constraints` アノテーションで定義する
- `@Form("フォーム名")` アノテーションでDTOとフォームを紐付ける
- フィールドはpublicで定義する（DTOの慣習に従う）

```java
// Good
@Form("login")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginDto {
	@NotBlank(message = "必須です。")
	public String id;
}
```

- クロスフィールドバリデーションはスコープ外（設計上サポートしない）
- 条件付き必須フィールドはスコープ外

### JavaScript / HTML / CSS

- フロントエンドビルドツールは使用しない（ES modules, TypeScript, バンドラー禁止）
- バリデーションロジックをJSに手書きしない（validator.jsが自動生成するため）
- UIの更新はコンテンツの差し替えで行う
- グローバルな状態管理ライブラリは使用しない
- 画面はステートレスとして扱い、状態はサーバー側で管理する

### ORM（iciql）

- DBアクセスはDAO層に閉じる
- validator.js生成処理（JsServlet）からDBアクセスを行わない

---

## Mavenコマンド

```bash
# サーバー起動
mvn exec:java "-Dexec.mainClass=com.uchicom.pj.Main"

# フォーマット適用
mvn spotless:apply

# テスト実行（JUnit + JaCoCo + Error Prone）
mvn verify

# フォーマット → クリーン → コンパイル → テスト（通常のフルビルド）
mvn spotless:apply clean compile verify
```

---

## テスト方針

- 単体テストは **JUnit** で書く
- テストカバレッジは **JaCoCo** で計測する
- コードを追加・変更した場合は対応するテストを追加する
- テストなしのコードを本番コードに含めない
- フロントエンドのテストは`www/test/ut.htm`(単体テスト),`www/test/it.htm`(バックエンドとの結合テスト)で作成する

---

## やってはいけないこと

- フロントエンドにバリデーションロジックを手書きする（validator.jsで自動生成されるため）
- npm / webpack / vite などフロントエンドビルドツールを導入する
- React / Vue / Angular などのフロントエンドフレームワークを導入する
- DTOのバリデーション定義をDTO以外の場所に書く
- Error Proneの警告を無視する
- Spotlessのフォーマットを手動で変える
- DBアクセスをJsServletや上位レイヤーに書く
- クロスフィールドバリデーションをフレームワーク内で実装する（設計上サポートしない）

---

## H2データベース操作

```bash
# SQLスクリプト実行
java -cp ~/.m2/repository/com/h2database/h2/2.4.240/2.4.240.jar org.h2.tools.RunScript \
	-url "jdbc:h2:./database/pj;AUTO_COMPACT_FILL_RATE=0;CIPHER=AES" \
	-user pj \
	-password "pj pj" \
	-script ./database/sql/create.sql \
	-showResults
```

---

## 開発環境

- **GitHub Codespaces** での動作が推奨（`.devcontainer` 設定済み）
- Java / Maven / H2 データベースがプリインストール済み
- ローカル環境でも同様の手順でセットアップ可能

---
