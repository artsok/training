package adapter.test

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import artsok.github.io.androidacademyexample.R
import utils.faker.PersonFactory.Person


/**
 * Provide views to RecyclerView with data from dataSet.
 *
 * Initialize the dataset of the Adapter.
 *
 * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
 */
class PersonAdapter(private val items: List<Person>,private val context: Context) :
        RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {


    class PersonViewHolder(view: View) : ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val mPersonNameTextView: TextView = view.findViewById(R.id.personNameView)
        val mPersonSexTextView: TextView = view.findViewById(R.id.personAdressView)
        val mPersonAdressTextView: TextView = view.findViewById(R.id.personAdressView)
        val mPersonAgeTextView: TextView = view.findViewById(R.id.personAgeView)

        init {


            // Define click listener for the ViewHolder's View.
//            v.setOnClickListener { Log.d(TAG, "Element $adapterPosition clicked.") }
        }




    }


    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * Inflates the item views
     * Create new views (invoked by the layout manager)
     * В методе onCreateViewHolder() создаем новое представление
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        return PersonViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.list_item_person_recycler_view_test, parent, false))
    }

    /**
     * Binds each person in the ArrayList to a view
     * Replace the contents of a view (invoked by the layout manager)
     * В методе onBindViewHolder() заполняем представление данными
     **/
    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        Log.d(Adapter, "Element $position set.")

        val person = items[position];
        holder.mPersonNameTextView.text = person.name
        holder.mPersonAdressTextView.text = person.address
        holder.mPersonAgeTextView.text = person.age.toString()
        holder.mPersonSexTextView.text = if (person.sex) "Мужчина" else "Женщина"
    }


    companion object {
        private val Adapter = "PersonAdapter"
    }
}

