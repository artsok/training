package artsok.github.io.androidacademyexample

import adapter.test.PersonAdapter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main_recyclerview_test.*
import utils.faker.PersonFactory

class MainActivity_RecyclerView_Test : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_recyclerview_test)

        val dataSet = PersonFactory.generatePerson()

        rv_person_list.layoutManager = LinearLayoutManager(this)
        rv_person_list.adapter = PersonAdapter(dataSet, this)





    }
}