package me.ninjachen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.view.ViewPropertyAnimator;
import me.ninjachen.anim.PM25Anim;
import me.ninjachen.util.NetworkUtils;
import me.ninjachen.util.PM25Geocoder;
import me.ninjachen.util.PM25Provider;

/**
 * 主界面，完成各种操作。
 * 
 */
@SuppressLint("SimpleDateFormat")
public class PM25Activity extends Activity {
	private static final String LogTag = "pm25";
	private static Map<String, String> maps = new HashMap<String, String>();
	private static float x;
	private Boolean canPrint = Boolean.valueOf(false);
	private Boolean canRip = Boolean.valueOf(false);
	private int cityIndex = 0;
	private Boolean isCitySwitching = Boolean.valueOf(false);
	private Boolean isFeedback = Boolean.valueOf(false);
	private Boolean isFirst = Boolean.valueOf(true);
	private TextView mAQI;
	private Animation mAlphaAnim = new AlphaAnimation(0.1F, 1.0F);
	private ViewGroup mBodyLayout;
	private Animator mBodyReset;
	private Animator mBodyTranslate;
	private TextView mBy;
	private TextView mCity;
	private Animator mFeedbackSended;
	private ListView mListView;
	private ImageButton mNext;
	private TextView mPM25;
	private TextView mPaperAQI;
	private TextView mPaperAQIDesc;
	private TextView mPaperArea;
	private TextView mPaperDivider1;
	private TextView mPaperDivider2;
	private TextView mPaperFeedbackDesc;
	private EditText mPaperFeedbackMessage;
	private TextView mPaperFeedbackTitle;
	private ViewGroup mPaperLayout;
	private ViewGroup mPaperListLayout;
	private TextView mPaperProposal;
	private TextView mPaperQuality;
	private TextView mPaperShareFrom;
	private TextView mPaperTime;
	private TextView mPaperTitle;
	private Animator mPaperUpAll;
	private Animator mPaperUpTitle;
	private ImageButton mPrint;
	private MediaPlayer mPrintPlayer;
	private MediaPlayer mRipPlayer;
	private PM25CitySetting mSetting;
	private ImageButton mShare;
	private TextView mStudio;
	private TextView mValue;
	private ViewGroup mrl;
	private String vAQI;
	private String vBeforeCity;
	private String vCity;
	private String vPM25;
	private String vQuality;
	private String vShare = "今日%s空气质量指数(AQI)：%s，等级【%s】；PM2.5 浓度值：%s μg/m3。%s。（请关注博客：http://blog.csdn.net/weidi1989 ）";
	private String vTime;

	static {
		maps.put("优", "空气特别好，尽情活动吧");
		maps.put("良", "仅对特别敏感患者轻微影响");
		maps.put("轻度污染", "易感人群勿长期户外活动");
		maps.put("中度污染", "可能影响心脏、呼吸系统");
		maps.put("重度污染", "心脏病和肺病患者症状加剧");
		maps.put("严重污染", "所有人尽量避免户外活动");
	}

	private void checkAccessLocation() {
		new PM25Geocoder(this).check();
	}

	@SuppressLint("NewApi")
	private void disablePaperFeedback() {
		mPaperTitle.setVisibility(View.VISIBLE);
		mPaperAQIDesc.setVisibility(View.VISIBLE);
		mPaperArea.setVisibility(View.VISIBLE);
		mPaperAQI.setVisibility(View.VISIBLE);
		mPaperQuality.setVisibility(View.VISIBLE);
		mPaperTime.setVisibility(View.VISIBLE);
		mPaperProposal.setVisibility(View.VISIBLE);
		mPaperListLayout.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.VISIBLE);
		mPaperShareFrom.setVisibility(View.VISIBLE);
		mPaperDivider1.setVisibility(View.VISIBLE);
		mPaperDivider2.setVisibility(View.VISIBLE);
		mPaperLayout.setAlpha(1.0F);
		mPaperFeedbackMessage.setVisibility(View.GONE);
		mPaperFeedbackTitle.setVisibility(View.GONE);
		mPaperFeedbackDesc.setVisibility(View.GONE);
	}

//	private void enableAbout() {
//		mAQI.setVisibility(View.GONE);
//		mPM25.setVisibility(View.GONE);
//		mValue.setVisibility(View.GONE);
//		mBy.setVisibility(View.VISIBLE);
//		mStudio.setVisibility(View.VISIBLE);
//		vBeforeCity = this.mCity.getText().toString();
//		mCity.setText("http://blog.csdn.net/weidi1989");
//	}

	private void enablePaperFeedback() {
		mPaperTitle.setVisibility(View.GONE);
		mPaperAQIDesc.setVisibility(View.GONE);
		mPaperArea.setVisibility(View.GONE);
		mPaperAQI.setVisibility(View.GONE);
		mPaperQuality.setVisibility(View.GONE);
		mPaperTime.setVisibility(View.GONE);
		mPaperProposal.setVisibility(View.GONE);
		mPaperListLayout.setVisibility(View.GONE);
		mListView.setVisibility(View.GONE);
		mPaperShareFrom.setVisibility(View.GONE);
		mPaperDivider1.setVisibility(View.GONE);
		mPaperDivider2.setVisibility(View.GONE);
		mPaperFeedbackMessage.setVisibility(View.VISIBLE);
		mPaperFeedbackTitle.setVisibility(View.VISIBLE);
		mPaperFeedbackDesc.setVisibility(View.VISIBLE);
	}

	private void initFontface() {
		Typeface typeface = Typeface.createFromAsset(getAssets(),
				"fonts/LCD.ttf");
		mCity.setTypeface(typeface);
		mAQI.setTypeface(typeface);
		mPM25.setTypeface(typeface);
		mValue.setTypeface(typeface);
		mBy.setTypeface(typeface);
		mStudio.setTypeface(typeface);
	}

	private void initPlayer() {
		mPrintPlayer = MediaPlayer.create(this, R.raw.print);
		mPrintPlayer.setLooping(true);
		mRipPlayer = MediaPlayer.create(this, R.raw.paper_rip);
	}

	private void initViewAndLayout() {
		mBodyLayout = ((ViewGroup) findViewById(R.id.layout_main));
		mPaperLayout = ((ViewGroup) findViewById(R.id.layout_ticket_out));
		mrl = ((ViewGroup) findViewById(R.id.rl));
		mPaperListLayout = ((ViewGroup) findViewById(R.id.layout_paper_list_header));
		mCity = ((TextView) findViewById(R.id.txt_city_value));
		mAQI = ((TextView) findViewById(R.id.txt_aqi_desc));
		mPM25 = ((TextView) findViewById(R.id.txt_pm25_desc));
		mValue = ((TextView) findViewById(R.id.txt_value));
		mShare = ((ImageButton) findViewById(R.id.btn_paper_share));
		mNext = ((ImageButton) findViewById(R.id.btn_next));
		mPrint = ((ImageButton) findViewById(R.id.btn_print));
		mBy = ((TextView) findViewById(R.id.txt_led_by));
		mStudio = ((TextView) findViewById(R.id.txt_led_studio));
		mListView = ((ListView) mPaperLayout.findViewById(R.id.paper_list));
		mPaperAQI = ((TextView) mPaperLayout
				.findViewById(R.id.txt_paper_aqi_value));
		mPaperArea = ((TextView) mPaperLayout.findViewById(R.id.txt_paper_area));
		mPaperTime = ((TextView) mPaperLayout
				.findViewById(R.id.txt_paper_datetime));
		mPaperQuality = ((TextView) mPaperLayout
				.findViewById(R.id.txt_paper_quality));
		mPaperProposal = ((TextView) mPaperLayout
				.findViewById(R.id.txt_paper_proposal));
		mPaperTitle = ((TextView) mPaperLayout
				.findViewById(R.id.txt_paper_report));
		mPaperAQIDesc = ((TextView) mPaperLayout
				.findViewById(R.id.txt_paper_aqi_desc));
		mPaperShareFrom = ((TextView) mPaperLayout
				.findViewById(R.id.txt_share_from));
		mPaperDivider1 = ((TextView) mPaperLayout.findViewById(R.id.divide1));
		mPaperDivider2 = ((TextView) mPaperLayout.findViewById(R.id.divide2));
		mPaperFeedbackMessage = ((EditText) mPaperLayout
				.findViewById(R.id.txt_paper_feedback_message));
		mPaperFeedbackTitle = ((TextView) mPaperLayout
				.findViewById(R.id.txt_paper_feedback_title));
		mPaperFeedbackDesc = ((TextView) mPaperLayout
				.findViewById(R.id.txt_paper_feedback_desc));
	}

	private void postFeedback(String feedbackMessage) {
		Intent email = new Intent(Intent.ACTION_SENDTO);
		email.setData(Uri.parse("mailto:way.ping.li@gmail.com"));
		email.putExtra(Intent.EXTRA_SUBJECT, "pm25反馈");
		email.putExtra(Intent.EXTRA_TEXT, feedbackMessage);
		startActivity(email);
		// new PostTask(
		// "http://www.pm25.in/api/update/feedback.json?token=4esfG6UEhGzNkbszfjAp&city")
		// .execute(new String[] { feedbackMessage });
		mPaperFeedbackMessage.setText("");
	}

	private void selectCity() {
		if (cityIndex < API.cities.length) {
			mCity.setText(wrapFont(API.cities[cityIndex]));
			cityIndex = (1 + cityIndex);
			return;
		}
		cityIndex = 1;
	}

	private void shareTo() {
		new File(getFilesDir(), "share.png").deleteOnExit();
		try {
			FileOutputStream localFileOutputStream = openFileOutput(
					"share.png", 1);
			this.mPaperLayout.setDrawingCacheEnabled(true);
			this.mPaperLayout.getDrawingCache().compress(
					Bitmap.CompressFormat.PNG, 100, localFileOutputStream);
			File localFile = new File(getFilesDir(), "share.png");
			Intent intent = new Intent("android.intent.action.SEND");
			intent.setType("image/*");
			Object[] arrayOfObject1 = new Object[5];
			arrayOfObject1[0] = this.vCity;
			arrayOfObject1[1] = this.vAQI;
			arrayOfObject1[2] = this.vQuality;
			arrayOfObject1[3] = this.vPM25;
			arrayOfObject1[4] = maps.get(this.vQuality);
			intent.putExtra("sms_body", String.format(vShare, arrayOfObject1));
			Object[] arrayOfObject2 = new Object[5];
			arrayOfObject2[0] = this.vCity;
			arrayOfObject2[1] = this.vAQI;
			arrayOfObject2[2] = this.vQuality;
			arrayOfObject2[3] = this.vPM25;
			arrayOfObject2[4] = maps.get(this.vQuality);
			intent.putExtra("android.intent.extra.TEXT",
					String.format(vShare, arrayOfObject2));
			intent.putExtra("android.intent.extra.STREAM",
					Uri.fromFile(localFile));
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(Intent.createChooser(intent, "分享到"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void startInfoShow() {
		mAQI.setAnimation(mAlphaAnim);
		mPM25.setAnimation(mAlphaAnim);
		mValue.setAnimation(mAlphaAnim);
		mAlphaAnim.setRepeatCount(1);
		mAlphaAnim.setDuration(300L);
		mAlphaAnim.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				mPM25.setText("    PM2.5 /H");
				mAQI.setText(" = AQI ");
				mValue.setText(vAQI);
				canPrint = true;
				// PM25Activity.access$1702(PM25Activity.this,
				// Boolean.valueOf(true));
			}

			public void onAnimationRepeat(Animation paramAnonymousAnimation) {
			}

			public void onAnimationStart(Animation paramAnonymousAnimation) {
			}
		});
		mAlphaAnim.start();
	}

	private void startPaperAnimation() {
		RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams) mPaperLayout
				.getLayoutParams();
		Object[] objects1 = new Object[1];
		objects1[0] = Integer.valueOf(localLayoutParams.height);
		Log.d(LogTag, String.format("paperLP.height %s", objects1));
		localLayoutParams.height = getResources().getDimensionPixelSize(
				R.dimen.paper_height);
		Object[] objects2 = new Object[1];
		objects2[0] = Integer.valueOf(localLayoutParams.height);
		Log.d(LogTag, String.format("paperLP.height %s", objects2));
		this.mPaperLayout.setLayoutParams(localLayoutParams);
		AnimatorSet animatorSet = new AnimatorSet();
		Animator[] arrayOfAnimator = new Animator[3];
		arrayOfAnimator[0] = mPaperUpTitle;
		arrayOfAnimator[1] = mBodyTranslate;
		arrayOfAnimator[2] = mPaperUpAll;
		animatorSet.playSequentially(arrayOfAnimator);
		animatorSet.addListener(new Animator.AnimatorListener() {
			public void onAnimationCancel(Animator paramAnonymousAnimator) {
			}

			public void onAnimationEnd(Animator paramAnonymousAnimator) {
				if (!isFeedback.booleanValue())
					mShare.setVisibility(View.VISIBLE);
				canRip = true;
			}

			public void onAnimationRepeat(Animator paramAnonymousAnimator) {
			}

			public void onAnimationStart(Animator paramAnonymousAnimator) {
			}
		});
		animatorSet.start();
	}

	private void updateAQI_PM25(String city) {
		if (NetworkUtils.getNetworkState(this) == NetworkUtils.NETWORN_NONE) {
			Toast.makeText(this, R.string.net_wrok_error, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		Log.d(LogTag, String.format("update city : %s", new Object[] { city }));
		vAQI = "88888 ";
		mValue.setText(vAQI);
		mAQI.setVisibility(View.VISIBLE);
		mPM25.setVisibility(View.VISIBLE);
		mValue.setVisibility(View.VISIBLE);
		if (city.equalsIgnoreCase("auto"))
			city = mSetting.getAutoCity();
		new PM25Provider().request(new PM25Provider.PM25Info() {
			public void onInfo(List<PM25Provider.PM25> pm25List) {
				if (pm25List == null) {
					Toast.makeText(PM25Activity.this, R.string.get_pm25_fail,
							Toast.LENGTH_SHORT).show();
					return;
				}
				PM25Provider.PM25 pm25 = (PM25Provider.PM25) pm25List.get(-1
						+ pm25List.size());
				vAQI = wrapFont(pm25.aqi);
				vPM25 = wrapFont(pm25.pm2_5);
				mPaperAQI.setText(vAQI);
				try {
					Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
							.parse(pm25.time_point);
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy年MM月dd日 HH:mm");
					vTime = sdf.format(date);
					mPaperTime.setText("发布时间:" + vTime);
					vCity = pm25.area;
					mPaperArea.setText(vCity);
					vQuality = pm25.quality;
					mPaperQuality.setText("等级:" + vQuality);
					mPaperProposal.setText("建议:" + (String) maps.get(vQuality));
					mValue.setText("88888 ");
					mListView.setAdapter(new PM25Adapter(PM25Activity.this,
							pm25List));
					startInfoShow();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}, city);
	}

	private String wrapFont(String paramString) {
		return paramString + " ";
	}

	@SuppressLint("NewApi")
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.main);
		checkAccessLocation();
		mSetting = new PM25CitySetting(this);
		initViewAndLayout();
		initFontface();
		initPlayer();
		final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mrl.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@SuppressWarnings("deprecation")
					public void onGlobalLayout() {
						int i = PM25Activity.this.getWindow()
								.findViewById(R.id.rl).getHeight();
						Log.d(LogTag, "device height :"
								+ PM25Activity.this.getWindow().getDecorView()
										.getHeight());
						Log.d(LogTag, "onGlobalLayout:" + i);
						mrl.setLayoutParams(new FrameLayout.LayoutParams(-1,
								i * 3));
						PM25Activity.this.mrl.setPadding(0, -i, 0, i);
						RelativeLayout.LayoutParams bodyLayoutParams = (RelativeLayout.LayoutParams) PM25Activity.this.mBodyLayout
								.getLayoutParams();
						bodyLayoutParams.height = (i * 2);
						bodyLayoutParams.setMargins(0, 0, 0, -i);
						PM25Activity.this.mBodyLayout
								.setLayoutParams(bodyLayoutParams);
						RelativeLayout.LayoutParams paperLayoutParams = (RelativeLayout.LayoutParams) PM25Activity.this.mPaperLayout
								.getLayoutParams();
						paperLayoutParams.height = (i * 2);
						paperLayoutParams
								.setMargins(
										0,
										PM25Activity.this
												.getResources()
												.getDimensionPixelSize(
														R.dimen.main_paper_margin_top),
										0,
										PM25Activity.this
												.getResources()
												.getDimensionPixelSize(
														R.dimen.main_paper_margin_bottom));
						mPaperLayout.setLayoutParams(paperLayoutParams);
						mrl.getViewTreeObserver().removeGlobalOnLayoutListener(
								this);
					}
				});
		mBodyTranslate = PM25Anim.down(this.mBodyLayout, getResources()
				.getDimension(R.dimen.paper_anim_down));
		mPaperUpTitle = PM25Anim.up(this.mPaperLayout, getResources()
				.getDimension(R.dimen.paper_anim_up_one));
		mPaperUpAll = PM25Anim.up(this.mPaperLayout, getResources()
				.getDimension(R.dimen.paper_anim_up_one), getResources()
				.getDimension(R.dimen.paper_anim_up_two));
		mFeedbackSended = PM25Anim.upAndVanish(this.mPaperLayout,
				getResources().getDimension(R.dimen.paper_anim_up_two));
		mBodyReset = PM25Anim.up(this.mBodyLayout,
				getResources().getDimension(R.dimen.paper_anim_down), 0.0F);
		mFeedbackSended.addListener(new Animator.AnimatorListener() {
			public void onAnimationCancel(Animator paramAnonymousAnimator) {
			}

			public void onAnimationEnd(Animator paramAnonymousAnimator) {
				mBodyReset.start();
				disablePaperFeedback();
			}

			public void onAnimationRepeat(Animator paramAnonymousAnimator) {
			}

			public void onAnimationStart(Animator paramAnonymousAnimator) {
			}
		});
		Animator.AnimatorListener local3 = new Animator.AnimatorListener() {
			public void onAnimationCancel(Animator paramAnonymousAnimator) {
			}

			public void onAnimationEnd(Animator paramAnonymousAnimator) {
				mPrintPlayer.pause();
			}

			public void onAnimationRepeat(Animator paramAnonymousAnimator) {
			}

			public void onAnimationStart(Animator paramAnonymousAnimator) {
				mPrintPlayer.start();
			}
		};
		mPaperUpAll.addListener(local3);
		mPaperUpTitle.addListener(local3);
		mNext.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				if (!isCitySwitching.booleanValue()) {
					if (PM25Activity.this.isFirst.booleanValue()) {
						isFirst = false;
						mPM25.setText(" = PM2.5 /H ");
						mAQI.setText("    AQI ");
						mValue.setText(vPM25);
						return;
					}
					isFirst = true;
					mPM25.setText("    PM2.5 /H ");
					mAQI.setText(" = AQI ");
					mValue.setText(vAQI);
					return;
				}
				selectCity();
			}
		});
		mNext.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View paramAnonymousView) {
				if (isCitySwitching.booleanValue()) {
					isCitySwitching = false;
					mAQI.setVisibility(View.VISIBLE);
					mPM25.setVisibility(View.VISIBLE);
					mValue.setVisibility(View.VISIBLE);
					mAlphaAnim.cancel();
					mCity.setAnimation(null);
					mCity.setText(vBeforeCity);
					return true;
				}
				isCitySwitching = true;
				canPrint = false;
				mAQI.setVisibility(View.GONE);
				mPM25.setVisibility(View.GONE);
				mValue.setVisibility(View.GONE);
				vBeforeCity = mCity.getText().toString().trim();
				cityIndex = 1;// 位置重置
				mCity.setText(API.cities[0]);
				mAlphaAnim.setDuration(500L);
				mAlphaAnim.setRepeatCount(-1);
				mCity.setAnimation(mAlphaAnim);
				mAlphaAnim.start();
				return true;
			}
		});
		mAlphaAnim.setDuration(100L);
		mAlphaAnim.setRepeatCount(-1);
		mCity.setAnimation(this.mAlphaAnim);
		mPrint.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				if ((!isCitySwitching.booleanValue())
						&& (canPrint.booleanValue())) {
					startPaperAnimation();
					// PM25Activity.access$2102(PM25Activity.this,
					// Boolean.valueOf(false));
					isFeedback = false;
					canPrint = false;
					return;
				}
				isCitySwitching = false;
				String city = mCity.getText().toString().trim();
				if (city.equalsIgnoreCase("change city")) {
					// PM25Activity.access$702(PM25Activity.this,
					// Boolean.valueOf(false));
					mAQI.setVisibility(View.VISIBLE);
					mPM25.setVisibility(View.VISIBLE);
					mValue.setVisibility(View.VISIBLE);
					mCity.setText(vBeforeCity);
					mAlphaAnim.cancel();
					mCity.setAnimation(null);
					return;
				} else if (city.equalsIgnoreCase("auto")) {
					mSetting.setCity("auto");
					// mCity.setText(wrapFont(mSetting.getAutoCity()));
					requesLocation();
				} else {
					mSetting.setCity(city);
					mAlphaAnim.cancel();
					mCity.setAnimation(null);
					updateAQI_PM25(mSetting.getCity());
					// PM25Activity.access$702(PM25Activity.this,
					// Boolean.valueOf(false));
					// PM25Activity.access$1902(PM25Activity.this, 0);
				}
				return;
			}
		});
		mPrint.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View view) {
				isFeedback = true;
				enablePaperFeedback();
				startPaperAnimation();
				return true;
			}
		});
		mShare.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				shareTo();
			}
		});
		mPaperLayout.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			public boolean onTouch(final View view, MotionEvent event) {
				if (canRip.booleanValue()) {
					mShare.setVisibility(View.GONE);
					if (Build.VERSION.SDK_INT >= 11) {// 只有3.0以上的api才能使用。
						if (event.getAction() == MotionEvent.ACTION_DOWN)
							x = event.getX();
						if (event.getAction() == MotionEvent.ACTION_MOVE) {
							if (view.getRotation() > 0.0F)
								view.setPivotX(view.getWidth());
							if (view.getRotation() < 0.0F)
								view.setPivotX(0.0F);
							view.setPivotY(view.getHeight());
							view.setRotation(view.getRotation()
									+ (event.getX() - x) / 50.0F);
						}
						if (event.getAction() == MotionEvent.ACTION_UP) {
							Object[] objects = new Object[1];
							objects[0] = Float.valueOf(view.getRotation());
							Log.d(LogTag,
									String.format("getRotation : %s", objects));
							Animator.AnimatorListener ripAnimal = new Animator.AnimatorListener() {
								public void onAnimationCancel(
										Animator paramAnonymous2Animator) {
								}

								public void onAnimationEnd(
										Animator paramAnonymous2Animator) {
									mBodyReset.start();
									view.setTranslationY(0.0F);
									view.setRotation(0.0F);
									view.setAlpha(1.0F);
									canRip = false;
								}

								public void onAnimationRepeat(
										Animator paramAnonymous2Animator) {
								}

								public void onAnimationStart(
										Animator paramAnonymous2Animator) {
									mRipPlayer.start();
								}
							};
							if (Math.abs(view.getRotation()) <= 3.0F)
								view.setRotation(0.0F);
							if (view.getRotation() > 3.0F) {
								ViewPropertyAnimator.animate(view)
										.setDuration(1000L).alpha(0.0F)
										.rotation(90.0F).setListener(ripAnimal)
										.start();
							} else if (view.getRotation() < -3.0F) {
								ViewPropertyAnimator.animate(view)
										.setDuration(1000L).alpha(0.0F)
										.rotation(-90.0F)
										.setListener(ripAnimal).start();
							} else {
								if (!isFeedback.booleanValue())
									mShare.setVisibility(View.VISIBLE);
							}
						}
					}
				}
				return false;
			}
		});
		mPaperFeedbackMessage.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if ((KeyEvent.KEYCODE_ENTER == keyCode)
						&& (event.getAction() == KeyEvent.ACTION_DOWN)) {
					Log.d(PM25Activity.LogTag, "send to");
					String feedbackMessage = mPaperFeedbackMessage.getText()
							.toString();
					Log.d(LogTag, feedbackMessage);
					if (TextUtils.isEmpty(feedbackMessage)
							|| feedbackMessage.length() < 5) {
						Toast.makeText(PM25Activity.this,
								R.string.feedback_can_not_null,
								Toast.LENGTH_SHORT).show();
						return true;
					}
					postFeedback(feedbackMessage);
					inputMethodManager.hideSoftInputFromWindow(
							view.getApplicationWindowToken(), 2);
					mFeedbackSended.start();
					return true;
				}
				return false;
			}
		});
		mBodyReset.addListener(new Animator.AnimatorListener() {
			public void onAnimationCancel(Animator animator) {
			}

			public void onAnimationEnd(Animator animator) {
				disablePaperFeedback();
			}

			public void onAnimationRepeat(Animator animator) {
			}

			public void onAnimationStart(Animator animator) {
			}
		});
	}

	public void onPause() {
		super.onPause();
	}

	protected void onResume() {
		super.onResume();
		if (mSetting.getCity().equalsIgnoreCase("auto")) {// 如果需要自动定位
			if (mSetting.getAutoCity().equals("")) {
				requesLocation();
			} else {
				String city = mSetting.getAutoCity();
				mCity.setText(city);
				updateAQI_PM25(city);
				mAlphaAnim.cancel();
				mCity.setAnimation(null);
			}
		} else {
			String city = mSetting.getCity();
			mCity.setText(city);
			updateAQI_PM25(city);
			mAlphaAnim.cancel();
			mCity.setAnimation(null);
		}

	}

	/**
	 * 请求获取位置
	 */
	private void requesLocation() {
		if (NetworkUtils.getNetworkState(this) == NetworkUtils.NETWORN_NONE) {
			Toast.makeText(this, R.string.net_wrok_error, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		new PM25Geocoder(this)
				.requestLocalCityName(new PM25Geocoder.CityNameStatus() {
					public void detecting() {
						mCity.setText(R.string.txt_detecting);
						mAlphaAnim.start();
					}

					public void update(String city) {
						if (city == null) {// 未定位到城市,提示一下，返回
							Toast.makeText(PM25Activity.this,
									R.string.get_location_fail,
									Toast.LENGTH_SHORT).show();
							mCity.setText(R.string.txt_location_fail);
							mCity.setAnimation(null);
							mAlphaAnim.cancel();
							return;
						}
						Log.d(LogTag, "geo coder get city name:" + city);
						mSetting.setAutoCity(city);
						mCity.setText(wrapFont(city));
						updateAQI_PM25(city);
						mAlphaAnim.cancel();
						mCity.setAnimation(null);
					}
				});
	}

	/**
	 * 连续按两次返回键就退出
	 */
	private long firstTime;

	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() - firstTime < 3000) {
			finish();
		} else {
			firstTime = System.currentTimeMillis();
			Toast.makeText(this, R.string.press_again_exit, Toast.LENGTH_SHORT)
					.show();
		}
	}

}