package com.example.lab15

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ThirdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        val edBookUpdate = findViewById<EditText>(R.id.edBookUpdate)
        val edDetailsUpdate = findViewById<EditText>(R.id.edDetailsUpdate)

        val currentTitle = intent.getStringExtra("TITLE") ?: ""
        val currentDetails = intent.getStringExtra("DETAILS") ?: ""
        edBookUpdate.setText(currentTitle)
        edDetailsUpdate.setText(currentDetails)

        findViewById<Button>(R.id.btnSaveUpdate).setOnClickListener {
            val updatedTitle = edBookUpdate.text.toString()
            val updatedDetails = edDetailsUpdate.text.toString()

            if (updatedTitle.isNotEmpty() && updatedDetails.isNotEmpty()) {
                val resultIntent = intent
                resultIntent.putExtra("UPDATED_TITLE", updatedTitle)
                resultIntent.putExtra("UPDATED_DETAILS", updatedDetails)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                showToast("標題和內容不能為空")
            }
        }

        findViewById<Button>(R.id.btnCancelUpdate).setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
