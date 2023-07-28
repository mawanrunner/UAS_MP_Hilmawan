package com.uas_project.uas_alfares_2107411020.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.uas_project.uas_alfares_2107411020.DBHelper
import com.uas_project.uas_alfares_2107411020.R

class SignUpActivity : AppCompatActivity() {
    private lateinit var uname: EditText
    private lateinit var pword: EditText
    private lateinit var cword: EditText
    private lateinit var dbhelper: DBHelper
    private lateinit var btnSignUp: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        uname = findViewById(R.id.usernameDaftar)
        pword = findViewById(R.id.passwordDaftar)
        cword = findViewById(R.id.confirmPassword)
        btnSignUp = findViewById(R.id.btnDaftar)
        dbhelper = DBHelper(this)

        btnSignUp.setOnClickListener {
            val unameText = uname.text.toString()
            val pwordText = pword.text.toString()
            val cwordText = cword.text.toString()
            if (TextUtils.isEmpty(unameText) || TextUtils.isEmpty(pwordText) || TextUtils.isEmpty(cwordText)){
                Toast.makeText(this, "Fill form correctly!", Toast.LENGTH_SHORT).show()
            }else{
                if (pwordText == cwordText){
                    val userExist = dbhelper.checkUserPass(unameText, pwordText)
                    if (!userExist){
                        val saveData = dbhelper.insertData(unameText, pwordText)
                        Toast.makeText(this, "Signup Successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this, "User exists!", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "Confirm password doesn't match!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}