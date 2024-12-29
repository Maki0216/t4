package com.example.lab15

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var items: ArrayList<String> = ArrayList()
    private lateinit var dbrw: SQLiteDatabase
    private lateinit var secondActivityLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbrw = MyDBHelper(this).writableDatabase

        secondActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                // Refresh database data
                val c = dbrw.rawQuery("SELECT * FROM myTable", null)
                items.clear()
                while (c.moveToNext()) {
                    items.add("大綱:${c.getString(0)}\t\t\t\t細節:${c.getString(1)}")
                }
                c.close()
                showToast("數據已同步更新")
            }
        }

        findViewById<Button>(R.id.btnInsert).setOnClickListener {
            val edBook = findViewById<EditText>(R.id.edBook)
            val edPrice = findViewById<EditText>(R.id.editTextMultiLine)

            if (edBook.text.isEmpty() || edPrice.text.isEmpty()) {
                showToast("欄位請勿留空")
            } else {
                try {
                    dbrw.execSQL(
                        "INSERT INTO myTable(book, price) VALUES(?, ?)",
                        arrayOf(edBook.text.toString(), edPrice.text.toString())
                    )
                    showToast("新增成功: ${edBook.text}, ${edPrice.text}")
                    edBook.text.clear()
                    edPrice.text.clear()
                } catch (e: Exception) {
                    showToast("新增失敗: ${e.localizedMessage}")
                }
            }
        }



        findViewById<Button>(R.id.btnSendToSecondActivity).setOnClickListener {
            val edBook = findViewById<EditText>(R.id.edBook)
            val queryString = if (edBook.text.isEmpty())
                "SELECT * FROM myTable"
            else
                "SELECT * FROM myTable WHERE book LIKE ?"

            try {
                items.clear()
                val c = if (edBook.text.isEmpty())
                    dbrw.rawQuery(queryString, null)
                else
                    dbrw.rawQuery(queryString, arrayOf("%${edBook.text}%"))

                while (c.moveToNext()) {
                    items.add("大綱:${c.getString(0)}\t\t\t\t細節:${c.getString(1)}")
                }
                c.close()

                val intent = Intent(this, SecondActivity::class.java)
                intent.putStringArrayListExtra("ITEMS", items)
                secondActivityLauncher.launch(intent)

            } catch (e: Exception) {
                showToast("查詢失敗: ${e.localizedMessage}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::dbrw.isInitialized) {
            dbrw.close()
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
