package com.karandeepsingh.bookhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.karandeepsingh.bookhub.R
import com.karandeepsingh.bookhub.activities.DescriptionActivity
import com.karandeepsingh.bookhub.models.Book
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(val context: Context, val listItem: ArrayList<Book>) :
    RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {
    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtSingleItemTitle: TextView = view.findViewById(R.id.txtSingleItemTitle)
        val txtSingleItemAuthorName:TextView=view.findViewById(R.id.txtSingleItemAuthorName)
        val txtSingleItemPrice:TextView=view.findViewById(R.id.txtSingleItemPrice)
        val txtSingleItemRating:TextView=view.findViewById(R.id.txtSingleItemRating)
        val imgSingleItemPic:ImageView=view.findViewById(R.id.imgSingleItemPic)
        val rlRecyclerDashboard:RelativeLayout=view.findViewById(R.id.rlRecyclerDashboard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dashboard_recycler_single_row_item, parent, false)
        return DashboardViewHolder(view)

    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
    val book:Book =listItem[position]
        holder.txtSingleItemTitle.text=book.bookName
        holder.txtSingleItemAuthorName.text=book.bookAuthor
        holder.txtSingleItemPrice.text=book.bookPrice
        holder.txtSingleItemRating.text=book.bookRating
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.imgSingleItemPic)
        holder.rlRecyclerDashboard.setOnClickListener {
            val intent=Intent(context,DescriptionActivity::class.java)
            intent.putExtra("book_id",book.bookId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listItem.size
    }
}