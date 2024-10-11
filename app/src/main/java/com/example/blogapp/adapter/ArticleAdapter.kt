package com.example.blogapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.databinding.ArticleItemBinding

class ArticleAdapter (
    private val context: Context,
    private var blogList:List<BlogItemModel>,
    private val itemClickListener:OnItemClickListener
): RecyclerView.Adapter<ArticleAdapter.BlogViewHolder>(){
    interface OnItemClickListener{
        fun onEditClick(blogItem: BlogItemModel)
        fun onReadMoreClick(blogItem: BlogItemModel)
        fun onDeleteClick(blogItem: BlogItemModel)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticleAdapter.BlogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ArticleItemBinding.inflate(inflater,parent,false)
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleAdapter.BlogViewHolder, position: Int) {
        val blogItem = blogList[position]
            holder.bind(blogItem)
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    fun setData(blogSavedList: ArrayList<BlogItemModel>) {
        this.blogList = blogSavedList
        notifyDataSetChanged()
    }

    inner class BlogViewHolder(private val binding:ArticleItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(blogItem:BlogItemModel){

            binding.heading.text = blogItem.heading
//            Glide.with(binding.profile.context)
//                .load(blogItem.profileImage)
//                .into(binding.profile)
            binding.userName.text = blogItem.userName
            binding.date.text = blogItem.date
            binding.post.text = blogItem.post

            //handle read more articles
            binding.readMoreButton.setOnClickListener{
                itemClickListener.onReadMoreClick(blogItem)
            }

            binding.editButton.setOnClickListener{
                itemClickListener.onEditClick(blogItem)
            }
            binding.deleteButton.setOnClickListener{
                itemClickListener.onDeleteClick(blogItem)
            }

        }

    }

}