# anko.js
Light and small framework for javascript and Java.

A tracking application based on Anko.

## 共通機能を使う場合
- anko.js

## 入力チェック機能を使う場合
- anko.js
- anko-validator.js

## it.htmを利用する場合
- anko.js
- anko-it.js

## ut.htmを利用する場合
- anko.js
- anko-ut.js
- anko-mock.js mock化する場合はanko.jsでpostする必要がある

## mvn
### サーバ起動
```
mvn exec:java "-Dexec.mainClass=com.uchicom.tracker.Main"
```

### フォーマッタ
```
mvn spotless:apply
```

### 全体テスト実行
```
mvn verify
```

#### ファイル単体でテスト実行
```
mvn test "-Dtest=com.uchicom.tracker.dao.AccountDaoTest"
```

### フォーマッタ & 全テスト実行
```
mvn spotless:apply verify
```

### フォーマッタ & クリア & 全テスト実行
```
mvn spotless:apply clean compile verify
```

## SQLスクリプト
```
java -cp ~/.m2/repository/com/h2database/h2/2.2.224/h2-2.2.224.jar org.h2.tools.RunScript -url "jdbc:h2:./database/tracker;AUTO_COMPACT_FILL_RATE=0;CIPHER=AES" -user tracker -password "tracker tracker" -script ./database/sql/create.sql  -showResults
```
