package com.minnu.kotlinetuts.activities

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.Toast
import com.minnu.kotlinetuts.R
import com.minnu.kotlinetuts.adapter.TasksAdapter
import com.minnu.kotlinetuts.constant.Constants.Companion.index
import com.minnu.kotlinetuts.database.DatabaseHandler
import com.minnu.kotlinetuts.listeners.Completion
import com.minnu.kotlinetuts.listeners.Listener
import com.minnu.kotlinetuts.log.L.Companion.toast
import com.minnu.kotlinetuts.manager.NotificationManager
import com.minnu.kotlinetuts.manager.TimeManager.Companion.add
import com.minnu.kotlinetuts.model.Tasks
import com.minnu.kotlinetuts.model.Tasks.Companion.arrayList
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list.*

class MainActivity : AppCompatActivity() {

    private var db = DatabaseHandler(this)
    var emptyView: LinearLayout? = null

    companion object {

        var linearLayout: LinearLayout? = null

        var context: Context? = null

        fun makeVisible() {
            linearLayout!!.visibility = VISIBLE
        }

        fun makeInvisible() {
            linearLayout!!.visibility = GONE
        }
    }

    fun ifListEmpty() {
        if (arrayList.size > 0) {
            emptyView!!.visibility = GONE
        } else {
            emptyView!!.visibility = VISIBLE
        }
    }

    private var adapt: TasksAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        setContentView(R.layout.activity_main)

        linearLayout = findViewById(R.id.emptyView)

        initialize()
//        val notification = NotificationManager(this)
//        NotificationManager.
        fetchDataFromSQLite()
        fab.setOnClickListener {
            add(this@MainActivity, "Schedule", Tasks(), db, object : Completion {
                override fun onComplete(index: Int) {
                    if (index != -1)
                        fetchDataFromSQLite()
                    else
                        toast(context!!, "Error")
                }
            })
        }

/*
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                toast(context as MainActivity, "${viewHolder.adapterPosition}")

//                val task: Tasks = arrayList[viewHolder.adapterPosition]
//                deleteDataFromSQLite(task)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(rv)
*/
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_delete -> {
                val db = DatabaseHandler(this)
                if (arrayList.size > 0) {
                    db.deleteAllTaskFromSQLiteDb(object : Completion {
                        override fun onComplete(index: Int) {
                            if (index > 0) {
                                Toast.makeText(context, "All Deleted Successful", Toast.LENGTH_SHORT).show()
                                arrayList.clear()
                                adapt!!.notifyDataSetChanged()
                                emptyView!!.visibility = VISIBLE
                            } else
                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(context, "List is already empty", Toast.LENGTH_SHORT).show()
                }
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun initialize() {
        emptyView = findViewById(R.id.emptyView)
        // Creates a vertical Layout Manager
        rv.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchDataFromSQLite() {
        arrayList!!.clear()
        ifListEmpty()
        db.readData(object : Listener {
            override fun onComplete(tasks: Tasks?, pos: Int) {
                if (pos > 0) {
                    arrayList.add(tasks!!)
                    index = arrayList!!.size

                    ifListEmpty()
                    adapt = TasksAdapter(this@MainActivity, arrayList!!)
                    // Access the RecyclerView Adapter and load the data into it
                    rv.adapter = adapt
                    adapt!!.notifyDataSetChanged()
                }
            }
        })
    }
}