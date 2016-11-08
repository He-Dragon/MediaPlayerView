package com.example.mediaplayerview;




import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private ImageButton playBtn;
	private SurfaceView surfaceView;
	private MediaPlayer mediaPlayer;
	private int position;
	private SeekBar mSeekBar;
	private ImageView mPlay;
	private TextView mPlayTime,fullScreen,back;
	private TextView mDurationTime;
	private View mBottomView;
	private static final int HIDE_TIME = 5000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		
	}

	private void initView() {
		// TODO Auto-generated method stub
		playBtn = (ImageButton) this.findViewById(R.id.playBtn);
		mPlayTime = (TextView) findViewById(R.id.play_time);
		mDurationTime = (TextView) findViewById(R.id.total_time);
		mPlay = (ImageView) findViewById(R.id.play_btn);
		mSeekBar = (SeekBar) findViewById(R.id.seekbar);
		mBottomView = findViewById(R.id.bottom_layout);
		mPlay.setOnClickListener(this);
		playBtn.setOnClickListener(this);
		mediaPlayer = new MediaPlayer();
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
		// 设置SurfaceView自己不管理的缓冲区
		surfaceView.getHolder()
				.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceView.getHolder().setFixedSize(180, 144);
		surfaceView.getHolder().setKeepScreenOn(true);
		surfaceView.getHolder().addCallback(new Callback() {
			/**
			 * @Title：
			 * @author：
			 * @annotation：画面销毁
			 */
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {

			}

			/**
			 * @Title：
			 * @author：
			 * @annotation：画面创建
			 */
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				if (position > 0) {
					try {
						// 开始播放
						play();
						// 并直接从指定位置开始播放
						mediaPlayer.seekTo(position);
						position = 0;
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}

			/**
			 * @Title：
			 * @author：
			 * @annotation：画面修改
			 */
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.playBtn:
			play();
			break;
		case R.id.play_btn:
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
				mPlay.setImageResource(R.drawable.video_btn_down);
			} else {
				mediaPlayer.start();
				mPlay.setImageResource(R.drawable.video_btn_on);
			}
			break;
		default:
			break;
		}

	}

	@Override
	protected void onPause() {
		// 先判断是否正在播放
		if (mediaPlayer.isPlaying()) {
			// 如果正在播放我们就先保存这个播放位置
			position = mediaPlayer.getCurrentPosition();
			mediaPlayer.stop();
		}
		super.onPause();
	}
/**
 * @Title：play()
 * @author：
 * @annotation：开始播放
 */
	private void play() {
		try {
			playBtn.setVisibility(View.GONE);
			mediaPlayer.reset();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// 设置需要播放的视频
			mediaPlayer.setDataSource("http://down.ffxia.com/avi/262.avi");
			// 把视频画面输出到SurfaceView
			mediaPlayer.setDisplay(surfaceView.getHolder());
			mediaPlayer.prepare();
			// 播放
			mediaPlayer.start();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	/**
	 * showOrHide()
	 * @author：
	 * @annotation：进度条的展示或者隐藏
	 */
	private void showOrHide() {
		if (back.getVisibility() == View.VISIBLE) {
			back.clearAnimation();
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.option_leave_from_top);
			animation.setAnimationListener(new AnimationImp() {
				@Override
				public void onAnimationEnd(Animation animation) {
					super.onAnimationEnd(animation);
					back.setVisibility(View.GONE);
				}
			});
			back.startAnimation(animation);

			mBottomView.clearAnimation();
			Animation animation1 = AnimationUtils.loadAnimation(this,
					R.anim.option_leave_from_bottom);
			animation1.setAnimationListener(new AnimationImp() {
				@Override
				public void onAnimationEnd(Animation animation) {
					super.onAnimationEnd(animation);
					mBottomView.setVisibility(View.GONE);
				}
			});
			mBottomView.startAnimation(animation1);
		} else {
			back.setVisibility(View.VISIBLE);
			back.clearAnimation();
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.option_entry_from_top);
			back.startAnimation(animation);

			mBottomView.setVisibility(View.VISIBLE);
			mBottomView.clearAnimation();
			Animation animation1 = AnimationUtils.loadAnimation(this,
					R.anim.option_entry_from_bottom);
			mBottomView.startAnimation(animation1);
			mHandler.removeCallbacks(hideRunnable);
			mHandler.postDelayed(hideRunnable, HIDE_TIME);
		}
	}
	
	private Handler mHandler = new Handler() {

//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			switch (msg.what) {
//			case 1:
//				try {
//					mp = mediaPlayer.getCurrentPosition();
//					mp1 = mediaPlayer.getDuration();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//				if (mp > 0) {
//					mPlayTime.setText(formatTime(mp));
//					int progress = mp * 100 / mp1;
//					mSeekBar.setProgress(progress);
//					if (mp > mp1 - 100) {
//						mPlayTime.setText("00:00");
//						mSeekBar.setProgress(0);
//					}
//					// mSeekBar.setSecondaryProgress(mediaPlayer.getBufferPercentage());
//				} else {
//					mPlayTime.setText("00:00");
//					mSeekBar.setProgress(0);
//				}
//
//				break;
//
//			default:
//				break;
//			}
//		}

	};

	private Runnable hideRunnable = new Runnable() {

		@Override
		public void run() {
			showOrHide();
		}
	};
	/**
	 * @author：
	 * @annotation：写一个AnimationListener
	 */
	private class AnimationImp implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}

	}
}
