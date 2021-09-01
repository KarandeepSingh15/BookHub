package com.karandeepsingh.bookhub.activities

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.karandeepsingh.bookhub.R
import com.karandeepsingh.bookhub.database.BookDatabase
import com.karandeepsingh.bookhub.database.BookEntity
import com.karandeepsingh.bookhub.fragments.FavouritesFragment
import com.karandeepsingh.bookhub.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
import java.lang.Exception

class DescriptionActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var imgBookImage: ImageView
    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var txtDescription: TextView
    lateinit var btnAddToFavourites: Button
    lateinit var rlProgressLayout: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)
        toolbar = findViewById(R.id.toolbar)
        imgBookImage = findViewById(R.id.imgBookImage)
        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        txtDescription = findViewById(R.id.txtDescription)
        btnAddToFavourites = findViewById(R.id.btnAddToFavourites)
        rlProgressLayout = findViewById(R.id.rlProgressLayout)
        rlProgressLayout.visibility = View.VISIBLE
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"

        var book_id: String? = null

        if (intent != null) {
            book_id = intent.getStringExtra("book_id")
        } else {
            Toast.makeText(
                this@DescriptionActivity,
                "Some Unexpected Error Occurred!!",
                Toast.LENGTH_LONG
            ).show()
        }
        if (book_id == null) {
            Toast.makeText(
                this@DescriptionActivity,
                "Some Unexpected Error Occurred!!",
                Toast.LENGTH_LONG
            ).show()
        }

        if (ConnectionManager().checkConnectivity(this@DescriptionActivity)) {
            rlProgressLayout.visibility = View.GONE
            val request = Volley.newRequestQueue(this@DescriptionActivity)
            val url = "http://13.235.250.119/v1/book/get_book/"
            val jsonParams = JSONObject()
            jsonParams.put("book_id", book_id)
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                Response.Listener {
                    try {
                        val success = it.getBoolean("success")
                        if (success) {
                            val bookObject = it.getJSONObject("book_data")
                            val image=bookObject.getString("image")
                            Picasso.get().load(bookObject.getString("image"))
                                .error(R.drawable.default_book_cover).into(imgBookImage)
                            txtBookName.text = bookObject.getString("name")
                            txtBookAuthor.text = bookObject.getString("author")
                            txtBookPrice.text = bookObject.getString("price")
                            txtBookRating.text = bookObject.getString("rating")
                            txtDescription.text = bookObject.getString("description")
                            val bookEntity=BookEntity(
                                book_id?.toInt() as Int,
                                txtBookName.text.toString(),
                                txtBookAuthor.text.toString(),
                                txtBookPrice.text.toString(),
                                txtBookRating.text.toString(),
                                txtDescription.text.toString(),
                                image
                            )
                            val checkFav=DBAsyncTask(applicationContext,bookEntity,1).execute()
                            val isFav=checkFav.get()
                            if(isFav)
                            {
                                btnAddToFavourites.text="Remove From Favourites"
                                val color=ContextCompat.getColor(applicationContext,R.color.red)
                                btnAddToFavourites.setBackgroundColor(color)
                            }
                            else
                            {
                                btnAddToFavourites.text=getString(R.string.add_to_favourites)
                                val color=ContextCompat.getColor(applicationContext,R.color.primaryColor)
                                btnAddToFavourites.setBackgroundColor(color)
                            }
                            btnAddToFavourites.setOnClickListener {
                                if(!DBAsyncTask(applicationContext,bookEntity,1).execute().get())
                                {
                                    val async=DBAsyncTask(applicationContext,bookEntity,2).execute()
                                    val result=async.get()
                                    if(result)
                                    {
                                        Toast.makeText(this@DescriptionActivity,"Added to Favourites",Toast.LENGTH_SHORT).show()
                                        btnAddToFavourites.text="Remove From Favourites"
                                        val color=ContextCompat.getColor(applicationContext,R.color.red)
                                        btnAddToFavourites.setBackgroundColor(color)
                                    }
                                    else
                                    {
                                        Toast.makeText(this@DescriptionActivity,"Some Error Occurred",Toast.LENGTH_LONG).show()
                                    }
                                }
                                else{
                                    val async=DBAsyncTask(applicationContext,bookEntity,3).execute()
                                    val result=async.get()
                                    if(result)
                                    {
                                        Toast.makeText(this@DescriptionActivity,"Removed from Favourites",Toast.LENGTH_LONG).show()
                                        btnAddToFavourites.text=getString(R.string.add_to_favourites)
                                        val color=ContextCompat.getColor(applicationContext,R.color.primaryColor)
                                        btnAddToFavourites.setBackgroundColor(color)
                                    }
                                    else
                                    {
                                        Toast.makeText(this@DescriptionActivity,"Some Error Occurred",Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                this@DescriptionActivity,
                                "Error in Receiving Data!!",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@DescriptionActivity,
                            "Some Unexpected Error Occurred!!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                Response.ErrorListener {

                    Toast.makeText(
                        this@DescriptionActivity,
                        "Volley Error($it) Occurred",
                        Toast.LENGTH_LONG
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["Token"] = "4996a5e2b7be6f"
                    return headers
                }
            }
            request.add(jsonObjectRequest)

        } else {
            val alertDialog = AlertDialog.Builder(this@DescriptionActivity)
            alertDialog.setTitle("Failure")
            alertDialog.setMessage("Internet Connection Not Found")
            alertDialog.setPositiveButton("Open Settings") { text, Listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            alertDialog.setNegativeButton("Exit") { text, Listener ->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            alertDialog.create()
            alertDialog.show()
        }
    }

    class DBAsyncTask(val context: Context, val bookEntity: BookEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            /*
            mode 1 -> Check whether bookEntity is added to database
            mode 2-> Add bookEntity to database
            mode 3-> Remove bookEntity from database
             */
            when (mode) {
                1 -> {
                    val book: BookEntity? = db.bookDao().getBookById(bookEntity.bookId.toString())
                    db.close()
                    return book != null
                }
                2 -> {
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }
                3-> {
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }

}