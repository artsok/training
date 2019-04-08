package artsok.github.io.androidacademyexample

import android.graphics.Color.*
import android.graphics.Typeface.DEFAULT_BOLD
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import artsok.github.io.androidacademyexample.R.drawable.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val talkTags = resources.getStringArray(R.array.talk_tags_names)
        addTags(talkTags)

    }

    private fun addTags(tags: Array<String>) {
        val layout = findViewById<LinearLayout>(R.id.tags_name)
        for (tag in tags) {
            val btn = TextView(this)
            btn.text = tag
            btn.setBackgroundResource(textview_border_tags)

            val params = LinearLayout.LayoutParams(
                    WRAP_CONTENT,
                    WRAP_CONTENT
            )
            params.setMargins(0, 0, 10, 0)

            btn.layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            btn.setTextColor(parseColor("#ffffff"))
            btn.typeface = DEFAULT_BOLD
            btn.setPadding(36, 36, 36, 36)
            btn.layoutParams = params
            layout.addView(btn, 0)
        }
    }
}

