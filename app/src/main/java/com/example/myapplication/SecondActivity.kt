package com.example.myapplication

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_second.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.concurrent.thread

class SecondActivity : AppCompatActivity() {

    fun signInSendPostRequest(id:String) {
        thread(start=true) {
            try{
                var reqParam = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
                val url = URL("http://172.30.1.3:3000/user/signIn")

                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "POST"

                val wr = OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(reqParam);
                wr.flush();
                wr.close()

                if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                    val streamReader = InputStreamReader(urlConnection.inputStream)
                    val buffered = BufferedReader(streamReader)

                    val content = StringBuilder()
                    while(true) {
                        val line = buffered.readLine() ?: break
                        content.append(line)
                        println(line)
                    }
                    // 스트림과 커넥션 해제
                    buffered.close()
                    urlConnection.disconnect()
                    runOnUiThread {
                        user_data.text = content.toString()
                    }
                }
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun withdrawalSendPostRequest(id:String) {
        thread(start=true) {
            try{
                var reqParam = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
                val url = URL("http://172.30.1.3:3000/user/withdrawal")

                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "POST"

                val wr = OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(reqParam);
                wr.flush();
                wr.close()

                if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                    val streamReader = InputStreamReader(urlConnection.inputStream)
                    val buffered = BufferedReader(streamReader)

                    val content = StringBuilder()
                    while(true) {
                        val line = buffered.readLine() ?: break
                        content.append(line)
                        println(line)
                    }
                    // 스트림과 커넥션 해제
                    buffered.close()
                    urlConnection.disconnect()
                    runOnUiThread {
                    }
                }
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        UserApiClient.instance.me { user, error ->
            id.text = "회원번호: ${user?.id}"
            nickname.text = "닉네임: ${user?.kakaoAccount?.profile?.nickname}"
            Glide.with(this)
                .load("${user?.kakaoAccount?.profile?.profileImageUrl}")
                .into(findViewById(R.id.profileimage_url))
            signInSendPostRequest("${user?.id}")
        }

        kakao_logout_button.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Toast.makeText(this, "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
            }
        }

        kakao_unlink_button.setOnClickListener {
            var user_id = "";
            UserApiClient.instance.me { user, error ->
                user_id = "${user?.id}"
            }
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Toast.makeText(this, "회원 탈퇴 실패 $error", Toast.LENGTH_SHORT).show()
                }else {
                    withdrawalSendPostRequest(user_id);
                    Toast.makeText(this, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
                }
            }
        }

    }
}
