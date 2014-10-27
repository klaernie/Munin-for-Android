package com.chteuchteu.munin.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.MaterialMenuIcon;
import com.chteuchteu.munin.MuninFoo;
import com.chteuchteu.munin.R;
import com.chteuchteu.munin.hlpr.DrawerHelper;
import com.chteuchteu.munin.hlpr.Util;
import com.chteuchteu.munin.hlpr.Util.Fonts;
import com.chteuchteu.munin.hlpr.Util.Fonts.CustomFont;
import com.chteuchteu.munin.hlpr.Util.TransitionStyle;
import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.EasyTracker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.tjeannin.apprate.AppRate;

import java.util.Locale;

/**
 * We are not extending MuninActivity from here since the activity cycle
 * is very different from others (showing UI elements only when the app
 * is loaded)
 */
public class Activity_Main extends Activity {
	private MuninFoo		muninFoo;
	private DrawerHelper	dh;
	private MaterialMenuIcon materialMenu;

	private Menu 			menu;
	private String			activityName;
	private boolean		doubleBackPressed;
	
	// Preloading
	private boolean preloading;
	private boolean optionsMenuLoaded;
	private Context context;
	private ProgressDialog myProgressDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		preloading = true;
		boolean loaded = MuninFoo.isLoaded();
		muninFoo = MuninFoo.getInstance(this);
		MuninFoo.loadLanguage(this);
		optionsMenuLoaded = false;
		if (loaded)
			preloading = false;
		
		context = this;
		setContentView(R.layout.main_clear);
		getActionBar().setTitle("");

		Util.UI.applySwag(this);
		Crashlytics.start(this);

		dh = new DrawerHelper(this, muninFoo);
		dh.setDrawerActivity(DrawerHelper.Activity_Main);
		dh.getDrawer().setOnOpenListener(new OnOpenListener() {
			@Override
			public void onOpen() {
				dh.toggle(false);
			}
		});

		this.materialMenu = new MaterialMenuIcon(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
		this.materialMenu.setNeverDrawTouch(true);
		
		Fonts.setFont(this, (TextView)findViewById(R.id.main_clear_appname), CustomFont.RobotoCondensed_Regular);
		
		if (loaded)
			onLoadFinished();
		else
			preload();
	}
	
	/**
	 * Executed when the app has loaded :
	 * 	- launching app, after the initialization
	 * 	- going back to Activity_Main
	 */
	private void onLoadFinished() {
		preloading = false;

		// Inflate menu if not already done
		if (!optionsMenuLoaded)
			createOptionsMenu();
		
		// Ask the user to rate the app
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
			.setTitle(getText(R.string.rate))
			.setIcon(R.drawable.launcher_icon)
			.setMessage(getText(R.string.rate_long))
			.setPositiveButton(getText(R.string.text33), null) // Yes
			.setNegativeButton(getText(R.string.text34), null) // No
			.setNeutralButton(getText(R.string.not_now), null); // Not now
		new AppRate(this)
			.setCustomDialog(builder)
			.setMinDaysUntilPrompt(8)
			.setMinLaunchesUntilPrompt(10)
			.init();
		
		// Display the "follow on Twitter" message
		// after X launches
		displayTwitterAlertIfNeeded();
		
		dh.getDrawer().setOnOpenListener(new OnOpenListener() {
			@Override
			public void onOpen() {
				dh.setIsOpened(true);
				materialMenu.animatePressedState(MaterialMenuDrawable.IconState.ARROW);
				activityName = getActionBar().getTitle().toString();
				getActionBar().setTitle(R.string.app_name);
			}
		});
		dh.getDrawer().setOnCloseListener(new OnCloseListener() {
			@Override
			public void onClose() {
				dh.setIsOpened(false);
				materialMenu.animatePressedState(MaterialMenuDrawable.IconState.BURGER);
				getActionBar().setTitle(activityName);
			}
		});

		dh.reset();
		dh.toggle(true);
		materialMenu.animatePressedState(MaterialMenuDrawable.IconState.ARROW);
	}
	
	@Override
	public void onBackPressed() {
		if (doubleBackPressed) {
			// Close the app when tapping twice on it.
			// Useful when going in GraphView from widgets
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
		
		doubleBackPressed = true;
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				doubleBackPressed = false;
			}
		}, 2000);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() != android.R.id.home)
			dh.closeDrawerIfOpened();
		switch (item.getItemId()) {
			case android.R.id.home:
				dh.toggle(true);
				return true;
			case R.id.menu_settings:
				startActivity(new Intent(Activity_Main.this, Activity_Settings.class));
				Util.setTransition(context, TransitionStyle.DEEPER);
				return true;
			case R.id.menu_about:
				startActivity(new Intent(Activity_Main.this, Activity_About.class));
				Util.setTransition(context, TransitionStyle.DEEPER);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		this.menu = menu;

		if (!preloading && !optionsMenuLoaded)
			createOptionsMenu();
		
		return true;
	}
	private void createOptionsMenu() {
		if (menu == null)
			return;

		optionsMenuLoaded = true;
		menu.clear();
		getMenuInflater().inflate(R.menu.main, menu);
	}
	
	public void displayTwitterAlertIfNeeded() {
		int NB_LAUNCHES = 8;
		String nbLaunches = Util.getPref(this, "twitter_nbLaunches");
		if (nbLaunches.equals(""))
			Util.setPref(this, "twitter_nbLaunches", "1");
		else if (!nbLaunches.equals("ok")) {
			int n = Integer.parseInt(nbLaunches);
			if (n == NB_LAUNCHES) {
				// Display message
				AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Main.this);
				builder.setMessage("Be the first to try beta versions of the app, and learn cool news like upcoming updates and known issues!")
				.setTitle("Follow Munin for Android on Twitter")
				.setCancelable(true)
				// Yes
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						try {
						   startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=muninforandroid")));
						} catch (Exception e) {
						   startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/muninforandroid")));
						}
					}
				})
				// No
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				Util.setPref(this, "twitter_nbLaunches", "ok");
			} else
				Util.setPref(this, "twitter_nbLaunches", String.valueOf(n+1));
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (!MuninFoo.DEBUG)
			EasyTracker.getInstance(this).activityStart(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if (!MuninFoo.DEBUG)
			EasyTracker.getInstance(this).activityStop(this);
	}
	
	private void preload() {
		Crashlytics.start(this);
		
		boolean updateOperations = !Util.getPref(context, "lastMFAVersion").equals(MuninFoo.VERSION + "");
		
		
		if (updateOperations) {
			if (myProgressDialog == null || !myProgressDialog.isShowing())
				myProgressDialog = ProgressDialog.show(context, "", getString(R.string.text39), true);
			// Please wait while the app does some update operations
			new UpdateOperations().execute();
		} else
			onLoadFinished();
	}
	
	private void updateActions() {
		if (Util.getPref(context, "lang").equals(""))
			Util.setPref(context, "lang", Locale.getDefault().getLanguage());
		
		if (Util.getPref(context, "graphview_orientation").equals(""))
			Util.setPref(context, "graphview_orientation", "auto");
		
		if (Util.getPref(context, "defaultScale").equals(""))
			Util.setPref(context, "defaultScale", "day");
		
		if (Util.hasPref(context, "drawer"))
			Util.removePref(context, "drawer");
		
		if (Util.hasPref(context, "splash"))
			Util.removePref(context, "splash");
		
		if (Util.hasPref(context, "listViewMode"))
			Util.removePref(context, "listViewMode");
		
		if (Util.hasPref(context, "transitions"))
			Util.removePref(context, "transitions");
		
		if (!Util.hasPref(context, "hideGraphviewArrows"))
			Util.setPref(context, "hideGraphviewArrows", "true");
		
		// MfA 3.0 : moved auth attributes from MuninServer to MuninMaster : migrate those
		// if possible
		// TODO put an if on this
		muninFoo.sqlite.migrateTo3();
		
		Util.setPref(context, "lastMFAVersion", MuninFoo.VERSION + "");
		muninFoo.resetInstance(this);
	}
	
	private class UpdateOperations extends AsyncTask<Void, Integer, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			updateActions();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if (myProgressDialog != null && myProgressDialog.isShowing())
				myProgressDialog.dismiss();
			
			onLoadFinished();
		}
	}

	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		materialMenu.syncState(savedInstanceState);
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		materialMenu.onSaveInstanceState(outState);
	}
}