package ru.rarescrap.instatsporttestapp

import android.media.MediaPlayer
import android.media.MediaPlayer.TrackInfo
import android.media.TimedText
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RawRes
import androidx.core.content.ContextCompat
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.media.PlayerAdapter
import androidx.leanback.widget.PlaybackControlsRow
import java.io.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


/** Handles video playback with media controls. */
class PlaybackVideoFragment : VideoSupportFragment() {

    private lateinit var mTransportControlGlue: PlaybackTransportControlGlue<MediaPlayerAdapter>

    private lateinit var tv_subtitles: TextView
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val (_, title, description, _, _, videoUrl) =
                activity?.intent?.getSerializableExtra(PlaybackActivity.MOVIE) as Movie

        val glueHost = VideoSupportFragmentGlueHost(this@PlaybackVideoFragment)
        val playerAdapter = MediaPlayerAdapter(activity)
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)

        mediaPlayer = playerAdapter.mediaPlayer

        mTransportControlGlue = PlaybackTransportControlGlue(getActivity(), playerAdapter)
        mTransportControlGlue.host = glueHost
        mTransportControlGlue.title = title
        mTransportControlGlue.subtitle = description
        mTransportControlGlue.playWhenPrepared()

        playerAdapter.setDataSource(Uri.parse(videoUrl))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState) as FrameLayout

        val subtitlesTextView = TextView(context)
        subtitlesTextView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
        subtitlesTextView.setBackgroundColor(ContextCompat.getColor(context!!, R.color.subtitle_bg))

        val controlsView = view.getChildAt(2) as FrameLayout
        controlsView.addView(subtitlesTextView)

        tv_subtitles = subtitlesTextView

        return view
    }

    val timer = Timer()
    lateinit var task: TimerTask
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        task = object : TimerTask() {
            override fun run() {
                this@PlaybackVideoFragment.activity?.runOnUiThread {
                    try {
                        val pos = mediaPlayer.currentPosition
                        var subtitle: String
                        subtitle = when (pos) {
                            in 5000..6000 -> "Строка субтитров на секунде 5"
                            in 6000..7000 -> "Строка субтитров на секунде 6"
                            in 7000..8000 -> "Строка субтитров на секунде 7"
                            in 8000..9000 -> "Строка субтитров на секунде 8"
                            in 9000..10000 -> "Строка субтитров на секунде 9"
                            in 10000..11000 -> "Строка субтитров на секунде 10"
                            else -> ""
                        }

                        tv_subtitles.setText(subtitle)
                    } catch (e: java.lang.Exception) {
                        this.cancel()
                    }
                }
            }
        }
        timer.schedule(task, 1, 1)
    }

    override fun onPause() {
        super.onPause()
        mTransportControlGlue.pause()
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        task.cancel()
        timer.cancel()
        timer.purge()
    }
}
