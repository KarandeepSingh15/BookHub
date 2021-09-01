package com.karandeepsingh.bookhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.karandeepsingh.bookhub.R
import com.karandeepsingh.bookhub.activities.DescriptionActivity
import com.karandeepsingh.bookhub.database.BookEntity
import com.squareup.picasso.Picasso

class FavouritesRecyclerAdapter(val context: Context,val booklist:List<BookEntity>):RecyclerView.Adapter<FavouritesRecyclerAdapter.FavouritesRecyclerViewHolder>(){
    class FavouritesRecyclerViewHolder(view: View):RecyclerView.ViewHolder(view)
    {
        var imgBookImage:ImageView=view.findViewById(R.id.imgBookImage)
        var txtBookName:TextView=view.findViewById(R.id.txtBookName)
        var txtBookAuthor:TextView=view.findViewById(R.id.txtBookPrice)
        var txtBookPrice:TextView=view.findViewById(R.id.txtBookPrice)
        var txtBookRating:TextView=view.findViewById(R.id.txtBookRating)
        var favouritesCard:CardView=view.findViewById(R.id.favouritesCard)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavouritesRecyclerViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_favourite_single_row,parent,false)
        return FavouritesRecyclerViewHolder(view)

    }

    override fun onBindViewHolder(holder: FavouritesRecyclerViewHolder, position: Int) {

        var book=booklist[position]
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.imgBookImage)
        holder.txtBookName.text=book.bookName
        holder.txtBookAuthor.text=book.bookAuthor
        holder.txtBookPrice.text=book.bookPrice
        holder.txtBookRating.text=book.bookRating
        holder.favouritesCard.setOnClickListener{
            val intent=Intent(context,DescriptionActivity::class.java)
            intent.putExtra("book_id",book.bookId.toString())
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return booklist.size
    }
}