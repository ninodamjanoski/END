package com.endumedia.core.ui

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.endumedia.core.GlideRequests
import com.endumedia.core.R
import com.endumedia.core.vo.Product

class ProductViewHolder(view: View, private val glide: GlideRequests)
: RecyclerView.ViewHolder(view) {

    private val tvName: TextView = view.findViewById(R.id.product_name)
    private val tvPrice: TextView = view.findViewById(R.id.product_price)
    private val productImage: ImageView = view.findViewById(R.id.product_image)
    val HTTP_GLIDE_IMAGE_TIMEOUT_MS = 6000

    fun bind(product: Product?) {
        tvName.text = product?.name
        tvPrice.text = product?.price

        product?.run {
            glide
                .load(image)
                .apply(RequestOptions().timeout(HTTP_GLIDE_IMAGE_TIMEOUT_MS))
                .placeholder(ColorDrawable(ContextCompat.getColor(productImage.context,
                    R.color.placeholder_grey)))
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(productImage);
        }
    }

    fun updatePrice(item: Product?) {
        item?.run {
            tvPrice.text = price
        }
    }

    companion object {
        fun create(parent: ViewGroup, glide: GlideRequests): ProductViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.product_list_row, parent, false)
            return ProductViewHolder(view, glide)
        }
    }
}
