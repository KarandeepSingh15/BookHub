package com.karandeepsingh.bookhub.fragments

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Database
import androidx.room.Room
import com.karandeepsingh.bookhub.R
import com.karandeepsingh.bookhub.adapter.FavouritesRecyclerAdapter
import com.karandeepsingh.bookhub.database.BookDatabase
import com.karandeepsingh.bookhub.database.BookEntity


class FavouritesFragment : Fragment() {
 lateinit var favRecycler:RecyclerView
 lateinit var favRecyclerLayoutManager:RecyclerView.LayoutManager
 lateinit var favouritesRecyclerAdapter:FavouritesRecyclerAdapter
 lateinit var rlProgressLayout:RelativeLayout
 var bookList= listOf<BookEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_favourites, container, false)
        favRecycler=view.findViewById(R.id.favRecycler)
        rlProgressLayout=view.findViewById(R.id.rlProgressLayout)
        rlProgressLayout.visibility=View.VISIBLE
        favRecyclerLayoutManager=GridLayoutManager(activity as Context,2)

        return view

    }

class FavouriteBooks(val context: Context):AsyncTask<Void,Void,List<BookEntity>>()
{   val db=Room.databaseBuilder(context, BookDatabase::class.java,"books-db").build()
    override fun doInBackground(vararg params: Void?): List<BookEntity> {
        return db.bookDao().getAllBooks()
    }

}

    override fun onResume() {
        bookList=FavouriteBooks(activity as Context).execute().get()
        if(activity!=null)
        {
            rlProgressLayout.visibility=View.GONE
            favouritesRecyclerAdapter=FavouritesRecyclerAdapter(activity as Context,bookList)
            favRecycler.layoutManager=favRecyclerLayoutManager
            favRecycler.adapter=favouritesRecyclerAdapter
        }
        super.onResume()
    }




}