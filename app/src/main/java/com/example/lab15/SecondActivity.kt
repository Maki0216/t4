package com.example.lab15

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    private var items: ArrayList<String> = ArrayList()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var dbrw: MyDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        dbrw = MyDBHelper(this)

        items = intent.getStringArrayListExtra("ITEMS") ?: ArrayList()

        val listView = findViewById<ListView>(R.id.listView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, items)
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE

        findViewById<Button>(R.id.btnDeleteSelected).setOnClickListener {
            val position = listView.checkedItemPosition
            if (position != ListView.INVALID_POSITION) {
                val itemToRemove = items[position]
                val bookTitle = itemToRemove.substringAfter("大綱:").substringBefore("\t\t\t\t")
                try {
                    dbrw.writableDatabase.execSQL("DELETE FROM myTable WHERE book = ?", arrayOf(bookTitle))
                    items.removeAt(position)
                    adapter.notifyDataSetChanged()
                    listView.clearChoices()
                    showToast("已刪除項目: $bookTitle")
                } catch (e: Exception) {
                    showToast("刪除失敗: ${e.localizedMessage}")
                }
            } else {
                showToast("請先選擇要刪除的項目")
            }
        }

        findViewById<Button>(R.id.btnUpdateSelected).setOnClickListener {
            val position = listView.checkedItemPosition
            if (position != ListView.INVALID_POSITION) {
                val selectedItem = items[position]
                val bookTitle = selectedItem.substringAfter("大綱:").substringBefore("\t\t\t\t")
                val bookDetails = selectedItem.substringAfter("細節:")

                val intent = Intent(this, ThirdActivity::class.java)
                intent.putExtra("TITLE", bookTitle)
                intent.putExtra("DETAILS", bookDetails)

                startActivityForResult(intent, REQUEST_CODE_UPDATE)
            } else {
                showToast("請先選擇要修改的項目")
            }
        }

        findViewById<Button>(R.id.btnback).setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().putStringArrayListExtra("UPDATED_ITEMS", items))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE && resultCode == Activity.RESULT_OK) {
            val updatedTitle = data?.getStringExtra("UPDATED_TITLE") ?: return
            val updatedDetails = data.getStringExtra("UPDATED_DETAILS") ?: return

            try {
                dbrw.writableDatabase.execSQL(
                    "UPDATE myTable SET price = ? WHERE book = ?",
                    arrayOf(updatedDetails, updatedTitle)
                )
                val updatedItem = "大綱:$updatedTitle\t\t\t\t細節:$updatedDetails"
                val position = items.indexOfFirst { it.contains("大綱:$updatedTitle") }
                if (position >= 0) {
                    items[position] = updatedItem
                    adapter.notifyDataSetChanged()
                }
                showToast("已更新: $updatedTitle")
            } catch (e: Exception) {
                showToast("更新失敗: ${e.localizedMessage}")
            }
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val REQUEST_CODE_UPDATE = 1
    }
}
