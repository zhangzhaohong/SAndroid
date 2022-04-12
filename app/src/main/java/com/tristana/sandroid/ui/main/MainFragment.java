package com.tristana.sandroid.ui.main;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.to.aboomy.pager2banner.Banner;
import com.to.aboomy.pager2banner.IndicatorView;
import com.to.aboomy.pager2banner.ScaleInTransformer;
import com.tristana.sandroid.R;
import com.tristana.sandroid.model.bannerModel.BannerDataModel;
import com.tristana.sandroid.ui.home.HomeViewModel;
import com.tristana.sandroid.ui.main.adapter.ImageAdapter;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;

public class MainFragment extends Fragment {

    private MainViewModel mainViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (mainViewModel == null)
            mainViewModel =
                    ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(MainViewModel.class);
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        final Banner banner = root.findViewById(R.id.banner);
        IndicatorView indicator = new IndicatorView(requireActivity())
                .setIndicatorColor(Color.DKGRAY)
                .setIndicatorSelectorColor(Color.WHITE)
                .setIndicatorRatio(1f) //ratio，默认值是1 ，也就是说默认是圆点，根据这个值，值越大，拉伸越长，就成了矩形，小于1，就变扁了呗
                .setIndicatorRadius(2f) // radius 点的大小
                .setIndicatorSelectedRatio(3)
                .setIndicatorSelectedRadius(2f)
                .setIndicatorStyle(IndicatorView.IndicatorStyle.INDICATOR_DASH);
        ArrayList<BannerDataModel> bannerData = new ArrayList<>();
        bannerData.add(new BannerDataModel("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F0122065754e97832f875a4291747d7.jpg%401280w_1l_2o_100sh.jpg&refer=http%3A%2F%2Fimg.zcool.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1652351410&t=6ba9e8ef4f9086e19e86152cb6af5bbf", ""));
        bannerData.add(new BannerDataModel("https://img0.baidu.com/it/u=2591738758,810788141&fm=253&fmt=auto&app=138&f=JPEG?w=2000&h=400", ""));
        bannerData.add(new BannerDataModel("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01e669572bfe806ac725381234430c.jpg%402o.jpg&refer=http%3A%2F%2Fimg.zcool.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1652358999&t=3fde581da2bc2b53a4e134daec92992b", ""));
        bannerData.add(new BannerDataModel("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01d56b5542d8bc0000019ae98da289.jpg%401280w_1l_2o_100sh.jpg&refer=http%3A%2F%2Fimg.zcool.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1652358999&t=031988bfab0c774b34ea31ce207a1e36", ""));
        bannerData.add(new BannerDataModel("https://img1.baidu.com/it/u=2780823041,991952778&fm=253&fmt=auto&app=138&f=JPG?w=1280&h=453", ""));
        banner
                .setIndicator(indicator)
                .setPageMargin(UIUtil.dip2px(requireActivity(), 20), UIUtil.dip2px(requireActivity(), 10))
                .addPageTransformer(new ScaleInTransformer())
                .setAdapter(new ImageAdapter(requireActivity(), requireParentFragment(), requireContext(), bannerData));
        mainViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
}