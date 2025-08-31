package com.example.todoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// AppDatabase.ktはRoomDB本体の定義、Roomデータベースの「本体」を定義するクラス.
// どのエンティティを使うか、どのDAOを使うかなど記述
@Database(                                                      // Roomのアノテーション、メタ情報のこと
    entities = [Task::class],                                   // RoomDBで扱うテーブル(tasks)に対応するdata classを指定
    version = 2,
    exportSchema = false                                        // Roomがスキーマ情報(テーブル定義や構造)をJSON形式で出力するか決めるもの　falseはスキーマ情報は保存されない
)

abstract class AppDatabase : RoomDatabase() {                   // abstract class：抽象クラスのこと、直接インスタンス化はできず、Room が自動で実装クラスを作ってくれるようなイメージ、
    abstract fun taskDao(): TaskDao                             // taskDao()：DAO（Data Access Object）を取得するための関数    タスク用のDAOをDBから取り出す窓口のようなイメージ　DAOはDBにアクセスする専用の窓口のイメージ

    companion object {                                          // 特別なオブジェクト　この中に書いた変数はクラス名から直接呼べる　インスタンス化せずに関数を呼び出せる（AppDatabaseクラスではアプリ全体で一つの共有DBインスタンス（シングルトン）を使用しているため）
        @Volatile private var INSTANCE: AppDatabase? = null     // @Volatileは変数にアクセスする際に複数のスレッド(処理)から同時にアクセスしても問題が起きないようにメモリ管理してくれる　　？はnullを許容

        private val MIGRATION_1_2 = object : Migration(1,2) {   // vertion1から2に移行するための処理　RoomがDBを起動したときに、ユーザーのDBがまだversion=1だったら、このMIGRATION_1_2が呼ばれる。
            override fun migrate(db: SupportSQLiteDatabase) {   // override fun migrateでDBをどう更新するのか記述
                db.execSQL("ALTER TABLE tasks ADD COLUMN dueAt INTEGER")    // 新しくタスクテーブルに「dueAt」というカラムを追加　既存レコードにはnullが入る
            }
        }

        fun get(context: Context): AppDatabase =                // contextはAndroid で使われる特別なオブジェクトで、アプリの情報（ファイル操作、DB接続、画面遷移など）を操作するのに必要
            INSTANCE ?: synchronized(this) {               // 「エルビス演算子」の条件分岐　INSTANCEがnull出ない場合、synchronized(this){...}中の処理を行う（インタンスを作成する）　synchronized(this){} は複数の処理が入った場合、インスタンスが２個作成される恐れがある、それを防ぐための仕組み
                Room.databaseBuilder(                           // RoomライブラリでDBインスタンスの作成をする準備　.build()で作成する。　※RoomとはSQLiteという組み込みデータベースの使いやすくしたライブラリ
                    context.applicationContext,                 // アプリ全体の共通Contextを取得している　共通Contextとは、アプリ起動から停止まで使用できる、共通の情報アクセス元のイメージ
                    AppDatabase::class.java,                    // Roomに対して「AppDatabase クラスを使って、DBを作製して」と指示している。　AppDatabase::は定義した抽象クラス
                    "todo.db"                             // DBのファイル名を指定している
                )
                .addMigrations(MIGRATION_1_2)
                .build().also { INSTANCE = it }                // Roomのデータベースビルダーからインスタンスを生成して、INSTANCE に代入している
            }
    }
}