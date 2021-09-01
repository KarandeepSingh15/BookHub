package com.karandeepsingh.bookhub.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.karandeepsingh.bookhub.APIKey
import com.karandeepsingh.bookhub.R
import com.karandeepsingh.bookhub.adapter.DashboardRecyclerAdapter
import com.karandeepsingh.bookhub.models.Book
import com.karandeepsingh.bookhub.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class DashboardFragment : Fragment() {
    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var dashboardAdapter: DashboardRecyclerAdapter
    lateinit var rlProgressLayout:RelativeLayout
    lateinit var progressBar :ProgressBar
    val ratingsComparator= Comparator<Book>{book1,book2->
        if(book1.bookRating.compareTo(book2.bookRating,true)==0)
        {
            book1.bookName.compareTo(book2.bookName,true)
        }
        else
        {
            book1.bookRating.compareTo(book2.bookRating,true)
        }

    }

    val bookList = arrayListOf<Book>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)
        layoutManager = LinearLayoutManager(activity)
        rlProgressLayout=view.findViewById(R.id.rlProgressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        rlProgressLayout.visibility=View.VISIBLE

        if (ConnectionManager().checkConnectivity(activity as Context)) {

                rlProgressLayout.visibility=View.GONE
                val queue = Volley.newRequestQueue(activity as Context)
                val url = "http://13.235.250.119/v1/book/fetch_books/"
                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                    Response.Listener {

                        //Code when response is receive
                        try {
                            val success = it.getBoolean("success")
                            if (success) {
                                val data = it.getJSONArray("data")
                                for (i in 0 until data.length()) {
                                    val bookJsonObject = data.getJSONObject(i)
                                    val book = Book(
                                        bookJsonObject.getString("book_id"),
                                        bookJsonObject.getString("name"),
                                        bookJsonObject.getString("author"),
                                        bookJsonObject.getString("rating"),
                                        bookJsonObject.getString("price"),
                                        bookJsonObject.getString("image")
                                    )
                                    bookList.add(book)
                                }
                                dashboardAdapter =
                                    DashboardRecyclerAdapter(activity as Context, bookList)
                                recyclerDashboard.adapter = dashboardAdapter
                                recyclerDashboard.layoutManager = layoutManager

                            } else {
                                Toast.makeText(activity, "Error in receiving data", Toast.LENGTH_LONG)
                                    .show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(activity, "Some Unexpected ERROR Occurred!!", Toast.LENGTH_LONG)
                                .show()
                        }


                    },
                    Response.ErrorListener {
                        //Code when error occurs
                        if(activity!=null)
                        {
                            Toast.makeText(activity, "Volley Error Occurred($it)", Toast.LENGTH_LONG)
                                .show()

                        }

                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        val apikey=APIKey()
                        headers["Token"] = apikey.token
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)

        } else {
            val alertDialog = AlertDialog.Builder(activity)
            alertDialog.setTitle("Failure")
            alertDialog.setMessage("Internet Connection Not Found")
            alertDialog.setPositiveButton("Open Settings") { text, Listener ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            alertDialog.setNegativeButton("Exit") { text, Listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            alertDialog.create()
            alertDialog.show()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_options,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==R.id.SortRating)
        {
            Collections.sort(bookList,ratingsComparator)
            dashboardAdapter.notifyDataSetChanged()
            bookList.reverse()
        }
        return super.onOptionsItemSelected(item)
    }

}