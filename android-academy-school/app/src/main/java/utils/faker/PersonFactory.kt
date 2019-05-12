package utils.faker

import android.util.Log

class PersonFactory {

    companion object {
        private var mPersonList = ArrayList<Person>(100)

        fun generatePerson():List<Person> {
            Log.d("PersonFactory", "How much element Element ${mPersonList.size}")
            if(mPersonList.size > 0) {
                return mPersonList
            }

            for (i in 0..99) {
                if (i % 2 == 0) {
                    mPersonList.add(Person("Иванов Иван клон# $i", 25, "Москва", true))
                } else {
                    mPersonList.add(Person("Петрова Мария клон# $i", 33, "Санкт-Петербург", false))
                }
            }
            return mPersonList
        }
    }

    data class Person(val name:String, val age:Int, val address:String, val sex:Boolean)


}