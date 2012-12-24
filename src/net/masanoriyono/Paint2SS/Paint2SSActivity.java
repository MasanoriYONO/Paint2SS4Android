package net.masanoriyono.Paint2SS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
//import android.provider.Browser;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
//import android.widget.ProgressBar;
import android.widget.Toast;

public class Paint2SSActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	public FrameLayout layout;
	// private LinearLayout layout;
	// private RelativeLayout layout;

	// private View v1;
	// private View v2;
	// private Button button1;
	public Menu mMenu;
	public EditText editText;
	public WebView webView;

	// private TextView mInstructionsText;

	private static final String TAG = "Paint2SS -> ImageManager";
	private static final String APPLICATION_NAME = "Paint2SS";
	private static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	private static final String PATH = Environment
			.getExternalStorageDirectory().toString() + "/" + APPLICATION_NAME;

	private static int editTextHeight = 72;

	static final int MY_BROWSER = 1;
	static final int MY_PAINT = 2;

	@Override
	/*
	 * public void onCreate(Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState);
	 * 
	 * RelativeLayout relativeLayout = new RelativeLayout(this);
	 * setContentView(relativeLayout);
	 * 
	 * Button button1 = new Button(this); button1.setId(1);
	 * button1.setText("Button1"); button1.setOnClickListener(new
	 * OnClickListener() { //@Override public void onClick(View v) { //write(a);
	 * Log.v("button1", "onClick" );
	 * 
	 * } });
	 * 
	 * relativeLayout.addView(button1, createParam(FP, WC));
	 * 
	 * Button button2 = new Button(this); button2.setText("Button2");
	 * button2.setId(2); RelativeLayout.LayoutParams param2 = createParam(WC,
	 * WC); param2.addRule(RelativeLayout.BELOW, 1);
	 * 
	 * relativeLayout.addView(button2, param2); }
	 * 
	 * private RelativeLayout.LayoutParams createParam(int w, int h){ return new
	 * RelativeLayout.LayoutParams(w, h); }
	 */
	/*
	 * public void onCreate(Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState);
	 * 
	 * final MyViewG vg = new MyViewG(this);
	 * 
	 * LayoutInflater inflater = this.getLayoutInflater(); LinearLayout ln =
	 * (LinearLayout) inflater.inflate(R.layout.main, null);
	 * ln.setBackgroundColor(Color.GREEN);
	 * 
	 * Button button1 = new Button(this); button1.setText("Button1");
	 * 
	 * ln.addView(button1);
	 * 
	 * vg.addView(ln);
	 * 
	 * View img = new View(this); img.setId(2);
	 * img.setBackgroundColor(Color.TRANSPARENT); // // TextView textView = new
	 * TextView(this); //
	 * textView.setText("android\nandroid\nandroid\nandroid\nandroid"); //
	 * ((ViewGroup) img).addView(textView); // // vg.addView(img);
	 * 
	 * final View v2 = new View(this); v2.setId(3);
	 * v2.setBackgroundColor(Color.TRANSPARENT); vg.addView(v2);
	 * 
	 * button1.setOnClickListener(new OnClickListener() { //@Override public
	 * void onClick(View v) { //write(a); Log.v("button1", "onClick" );
	 * v2.setBackgroundColor(Color.BLUE); } });
	 * 
	 * setContentView(vg);
	 * 
	 * 
	 * }
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// layout = new LinearLayout(this);
		// layout.setOrientation(LinearLayout.VERTICAL);
		layout = new FrameLayout(this);

		LayoutParams lp0 = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);

		setContentView(layout, lp0);
		// layout.setBackgroundColor(Color.TRANSPARENT);

		editText = new EditText(this);
		editText.setId(1);
		// 1行で表示。折り返さない。
		editText.setSingleLine();

		editText.setOnClickListener(this);
		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Log.i("MyTest", "onFocusChange " + hasFocus);
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				// フォーカスを受け取ったとき
				if (hasFocus) {
					CharSequence str = editText.getText();
					editText.setSelection(str.length());
					// ソフトキーボードを表示する
					inputMethodManager.showSoftInput(v,
							InputMethodManager.SHOW_FORCED);
				}
				// フォーカスが外れたとき
				else {
					// ソフトキーボードを閉じる
					inputMethodManager.hideSoftInputFromWindow(
							v.getWindowToken(), 0);
				}
			}
		});

		editText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.i("MyTest", "onKey " + keyCode);
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					inputMethodManager.hideSoftInputFromWindow(
							v.getWindowToken(), 0);
					Log.i("Enter",editText.getEditableText().toString());
					webView.loadUrl(editText.getEditableText().toString());
					return true;
				}
				return false;
			}
		});

		layout.addView(editText, LayoutParams.MATCH_PARENT, editTextHeight);

		webView = new WebView(this);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if (webView.getTitle() != null) {
					Paint2SSActivity.this.setTitle(webView.getTitle());
					editText.setText(webView.getUrl());
					webView.requestFocus();

					// ブックマークの履歴に残す。
					// ContentValues values = new ContentValues();
					// //1がbookmark,0が履歴
					// values.put(Browser.BookmarkColumns.BOOKMARK,"0");
					// values.put(Browser.BookmarkColumns.URL,
					// editText.getEditableText().toString());
					// values.put(Browser.BookmarkColumns.TITLE,
					// webView.getTitle());
					// getContentResolver().insert(Browser.BOOKMARKS_URI,
					// values);

					Log.i("MyTest", "onPageFinished " + url);
				}
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Paint2SSActivity.this.setTitle("Loading...");
				editText.setText(url);
				Log.i("MyTest", "onPageStarted " + url);
			}
		});
		
		webView.setVerticalScrollbarOverlay(true);
		
		WebSettings setting = webView.getSettings();
		setting.setJavaScriptEnabled(true);
		setting.setDomStorageEnabled(true);
		
		File databasePath = new File(getCacheDir(), APPLICATION_NAME);
		Log.i("onCreate CacheDir",databasePath.getPath());
		setting.setDatabasePath(databasePath.toString());
		
		setting.setGeolocationEnabled(true);
		// ここから
		setting.setSaveFormData(true);
		setting.setSavePassword(true);
		setting.setBuiltInZoomControls(true);
		setting.setSupportZoom(true);
		// これを有効にする場合はsetWebChromeClientの中でウィンドウの生成が必要。
		// setting.setSupportMultipleWindows(true);
		setting.setLoadsImagesAutomatically(true);
		setting.setLightTouchEnabled(true);
		//元に戻す時の操作性などを考えて、標準ブラウザと同じにしておく。
		// マルチタッチを有効にしたまま、zoom controlを消す
		try {
			java.lang.reflect.Field nameField = setting.getClass()
					.getDeclaredField("mBuiltInZoomControls");
			nameField.setAccessible(true);
			nameField.set(setting, false);
		} catch (Exception e) {
			e.printStackTrace();
			setting.setBuiltInZoomControls(false);
		}
		// ここまで。
		
		//ページ横幅にあわせる。
		setting.setLoadWithOverviewMode(true);
		setting.setUseWideViewPort(true);
		//プラグイン。
		setting.setPluginsEnabled(true);
		//セッティングここまで。
		
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onGeolocationPermissionsShowPrompt(String origin,
					Callback callback) {
				super.onGeolocationPermissionsShowPrompt(origin, callback);
				callback.invoke(origin, true, false);
			}
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				setProgress(newProgress * 100);
			}
			//
			// @Override
			// public boolean onCreateWindow(WebView view, boolean dialog,
			// boolean userGesture, Message resultMsg) {
			// //この中で画面を作成する。
			// /*
			// * ここでtrueを返すと元の画面を操作しても反応しなくなる
			// * おそらく、別のWebViewが開かれたものと認識しているんだと思う。
			// */
			// // return true;
			// return false;
			// }
		});

		LayoutParams layoutParams_webview = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		// //FrameLayoutではeditText.getHeight=0が返ってきた。まだ生成前なので。
		layoutParams_webview.gravity = Gravity.TOP;
		layoutParams_webview.topMargin = editTextHeight;
		layout.addView(webView, layoutParams_webview);
		
		webView.setDownloadListener(new DownloadListener() {
	        @Override
	        public void onDownloadStart(String url, String userAgent,
	                String contentDisposition, String mimetype, long contentLength) {
	            // TODO Auto-generated method stub
	           
	          Intent intent = new Intent(Intent.ACTION_VIEW);
	          intent.setType(mimetype);
	          intent.setData(Uri.parse(url));
	          startActivity(intent);
	        }
		});
	    
//		//progress bar
//		ProgressBar pBar = new ProgressBar(this);
//		LayoutParams layoutParams_pBar = new LayoutParams(
//				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//		layoutParams_pBar.gravity = Gravity.BOTTOM;
//		layoutParams_pBar.bottomMargin = 0;
//		
//		layout.addView(pBar, layoutParams_pBar);
//		////
		
		
		webView.loadUrl("http://www.google.co.jp/");
		webView.requestFocus();

		/*
		 * button1 = new Button(this); button1.setText("Button1");
		 * layout.addView
		 * (button1,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		 * button1.setId(1); button1.setOnClickListener(this);
		 * 
		 * v1 = new View(this); v1.setId(2); v1.setBackgroundColor(Color.RED);
		 * LayoutParams layoutParams_v1 = new
		 * LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		 * layoutParams_v1.gravity= Gravity.LEFT |
		 * Gravity.TOP|Gravity.RIGHT|Gravity.BOTTOM; layoutParams_v1.leftMargin
		 * =20; layoutParams_v1.topMargin =20; layoutParams_v1.rightMargin =20;
		 * layoutParams_v1.bottomMargin =20; layout.addView(v1,layoutParams_v1);
		 * v1.setOnClickListener(this);
		 * 
		 * TextView textView = new TextView(this); textView.setId(4);
		 * textView.setText("android android android android android");
		 * textView.setPadding(0, 100, 0, 150);
		 * layout.addView(textView,LayoutParams
		 * .MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		 * textView.setOnClickListener(this);
		 * 
		 * v2 = new View(this); v2.setId(3); v2.setBackgroundColor(Color.BLUE);
		 * LayoutParams layoutParams_v2 = new LayoutParams(250,250);
		 * layoutParams_v2.gravity= Gravity.LEFT | Gravity.TOP;
		 * layoutParams_v2.leftMargin =50; layoutParams_v2.topMargin =50;
		 * layout.addView(v2,layoutParams_v2); v2.setOnClickListener(this);
		 */
		/*
		 * button1.setOnClickListener(new OnClickListener() { //@Override public
		 * void onClick(View v) { //write(a); Log.v("button1", "onClick" );
		 * 
		 * v1.setBackgroundColor(0x80FF0000); v2.setBackgroundColor(0x800000FF);
		 * button1.setClickable(false); button1.setFocusable(false);
		 * button1.setVisibility(View.INVISIBLE);
		 * 
		 * v1.setVisibility(View.VISIBLE); v1.setFocusable(true);
		 * v1.setClickable(true); } });
		 * 
		 * v1.setOnTouchListener(new OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View arg0, MotionEvent arg1) { //
		 * TODO Auto-generated method stub button1.setFocusable(true);
		 * button1.setClickable(true); button1.setVisibility(View.VISIBLE);
		 * 
		 * v1.setClickable(false); v1.setFocusable(false);
		 * v1.setVisibility(View.INVISIBLE);
		 * v1.setBackgroundColor(Color.TRANSPARENT);
		 * v2.setBackgroundColor(Color.TRANSPARENT);
		 * 
		 * Log.v("v1", "OnTouchListener" ); return false; }
		 * 
		 * });
		 */

	}

	@Override
	public void onClick(View v) {
		ViewGroup p = (ViewGroup) v.getParent();
		// if(v.getId() == 1){
		// return;
		// }
		// p.removeView(v);
		// p.addView(v, 0);
		// v.setEnabled(false);
		Log.i("editText", "height:" + editText.getHeight());
		Log.v("ViewGroup", "onClick" + v.getId() + ":" + p.getChildCount());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Hold on to this
		mMenu = menu;

		// Inflate the currently selected menu XML resource.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.title_only, menu);

		// Disable the spinner since we've already created the menu and the user
		// can no longer pick a different menu XML.
		// mSpinner.setEnabled(false);

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		Log.i("onActivityResult", "requestCode:" + requestCode + " resultCode:"
				+ resultCode + intent);
		// super.onActivityResult(requestCode, resultCode, intent);
		// 返り値の取得
		if (requestCode == MY_BROWSER) {
			if (resultCode == RESULT_OK) {
				// Success
				// String res_url =
				// intent.getExtras().getString("com.android.browser.CombinedBookmarkHistoryActivity");
				Log.i("onActivityResult",
						"MY_BROWSER intent:" + intent.getAction());
				webView.loadUrl(intent.getAction());
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
		// else if(requestCode == MY_PAINT){
		// if (resultCode == RESULT_OK) {
		// Log.i("onActivityResult", "MY_PAINT intent:" + intent.getAction());
		//
		// }
		// else{
		// // Handle cancel
		// Log.i("onActivityResult", "intent throw 2 MY_PAINT failure.:" +
		// resultCode);
		// }
		// }
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean f_res;
		switch (item.getItemId()) {
		// For "Title only": Examples of matching an ID with one assigned in
		// the XML
		case R.id.menu_upper_left:

			Log.v("menu_upper_left", "reload");

			webView.reload();

			return true;

		case R.id.menu_upper_center:

			Log.v("menu_upper_center", "back");
			if (webView.canGoBack()) {
				webView.goBack();
			}

			return true;

		case R.id.menu_upper_right:
			if (webView.canGoForward()) {
				webView.goForward();
			}
			Log.v("menu_upper_right", "forward");

			return true;

		case R.id.menu_lower_left:

			long dateTaken = System.currentTimeMillis();
			String filename = createName(dateTaken) + ".png";

			Log.v("menu_lower_left", PATH + " " + filename);

			f_res = saveToFile(PATH, filename, dateTaken);
			if (f_res) {
				Toast.makeText(this, "Save to gallery", Toast.LENGTH_SHORT)
						.show();
				// インテントのインスタンス生成
				// 起動可能なIntent
				Intent intent_search = new Intent(Intent.ACTION_MAIN, null);
				// デスクトップから可能なIntent(つまり通常のアプリケーション)
				intent_search.addCategory(Intent.CATEGORY_LAUNCHER);
				// 通常のアプリケーションのリストを取得
				PackageManager manager = getPackageManager();
				List<ResolveInfo> infoes = manager.queryIntentActivities(
						intent_search, 0);
				Boolean f_exist = false; 
				for (int i = 0; i < infoes.size(); i++) {
					ResolveInfo a_info = infoes.get(i);
					//Log.v("menu_lower_left", "name:" + a_info.nonLocalizedLabel.toString());
					Log.v("menu_lower_left", "judge:" + a_info.toString().contains("net.masanoriyono.Paint.PaintActivity"));
					//if(a_info.loadLabel(manager).equals("Paint")){
					if(a_info.toString().contains("net.masanoriyono.Paint.PaintActivity")){
						f_exist= true;
						Log.v("menu_lower_left", "name:" + a_info.loadLabel(manager));
					}
				}
				if(f_exist){
					Intent intent2Paint = new Intent();
					intent2Paint.setClassName("net.masanoriyono.Paint",
					 	"net.masanoriyono.Paint.PaintActivity");
					
					// //この書き方でエラーにはなってないけれど、調べてみるとBitmapの画像が粗いらしい。
					intent2Paint.setAction("bitmap");
					intent2Paint.putExtra("file",(PATH + "/" + filename).toString());
					//なのでファイル名渡しにしようと思う。時間があればデータそのものを渡す方法もいいかも。
					// startActivityForResult(intent2Paint,MY_PAINT);
					startActivity(intent2Paint);
				}
			}
			Log.v("menu_lower_left", "save");

			return true;

		case R.id.menu_lower_center:
			// 起動可能なIntent
			Intent intent_search = new Intent(Intent.ACTION_MAIN, null);
			// デスクトップから可能なIntent(つまり通常のアプリケーション)
			intent_search.addCategory(Intent.CATEGORY_BROWSABLE);
			// 通常のアプリケーションのリストを取得
			PackageManager manager = getPackageManager();
			List<ResolveInfo> infoes = manager.queryIntentActivities(
					intent_search, 0);
			Boolean f_exist = false; 
			for (int i = 0; i < infoes.size(); i++) {
				ResolveInfo a_info = infoes.get(i);
				
//				Log.v("menu_lower_center", "name:" + a_info.toString());
				
//				Log.v("menu_lower_center", "judge:" + a_info.toString().contains("com.android.browser.BrowserActivity"));
				if(a_info.toString().contains("com.android.browser")){
//					Log.v("menu_lower_center", "name:" + a_info.toString());
					f_exist= true;
//					Log.v("menu_lower_center", "name:" + a_info.loadLabel(manager));
				}
			}
			
			if(f_exist){
				// インテントのインスタンス生成
				Intent intent = new Intent();
				// パッケージ名, クラス名をセット
				intent.setClassName("com.android.browser",
						"com.android.browser.CombinedBookmarkHistoryActivity");
				// 追加の確認ダイアログを表示させずに追加する方法。
				// ContentValues values = new ContentValues();
				// //1がbookmark,0が履歴
				// values.put(Browser.BookmarkColumns.BOOKMARK,0);
				// values.put(Browser.BookmarkColumns.URL,
				// editText.getEditableText().toString());
				// values.put(Browser.BookmarkColumns.TITLE, webView.getTitle());
				// getContentResolver().insert(Browser.BOOKMARKS_URI, values);
				// ここまで。
	
				// ダイアログを表示する方法。思ったタイミングでは表示されない。
				// Browser.saveBookmark(this,webView.getTitle(),editText.getEditableText().toString());
	
				// アプリを起動
				startActivityForResult(intent, MY_BROWSER);
				
//				Toast.makeText(this, "Default Browser Bookmarks found."
//						, Toast.LENGTH_SHORT).show();
			}
			else{
				Toast.makeText(this, "Default Browser Bookmarks not found."
						, Toast.LENGTH_LONG).show();
			}
			return true;

		case R.id.menu_lower_right:
			finish();
			return true;
			// // For "Groups": Toggle visibility of grouped menu items with
			// // nongrouped menu items
			// case R.id.browser_visibility:
			// // The refresh item is part of the browser group
			// final boolean shouldShowBrowser =
			// !mMenu.findItem(R.id.refresh).isVisible();
			// mMenu.setGroupVisible(R.id.browser, shouldShowBrowser);
			// break;
			//
			// case R.id.email_visibility:
			// // The reply item is part of the email group
			// final boolean shouldShowEmail =
			// !mMenu.findItem(R.id.reply).isVisible();
			// mMenu.setGroupVisible(R.id.email, shouldShowEmail);
			// break;
			//
			// Generic catch all for all the other menu resources
		default:
			// Don't toast text when a submenu is clicked
			// if (!item.hasSubMenu()) {
			// Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
			// return true;
			// }
			break;
		}

		return false;
	}

	private String createName(long dateTaken) {
		return DateFormat.format("yyyyMMdd_kkmmss", dateTaken).toString();
	}

	public boolean saveToFile(String directory, String filename, long dateTaken) {
		// これによりキャッシュを利用しない。
		layout.setDrawingCacheEnabled(false);
		// キャッシュを利用してしまうので前に表示していた画面が保存されてしまう。
		// かといって、コメントすると下のメソッドでエラーになってしまう。
		layout.setDrawingCacheEnabled(true);
		Bitmap bitmap0 = Bitmap.createBitmap(layout.getDrawingCache());

		OutputStream outputStream = null;
		try {
			File dir = new File(directory + "/");
			if (!dir.exists()) {
				dir.mkdirs();
				Log.d(TAG, dir.toString() + " create");
			}

			String filePath = directory + "/" + filename;

			File file = new File(directory + "/" + filename);
			if (file.createNewFile()) {
				outputStream = new FileOutputStream(file);
				if (bitmap0 != null) {
					// source.compress(CompressFormat.JPEG, 75, outputStream);
					bitmap0.compress(CompressFormat.PNG, 100, outputStream);
					// } else {
					// outputStream.write(jpegData);

					// ギャラリーへの登録。
					ContentResolver contentResolver = getContentResolver();

					ContentValues values = new ContentValues();
					values.put(Images.Media.TITLE, filename);
					values.put(Images.Media.DISPLAY_NAME, filename);
					values.put(Images.Media.DATE_TAKEN, dateTaken);
					values.put(Images.Media.MIME_TYPE, "image/png");
					values.put(Images.Media.DATA, filePath);

					contentResolver.insert(IMAGE_URI, values);

					Log.d(TAG, file.toString() + " write");

					return true;
				}
			}

		} catch (FileNotFoundException ex) {
			Log.w(TAG, ex);
			return false;
		} catch (IOException ex) {
			Log.w(TAG, ex);
			return false;
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Throwable t) {
				}
			}
		}
		return false;

		// try {
		// FileOutputStream out = openFileOutput(filename, MODE_PRIVATE);
		//
		// //layout.setDrawingCacheEnabled(false);
		//
		//
		//
		// /*
		// * 画像を縮尺して保存する場合はコメントアウト
		// */
		// // float scale = 0.25f;
		// //
		// // int width = bitmap0.getWidth();
		// // int height = bitmap0.getHeight();
		// //
		// // Matrix matrix = new Matrix();
		// // matrix.postScale(scale, scale);
		// //
		// // Bitmap bitmap = Bitmap.createBitmap(bitmap0, 0, 0, width, height,
		// matrix, true);
		// // bitmap.compress(CompressFormat.PNG, 100, out);
		//
		// out.close();
		//
		// } catch (FileNotFoundException e) {
		// } catch (IOException e) {
		// }
	}

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// Log.d("TouchEvent", "X:" + event.getX() + ",Y:" + event.getY());
	// return super.onTouchEvent(event);
	// }

}