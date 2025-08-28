====================
Android ToDo (Room × MVVM)
シンプルなToDo管理アプリです。  
ローカル永続化に Room を採用し、アーキテクチャは MVVM をベースに実装しました。  
基本的なCRUD処理（追加・閲覧・削除）に加え、完了フラグや作成日時の表示機能を備えています。
--------------------------------------------------
[ Demo ]
・デモ動画（下記リンクよりダウンロードお願いいたします。）
https://drive.google.com/file/d/1K7t6wJ-njLBrYRPCJhEWlf54ICfS4ddw/view?usp=drive_link
--------------------------------------------------
[ Features ]
・ToDoの追加・削除
・完了状態の切り替え（チェックボックス、取り消し線）
・作成日時の自動保存、表示
・データのローカル永続化(ROOM DB使用)
--------------------------------------------------
[ Tech Stack ]
・言語: Kotlin
・アーキテクチャ: MVVM (ViewModel / Dao)
・データベース: Room (SQLite)
・UI: AndroidX / Material Components / ListView + AlertDialog
・非同期処理: Coroutine + Flow
--------------------------------------------------
[ Project Structure ]
android-todo-room/
┣ todoapp/
┃ ┣ data/ -> DB関連 (Entity / Dao / Database)
┃ ┃ ┣ AppDatabase.kt
┃ ┃ ┣ Task.kt
┃ ┃ ┗ TaskDao.kt
┃ ┣ ui/ -> ViewModel
┃ ┃ ┗ TaskViewModel.kt
┃ ┣ MainActivity.kt -> メイン画面 (ListView + 入力UI)
┃ ┣ TaskAdapter.kt -> ListView 用アダプター
--------------------------------------------------
[ Build ]
・開発環境: Android Studio Koala 以降推奨
・minSdkVersion: 21
・targetSdkVersion: 34
--------------------------------------------------
[ Design & Thought ]
・MVVM採用 → UIとロジックを分離し、保守性を高めています
・Room導入 → データをローカルに永続化し、シンプルなDB操作を実現
・責務分離 → Entity/Dao/ViewModelの役割を明確化
--------------------------------------------------
[ 今後の改善予定 ]
・編集機能追加、期限の入力欄追加
・ソート機能追加（作成日時、期限など）
・エラーハンドリングの充実（入力エラー、DB保存エラーなど）
・入力バリデーション追加（文字数、空文字など）
・ChatGPT APIを活用し、タスクのカテゴリ分け
--------------------------------------------------
[ License ]
・MIT
--------------------------------------------------
