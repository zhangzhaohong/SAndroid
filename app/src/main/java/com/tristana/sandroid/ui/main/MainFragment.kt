package com.tristana.sandroid.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.blankj.utilcode.util.LogUtils
import com.google.common.base.Splitter
import com.to.aboomy.pager2banner.Banner
import com.to.aboomy.pager2banner.IndicatorView
import com.to.aboomy.pager2banner.ScaleInTransformer
import com.tristana.sandroid.FragmentDirector
import com.tristana.sandroid.MyApplication
import com.tristana.sandroid.R
import com.tristana.sandroid.customizeInterface.IOnClickBannerInterface
import com.tristana.sandroid.dataModel.bannerModel.BannerDataModel
import com.tristana.sandroid.ui.ad.AdWebViewFragment
import com.tristana.sandroid.ui.main.adapter.ImageAdapter
import net.lucode.hackware.magicindicator.buildins.UIUtil


class MainFragment : Fragment(), IOnClickBannerInterface {
    private var mainViewModel: MainViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (mainViewModel == null) mainViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(
                    MainViewModel::class.java
                )
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        val textView = root.findViewById<TextView>(R.id.text_home)
        val banner: Banner = root.findViewById(R.id.banner)
        val indicator = IndicatorView(requireActivity())
            .setIndicatorColor(Color.DKGRAY)
            .setIndicatorSelectorColor(Color.WHITE)
            .setIndicatorRatio(1f) //ratio，默认值是1 ，也就是说默认是圆点，根据这个值，值越大，拉伸越长，就成了矩形，小于1，就变扁了呗
            .setIndicatorRadius(2f) // radius 点的大小
            .setIndicatorSelectedRatio(3f)
            .setIndicatorSelectedRadius(2f)
            .setIndicatorStyle(IndicatorView.IndicatorStyle.INDICATOR_DASH)
        val bannerData = ArrayList<BannerDataModel>()
        bannerData.add(
            BannerDataModel(
                MyApplication.cdnHost + "/sandroid/banner-main/20230829_170000_1.webp",
                "https://www.baidu.com"
            )
        )
        bannerData.add(
            BannerDataModel(
                MyApplication.cdnHost + "/sandroid/banner-main/20230829_170000_2.webp",
                "https://www.csdn.net"
            )
        )
        bannerData.add(
            BannerDataModel(
                MyApplication.cdnHost + "/sandroid/banner-main/20230829_170000_3.webp",
                "https://www.bilibili.com"
            )
        )
        bannerData.add(
            BannerDataModel(
                MyApplication.cdnHost + "/sandroid/banner-main/20230829_170000_4.webp",
                "http://192.168.2.70:8080/tools/DouYin/player/video?vid=v0200fg10000c5v5ha3c77u5r9jepv6g&ratio=540p&isDownload=0"
            )
        )
        bannerData.add(
            BannerDataModel(
                MyApplication.cdnHost + "/sandroid/banner-main/20230829_170000_5.webp",
                "router://m.sandroid.com/app/activity/scheme?direct=/app/browser/ad&extra=https://www.douyin.com/"
            )
        )
        val adapter = ImageAdapter(requireContext(), bannerData)
        adapter.onClickBannerInterface = this
        banner
            .setIndicator(indicator)
            .setPageMargin(
                UIUtil.dip2px(requireActivity(), 20.0),
                UIUtil.dip2px(requireActivity(), 10.0)
            )
            .addPageTransformer(ScaleInTransformer()).adapter = adapter
        mainViewModel!!.text.observe(viewLifecycleOwner) { text: String? -> textView.text = text }
        return root
    }

    override fun onClick(it: View?, directionPath: String) {
        LogUtils.i("onClick: $directionPath")
        val navController =
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        if (directionPath.isNotEmpty()) {
            if (directionPath.startsWith("router://")) {
                val direct = getParam(directionPath, "direct")
                val extra = getParam(directionPath, "extra")
                FragmentDirector.doDirect(navController, direct, extra)
            } else {
                val direct = AdWebViewFragment.ROUTE
                FragmentDirector.doDirect(navController, direct, directionPath)
            }
        }
    }

    private fun getParam(url: String, name: String): String? {
        val params = url.substring(url.indexOf("?") + 1)
        val split = Splitter.on("&").withKeyValueSeparator("=").split(params)
        return split[name]
    }
}