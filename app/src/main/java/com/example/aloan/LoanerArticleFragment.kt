package com.example.aloan

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class LoanerArticleFragment : Fragment() {

    var recyclerView: RecyclerView?=null
    var searchView: SearchView?=null
    var title=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_loaner_article, container, false)
        recyclerView=root.findViewById(R.id.recyclerView)
        searchView=root.findViewById(R.id.searchview)

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                title=query
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                title=newText
                showlist()
                return false
            }
        })
        showlist()
        return root
    }
    private fun showlist() {
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.article_url)+"?title="+title
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(url).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            data.add(Data(
                                item.getString("ArticleID"),
                                item.getString("title"),
                                item.getString("detail"),
                                item.getString("view"),
                                item.getString("image_article"),
                                item.getString("dateCreate")



                            )
                            )
                            recyclerView!!.adapter = DataAdapter(data)

                        }
                    } else {
                        recyclerView!!.adapter = DataAdapter(data)


                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                response.code
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    internal class Data(
        var ArticleID: String,var title: String,var detail: String,var view: String,
        var image_article: String,var txtdate:String

    )
    internal inner class DataAdapter(private val list: List<Data>) :
        RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.item_article,
                parent, false
            )
            return ViewHolder(view)
        }


        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            var url = getString(R.string.root_url) +
                    getString(R.string.article_image_url) + data.image_article
            Picasso.get().load(url).into(holder.image_article)
            holder.txttitle.text=data.title
            holder.txtview.text=data.view

            holder.txtdetail.text= Html.fromHtml(data.detail).toString().take(20)+"..."

            holder.txtdatear.text=data.txtdate

            holder.constraintLayout?.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("ArticleID", data.ArticleID)
                val fm = LoanerArticleDetailFragment()
                fm.arguments = bundle;
                val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                fragmentTransaction.commit()
            }


        }
        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var txttitle: TextView = itemView.findViewById(R.id.txttitle)
            var image_article: ImageView = itemView.findViewById(R.id.imageView6)
            var txtview: TextView = itemView.findViewById(R.id.txtview)
            var txtdetail : TextView =itemView.findViewById(R.id.textView132)
            var constraintLayout: ConstraintLayout =itemView.findViewById(R.id.constraintlayout)
            var txtdatear:TextView=itemView.findViewById(R.id.txtdatear)



        }
    }
}