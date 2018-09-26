package com.pushpal.bakingapp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.pushpal.bakingapp.R;
import com.pushpal.bakingapp.adapter.IngredientsAdapter;
import com.pushpal.bakingapp.model.Ingredient;
import com.pushpal.bakingapp.model.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeActivity extends AppCompatActivity implements ExoPlayer.EventListener {

    private static MediaSessionCompat mMediaSession;
    private final String TAG = RecipeActivity.class.getSimpleName();
    @BindView(R.id.playerView)
    public SimpleExoPlayerView mPlayerView;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.tv_description)
    public TextView stepDescription;
    @BindView(R.id.btn_previous)
    public AppCompatButton previousBtn;
    @BindView(R.id.btn_next)
    public AppCompatButton nextBtn;
    @BindView(R.id.rv_ingredients)
    public RecyclerView mRecyclerView;
    List<Step> steps = null;
    List<Ingredient> ingredients = null;
    Step currentStep = null;
    int position = -1;
    private SimpleExoPlayer mExoPlayer;
    private PlaybackStateCompat.Builder mStateBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            steps = bundle.getParcelableArrayList("Step");
            ingredients = bundle.getParcelableArrayList("Ingredients");
            position = bundle.getInt("Position");

            setCurrentStep();
            setButtonState();
        }

        setUpActionBar();
    }

    public void onPreviousStep(View view) {
        position--;
        if (position > -2 && position <= (steps.size() - 1)) {
            setCurrentStep();
            setButtonState();
        } else
            position++;
    }

    public void onNextStep(View view) {
        position++;
        if (position > -1 && position <= (steps.size() - 1)) {
            setCurrentStep();
            setButtonState();
        } else
            position--;
    }

    private void setButtonState() {
        if (position == -1)
            previousBtn.setEnabled(false);
        else
            previousBtn.setEnabled(true);

        if (steps != null && position == (steps.size() - 1))
            nextBtn.setEnabled(false);
        else
            nextBtn.setEnabled(true);
    }

    private void setCurrentStep() {
        releasePlayer();

        if (steps != null && position > -1) {
            currentStep = steps.get(position);

            if (currentStep.getDescription() != null && !currentStep.getDescription().equals(""))
                stepDescription.setText(currentStep.getDescription());
            if (currentStep.getVideoURL() != null && !currentStep.getVideoURL().equals("")) {
                initializeMediaSession();
                initializePlayer(Uri.parse(currentStep.getVideoURL()));
            } else if (currentStep.getThumbnailURL() != null && !currentStep.getThumbnailURL().equals("")) {
                initializeMediaSession();
                initializePlayer(Uri.parse(currentStep.getThumbnailURL()));
            }

            mRecyclerView.setVisibility(View.GONE);
            stepDescription.setVisibility(View.VISIBLE);
        } else {
            setupRecyclerView();
            mRecyclerView.setVisibility(View.VISIBLE);
            stepDescription.setVisibility(View.GONE);
        }

        if (mExoPlayer == null)
            mPlayerView.setVisibility(View.GONE);
        else
            mPlayerView.setVisibility(View.VISIBLE);
    }

    private void setupRecyclerView() {
        IngredientsAdapter mAdapter = new IngredientsAdapter(ingredients);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setUpActionBar() {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(StepsActivity.RecipeName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        if (mMediaSession != null)
            mMediaSession.setActive(false);
    }

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(this, "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    this, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(this, TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }
}
