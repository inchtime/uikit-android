package io.inchtime.uikit.example

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        http://oss.beefintech.net/LOGO/COMPANY/%E5%8F%8B%E9%82%A6%403x.png

        GlideApp.with(this)
            .load("https://oss.beefintech.net/LOGO/COMPANY/%E5%8F%8B%E9%82%A6%403x.png")
            .placeholder(R.drawable.recyclerkit)
            .into(image)

    }
}
