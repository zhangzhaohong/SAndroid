package com.tristana.sandroid.ui.illegalManager.fragment.video.player;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.tristana.sandroid.R;
import com.tristana.sandroid.tools.log.Timber;
import com.tristana.sandroid.tools.toast.ToastUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.BundleCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class VideoPlayerFragment extends Fragment {

    private VideoPlayerViewModel videoPlayerViewModel;

    private CustomVideoView videoView;

    private Boolean isPlaying = false;

    private int currentPosition = 0;

    private Timber timber = new Timber("VideoPlayerFragment");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        videoPlayerViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(VideoPlayerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_video_player, container, false);
        currentPosition = 0;
        String url = "";
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString("VideoUrl");
        }
        videoView = root.findViewById(R.id.videoView);
        Uri uri = Uri.parse(url);
        videoView.setVideoURI(uri);
        videoView.setMediaController(new MediaController(requireActivity()));
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                requireActivity().onBackPressed();
            }
        });
        videoPlayerViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        videoView.start();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        timber.d("On Resume");
        videoView.seekTo(currentPosition);
        if (isPlaying) {
            isPlaying = false;
            videoView.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        timber.d("On Pause");
        currentPosition = videoView.getCurrentPosition();
        if (videoView.isPlaying()) {
            isPlaying = true;
            videoView.pause();
        } else {
            isPlaying = false;
        }
    }
}