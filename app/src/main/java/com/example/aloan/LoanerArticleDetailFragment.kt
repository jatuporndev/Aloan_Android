package com.example.aloan

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class LoanerArticleDetailFragment : Fragment() {
    var txtback: ImageView? = null
    var imageArticle: ImageView? = null
    var txtTitle: TextView? = null
    var txtdetail: TextView? = null
    var txtview: TextView? = null
    var txtdate:TextView?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_loaner_article_detail, container, false)
        val Bundle = this.arguments
        txtback = root.findViewById(R.id.imageviewback)
        imageArticle = root.findViewById(R.id.imageViewartic)
        txtTitle = root.findViewById(R.id.txttitle)
        txtdetail = root.findViewById(R.id.txtdetail)
        txtview = root.findViewById(R.id.txtview)
        txtdate=root.findViewById(R.id.txtdatearti)

        txtback?.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, LoanerArticleFragment())
            fragmentTransaction.commit()
        }
        Article(Bundle?.get("ArticleID").toString())
        return root
    }

    private fun Article(ArticleID: String) {

        var url: String =
            getString(R.string.root_url) + getString(R.string.articledetail_url) + ArticleID
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val data = JSONObject(response.body!!.string())
                    if (data.length() > 0) {
                        ///////////////////////////
                        txtdetail?.text=data.getString("detail").replace("<.*?>".toRegex(), " ")
                        txtTitle?.text = data.getString("title")
                        txtview?.text = data.getString("view")
                        txtdate?.text = data.getString("dateCreate")
                        var url = getString(R.string.root_url) +
                                getString(R.string.article_image_url) + data.getString("image_article")
                        Picasso.get().load(url).into(imageArticle)

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
}