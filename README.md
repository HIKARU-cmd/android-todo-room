
# Android ToDo (Firestore × MVVM)

シンプルなToDo管理アプリです。  
データ永続化を Room(ローカル)からFirebase Firestore(クラウド)へ移行しました。 アーキテクチャは MVVM をベースに実装しました。  
基本的なCRUD処理（追加・編集・閲覧・削除）に加え、完了フラグや作成日時、期限日時の表示機能を備えています。
--------------------------------------------------
## Demo
- デモ動画（下記リンクよりダウンロードお願いいたします。） 
https://drive.google.com/file/d/1Ci7pl1dAmvIoQqAGwXd2XVY-8VlqarHM/view?usp=drive_link

## Features
- ToDoの追加・編集・削除
- 完了状態の切り替え（チェックボックス、取り消し線）
- 作成日時の自動保存、表示
- 期限の設定・表示
  - 期限切れ：赤
  - 今日が期限：オレンジ
  - 完了済み：グレー
  - 未設定/未来：黒
- データのクラウド保存 (Firebase Firestore)：複数端末で同期可能

## Tech Stack
- 言語: Kotlin
- アーキテクチャ: MVVM (ViewModel / Repository)
- データベース: Firebase Firestore（cloud)
- UI: AndroidX / Material Components / ListView + AlertDialog
- 非同期処理: Coroutine + Flow
  
## Project Structure
```
android-todo-room/
┣ todoapp/
┃ ┣ data/
┃ ┃ ┗ FirestoreRepository.kt #Firestoreとのやり取り
┃ ┣ ui/ -> ViewModel
┃ ┃ ┗ TaskViewModel.kt #Repositoryを利用してUI用の状態を公開
┃ ┣ MainActivity.kt #メイン画面 (ListView + 入力UI)
┃ ┣ TaskAdapter.kt #ListView 用アダプター
```

## Build
- 開発環境: Android Studio Koala 以降推奨
- minSdkVersion: 21
- targetSdkVersion: 34

## Design & Thought
- MVVM採用 → UIとロジックを分離し、保守性を高めています
- Ripositoryパターン：Firestoreへのアクセスを集約
- クラウド永続化：端末依存を排除し、複数端末でのデータ同期が容易
- UX向上のため、期限の状態を色分け表示  
  → ユーザーが直感的にタスクの優先度を把握できるよう工夫しています
  
## 今後の改善予定
- ソート機能追加（作成日時、期限など）
- エラーハンドリングの充実（入力エラー、DB保存エラーなど）
- 入力バリデーション追加（文字数、空文字など）
- ChatGPT APIを活用し、タスクのカテゴリ分け
- UI、UX向上のため、遷移画面の追加
  
## License
MIT
