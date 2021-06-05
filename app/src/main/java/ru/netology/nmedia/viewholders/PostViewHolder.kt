package ru.netology.nmedia.viewholders

import android.annotation.SuppressLint
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.listeners.OnInteractionListener
import ru.netology.nmedia.repository.PostRepositoryImpl.Companion.context
import java.text.SimpleDateFormat
import java.util.*

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            val url = "http://10.0.2.2:9999/avatars/${post.authorAvatar}"
            Glide
                .with(avatar)
                .load(url)
                .placeholder(R.drawable.ic_avatar_placeholder)
                .error(R.drawable.ic_avatar_not_found)
                .timeout(10_000)
                .circleCrop()
                .into(avatar)

            published.text = Date(post.published * 1000).toFormattedString()
            content.text = post.content
            // в адаптере
            like.isChecked = post.likedByMe
            like.text = "${post.likes}"

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun Date.toFormattedString() = context.getString(
        R.string.published_at,
        SimpleDateFormat("dd MMM yyyy").format(this),
        SimpleDateFormat("hh:mm:ss").format(this)
    )
}
