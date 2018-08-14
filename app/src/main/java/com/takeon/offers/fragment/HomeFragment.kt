package com.takeon.offers.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.takeon.offers.R
import com.takeon.offers.R.layout
import com.takeon.offers.adapter.CategoryListAdapter
import com.takeon.offers.adapter.MyViewPagerAdapter
import com.takeon.offers.kprogresshud.KProgressHUD
import com.takeon.offers.model.CategoryModel
import com.takeon.offers.ui.MyApplication
import com.takeon.offers.ui.business.BusinessActivity
import com.takeon.offers.ui.business.BusinessSearchActivity
import com.takeon.offers.utils.CommonDataUtility
import com.takeon.offers.utils.StaticDataUtility
import com.takeon.offers.utils.VolleyNetWorkCall
import kotlinx.android.synthetic.main.takeon_fragment_home.imgNext
import kotlinx.android.synthetic.main.takeon_fragment_home.imgPrevious
import kotlinx.android.synthetic.main.takeon_fragment_home.imgSearch
import kotlinx.android.synthetic.main.takeon_fragment_home.indicator
import kotlinx.android.synthetic.main.takeon_fragment_home.llRoot
import kotlinx.android.synthetic.main.takeon_fragment_home.recyclerViewCategory
import kotlinx.android.synthetic.main.takeon_fragment_home.txtCity
import kotlinx.android.synthetic.main.takeon_fragment_home.view_pager
import kotlinx.android.synthetic.main.takeon_layout_error.btnRetry
import kotlinx.android.synthetic.main.takeon_layout_error.llErrorLayout
import kotlinx.android.synthetic.main.takeon_layout_error.txtMessage
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class HomeFragment : BaseFragment(),
    OnClickListener,
    VolleyNetWorkCall.OnResponse,
    CategoryListAdapter.CategoryClickListener {

  private var categoryArrayList: ArrayList<CategoryModel>? = null
  private var viewPagerPosition = 0
  private var categoryPosition = 0
  private val imageList = ArrayList<String>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(layout.takeon_fragment_home, container, false)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    initUI()
  }

  override fun onClick(v: View?) {

    when (v?.id) {
      R.id.imgSearch -> {
        startActivity(Intent(activity, BusinessSearchActivity::class.java))
      }
    }
  }

  override fun onSuccessCall(
    response: JSONObject,
    url: String
  ) {

    hideProgressBar()

    try {

      when (url) {

        StaticDataUtility.API_CATEGORIES -> {

          if (response.optString("status").equals("true", ignoreCase = true)) {

            val jsonArray = response.getJSONArray("categories")

            for (i in 0 until jsonArray.length()) {

              val jsonObject = jsonArray.getJSONObject(i)

              val categoryModel = CategoryModel()
              categoryModel.categoryId = jsonObject.optString("id")
              categoryModel.categoryName = jsonObject.optString("category")
              categoryModel.categoryImage = jsonObject.optString("image")

              categoryArrayList!!.add(categoryModel)
            }

            if (categoryArrayList!!.size > 0) {
              recyclerViewCategory!!.adapter =
                  CategoryListAdapter(activity, categoryArrayList!!, this)
            } else {
              showNoDataLayout("noData", getString(R.string.str_no_category))
            }
            getSlider()
          } else {
            showNoDataLayout("error", getString(R.string.str_no_category_error))
          }
        }

        StaticDataUtility.API_CATEGORY_SLIDER -> {

          if (response.optString("status").equals("true", ignoreCase = true)) {

            val jsonArray = response.getJSONArray("slider")

            for (i in 0 until jsonArray.length()) {
              imageList.addAll(
                  setOf(
                      StaticDataUtility.CATEGORY_PHOTO_URL + jsonArray.getJSONObject(i)
                          .optString("image")
                  )
              )
            }

            setSlider()
          }
        }
      }

    } catch (e: Exception) {
      e.printStackTrace()
      CommonDataUtility.showSnackBar(llRoot, getString(R.string.error_server))
    }
  }

  override fun onPostSuccessCall(
    response: String,
    url: String
  ) {

  }

  override fun onFailCall(
    error: VolleyError,
    url: String
  ) {
    hideProgressBar()

    if (url == StaticDataUtility.API_CATEGORIES) {
      showNoDataLayout("error", getString(R.string.str_no_category_error))
    }
  }

  private fun showNoDataLayout(
    type: String,
    message: String
  ) {

    btnRetry.visibility = if (type == "error") View.VISIBLE else View.GONE

    llErrorLayout!!.visibility = View.VISIBLE
    recyclerViewCategory!!.visibility = View.GONE

    txtMessage!!.text = message
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    when (requestCode) {

      101 -> {

        val perms = HashMap<String, Int>()
        // Initial
        perms[Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager
            .PERMISSION_GRANTED
        perms[Manifest.permission.ACCESS_COARSE_LOCATION] = PackageManager
            .PERMISSION_GRANTED

        // Fill with results
        for (i in permissions.indices)
          perms[permissions[i]] = grantResults[i]

        val fineLocation =
          perms[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED
        val coarseLocation =
          perms[Manifest.permission.ACCESS_COARSE_LOCATION] == PackageManager.PERMISSION_GRANTED

        if (fineLocation && coarseLocation) {
          openActivity()
        } else {
          CommonDataUtility.showSnackBar(llRoot, getString(R.string.location_permission))

          if (locationPermission()) {
            openActivity()
          }
        }
      }
    }
    // other 'case' lines to check for other
    // permissions this app might request
  }

  override fun categoryOnclick(position: Int) {

    categoryPosition = position
    if (locationPermission()) {
      openActivity()
    }
  }

  private fun initUI() {

    progressHUD = KProgressHUD.create(activity)
        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        .setCancellable(false)

    if (!TextUtils.isEmpty(
            MyApplication.instance.preferenceUtility
                .getString("city")
        )
    ) {
      txtCity!!.text = MyApplication.instance.preferenceUtility
          .getString("city")
    } else {
      txtCity!!.text = String.format("%s", "Surat")
    }

    imgSearch.setOnClickListener(this)

    recyclerViewCategory!!.layoutManager = GridLayoutManager(activity, 3)
    recyclerViewCategory!!.isNestedScrollingEnabled = false

    getCategory()

    btnRetry.setOnClickListener { getCategory() }
  }

  private fun setSlider() {

//    imageList.add("https://i.pinimg.com/originals/c6/a2/89/c6a289648d689ebf779f5c11b2699ab5.jpg")
//    imageList.add("https://www.planwallpaper.com/static/images/Old_tracks_iPhone_6_Wallpapers.jpg")
//    imageList.add(
//        "http://papers.co/wallpaper/papers.co-aa99-wallpaper-europe-and-africa-worldmap-33-iphone6-wallpaper.jpg"
//    )
//    imageList.add(
//        "https://images.designtrends.com/wp-content/uploads/2016/03/29071910/Very-nice-Fireworks-iPhone-6-Wallpaper.jpg"
//    )

    val adapter = MyViewPagerAdapter(activity, imageList)
    view_pager!!.adapter = adapter
    indicator!!.setViewPager(view_pager)

    view_pager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
      override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
      ) {
        viewPagerPosition = view_pager!!.currentItem
      }

      override fun onPageSelected(position: Int) {

      }

      override fun onPageScrollStateChanged(state: Int) {

      }
    })

    imgNext!!.setOnClickListener {
      if (viewPagerPosition < 4) {
        view_pager!!.currentItem = viewPagerPosition + 1
      }
    }

    imgPrevious!!.setOnClickListener {
      if (viewPagerPosition > 0) {
        view_pager!!.currentItem = viewPagerPosition - 1
      }
    }
  }

  private fun getCategory() {

    categoryArrayList = ArrayList()
    showProgressBar()

    recyclerViewCategory!!.visibility = View.VISIBLE

    netWorkCall.makeServiceCall(activity, StaticDataUtility.API_CATEGORIES, this)
  }

  private fun getSlider() {

    showProgressBar()

    netWorkCall.makeServiceCall(activity, StaticDataUtility.API_CATEGORY_SLIDER, this)
  }

  private fun showProgressBar() {
    progressHUD!!.show()
  }

  fun hideProgressBar() {
    if (progressHUD != null) {
      progressHUD!!.dismiss()
    }
  }

  private fun openActivity() {

    val intent = Intent(activity, BusinessActivity::class.java)
    intent.putExtra("category_id", categoryArrayList!![categoryPosition].categoryId)
    intent.putExtra("category_name", categoryArrayList!![categoryPosition].categoryName)
    startActivity(intent)
  }
}
