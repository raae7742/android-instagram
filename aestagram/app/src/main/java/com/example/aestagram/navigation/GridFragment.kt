package com.example.aestagram.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.aestagram.R
import com.example.aestagram.navigation.model.ContentDTO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_grid.view.*

class GridFragment : Fragment() {
    var firestore: FirebaseFirestore? = null
    var fragmentView : View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_grid, container, false)
        firestore = FirebaseFirestore.getInstance()
        fragmentView?.gridfragment_recyclerview?.adapter = UserFragmentRecyclerViewAdapter()
        fragmentView?.gridfragment_recyclerview?.layoutManager = GridLayoutManager(activity, 3)
        return fragmentView
    }

    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()

        init {
            // uid가 같은 content들만 contentDTOs에 저장
            firestore?.collection("images")?.addSnapshotListener { value, error ->
                // Sometimes, This code return null of value when it signout
                if(value == null) return@addSnapshotListener

                // Get data
                for (snapshot in value.documents) {
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                // textView에 post 개수 설정
                notifyDataSetChanged()


            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels / 3

            // 화면의 1/3 크기를 갖는 정사각형 imageView 생성
            var imageview = ImageView(parent.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            return CustomViewHolder(imageview)
        }

        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview) {}

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageview = (holder as CustomViewHolder).imageview

            // content 이미지 load
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl).apply(
                RequestOptions().centerCrop()).into(imageview)
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }
}