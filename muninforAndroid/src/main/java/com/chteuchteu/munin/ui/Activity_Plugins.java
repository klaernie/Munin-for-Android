package com.chteuchteu.munin.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.chteuchteu.munin.R;
import com.chteuchteu.munin.adptr.Adapter_SeparatedList;
import com.chteuchteu.munin.adptr.ServersListAlertDialog;
import com.chteuchteu.munin.hlpr.DrawerHelper;
import com.chteuchteu.munin.hlpr.Util;
import com.chteuchteu.munin.hlpr.Util.TransitionStyle;
import com.chteuchteu.munin.obj.MuninPlugin;
import com.chteuchteu.munin.obj.MuninServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Activity_Plugins extends MuninActivity {
	private SimpleAdapter 		sa;
	private List<HashMap<String,String>> list;
	private List<MuninPlugin>	pluginsList;
	private MuninPlugin[] 		pluginsFilter;

	private TextView			customActionBarView_textView;
    private ServersListAlertDialog serversListAlertDialog;
	
	private LinearLayout	ll_filter;
	private EditText		filter;
	private ListView       listview;
	
	private int mode;
	private static final int MODE_GROUPED = 1;
	private static final int MODE_FLAT = 2;

	@SuppressLint("InflateParams")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_plugins);
		super.onContentViewSet();
		dh.setDrawerActivity(this);

		this.listview = (ListView) findViewById(R.id.listview);
		list = new ArrayList<>();

		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

		// ActionBar custom view
		LayoutInflater inflater = LayoutInflater.from(context);
		final View customActionBarView = inflater.inflate(R.layout.actionbar_dropdown, null);
		customActionBarView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                if (serversListAlertDialog == null)
				    serversListAlertDialog = new ServersListAlertDialog(context, customActionBarView,
                            new ServersListAlertDialog.ServersListAlertDialogClick() {
                        @Override
                        public void onItemClick(MuninServer server) {
                            muninFoo.setCurrentServer(server);
                            customActionBarView_textView.setText(server.getName());
                            updateListView(mode);
                        }
                    });

                serversListAlertDialog.show();
			}
		});
        customActionBarView_textView = (TextView) customActionBarView.findViewById(R.id.text);
        customActionBarView_textView.setText(muninFoo.getCurrentServer().getName());
		
		actionBar.setCustomView(customActionBarView);
		super.setOnDrawerOpen(new Runnable() {
			@Override
			public void run() {
				customActionBarView.setVisibility(View.GONE);
				actionBar.setDisplayShowCustomEnabled(false);
				actionBar.setDisplayShowTitleEnabled(true);
				actionBar.setSubtitle(muninFoo.getCurrentServer().getName());
			}
		});
		super.setOnDrawerClose(new Runnable() {
			@Override
			public void run() {
				customActionBarView.setVisibility(View.VISIBLE);
				actionBar.setDisplayShowCustomEnabled(true);
				actionBar.setDisplayShowTitleEnabled(false);
				actionBar.setSubtitle("");
			}
		});

		mode = MODE_GROUPED;

		updateListView(mode);
	}
	
	private void updateListView(final int mode) {
		this.mode = mode;
		
		if (mode == MODE_FLAT) {
			pluginsList = new ArrayList<>();
			for (int i=0; i<muninFoo.getCurrentServer().getPlugins().size(); i++) {
				if (muninFoo.getCurrentServer().getPlugins().get(i) != null)
					pluginsList.add(muninFoo.getCurrentServer().getPlugins().get(i));
			}
			
			list.clear();
			HashMap<String,String> item;
			for (MuninPlugin pl : pluginsList) {
				item = new HashMap<>();
				item.put("line1", pl.getFancyName());
				item.put("line2", pl.getName());
				list.add(item);
			}
			sa = new SimpleAdapter(this, list, R.layout.plugins_list, new String[] { "line1","line2" }, new int[] {R.id.line_a, R.id.line_b});
			listview.setAdapter(sa);
		} else {
			// Create plugins list
			List<List<MuninPlugin>> pluginsListCat = muninFoo.getCurrentServer().getPluginsListWithCategory();
			
			pluginsList = new ArrayList<>();
			for (int i=0; i<muninFoo.getCurrentServer().getPlugins().size(); i++) {
				if (muninFoo.getCurrentServer().getPlugins().get(i) != null)
					pluginsList.add(muninFoo.getCurrentServer().getPlugins().get(i));
			}
			
			Adapter_SeparatedList adapter = new Adapter_SeparatedList(this, false);
			for (List<MuninPlugin> l : pluginsListCat) {
				List<Map<String,?>> elements = new LinkedList<>();
				String categoryName = "";
				for (MuninPlugin p : l) {
					elements.add(createItem(p.getFancyName(), p.getName()));
					categoryName = p.getCategory();
					categoryName = Util.capitalize(categoryName);
				}
				
				adapter.addSection(categoryName, new SimpleAdapter(this, elements, R.layout.plugins_list,
						new String[] { "title", "caption" }, new int[] { R.id.line_a, R.id.line_b }));
			}
			listview.setAdapter(adapter);
		}
		
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
				TextView plu = (TextView) view.findViewById(R.id.line_b);
				Intent intent = new Intent(Activity_Plugins.this, Activity_GraphView.class);
				int p = 0;
				for (int i = 0; i < muninFoo.getCurrentServer().getPlugins().size(); i++) {
					if (muninFoo.getCurrentServer().getPlugin(i).getName().equals(plu.getText().toString())) {
						p = i;
						break;
					}
				}
				intent.putExtra("position", p);
				intent.putExtra("from", "plugins");
				startActivity(intent);
				Util.setTransition(context, TransitionStyle.DEEPER);
			}
		});
		
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, final View view, final int position, long arg) {
				// Display actions list
				AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
				final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
						context, android.R.layout.simple_list_item_1);
				arrayAdapter.add(context.getString(R.string.delete_plugin));
				
				builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								TextView plu = (TextView) view.findViewById(R.id.line_b);
								for (int i=0; i<muninFoo.getCurrentServer().getPlugins().size(); i++) {
									MuninPlugin plugin = muninFoo.getCurrentServer().getPlugin(i);
									if (plugin != null && plugin.getName().equals(plu.getText().toString())) {
										muninFoo.getCurrentServer().getPlugins().remove(plugin);
										muninFoo.sqlite.dbHlpr.deleteMuninPlugin(plugin, true);
										// Remove from labels if necessary
										muninFoo.removeLabelRelation(plugin);
										
										// Save scroll state
										int index = listview.getFirstVisiblePosition();
										View v = listview.getChildAt(0);
										int top = (v == null) ? 0 : v.getTop();
										
										updateListView(mode);
										
										listview.setSelectionFromTop(index, top);
										break;
									}
								}
								
								break;
						}
					}
				});
				builderSingle.show();
				
				return true;
			}
		});
	}
	
	private static Map<String,?> createItem(String title, String caption) {
		Map<String,String> item = new HashMap<>();
		item.put("title", title);
		item.put("caption", caption);
		return item;
	}

	protected void createOptionsMenu() {
		super.createOptionsMenu();

		getMenuInflater().inflate(R.menu.plugins, menu);
		
		ll_filter = (LinearLayout) this.findViewById(R.id.ll_filter);
		filter = (EditText) this.findViewById(R.id.filter);
		
		filter.addTextChangedListener(new TextWatcher() {
			@SuppressLint("DefaultLocale")
			@Override
			public void afterTextChanged(Editable s) {
				if (pluginsList != null && !pluginsList.isEmpty() && s != null) {
					list.clear();
					String search = s.toString();
					
					pluginsFilter = new MuninPlugin[pluginsList.size()];
					for (int i=0; i<pluginsList.size(); i++) {
						if (pluginsList.get(i).getFancyName().toLowerCase(Locale.ENGLISH).contains(search.toLowerCase(Locale.ENGLISH))
								|| pluginsList.get(i).getName().toLowerCase(Locale.ENGLISH).contains(search.toLowerCase(Locale.ENGLISH)))
							pluginsFilter[i] = pluginsList.get(i);
					}
					
					HashMap<String,String> item;
					for (MuninPlugin p : pluginsFilter) {
						if (p != null) {
							item = new HashMap<>();
							item.put("line1", p.getFancyName());
							item.put("line2", p.getName());
							list.add(item);
						}
					}
					sa = new SimpleAdapter(Activity_Plugins.this, list, R.layout.plugins_list, new String[] { "line1","line2" }, new int[] {R.id.line_a, R.id.line_b});
					listview.setAdapter(sa);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
		});
	}

	@Override
	public DrawerHelper.DrawerMenuItem getDrawerMenuItem() { return DrawerHelper.DrawerMenuItem.Graphs; }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
			case R.id.menu_filter:
				if (ll_filter.getVisibility() == View.GONE) {
					filter.setFocusable(true);
					filter.setFocusableInTouchMode(true);
					ll_filter.setVisibility(View.VISIBLE);
				} else {
					ll_filter.setVisibility(View.GONE);
					filter.setFocusable(false);
					filter.setFocusableInTouchMode(false);
					filter.clearFocus();
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(filter.getWindowToken(), 0);
				}
				return true;
		}

		return true;
	}
	
	@Override
	public void onBackPressed() {
		if (ll_filter != null && ll_filter.getVisibility() == View.VISIBLE) {
			filter.setText("");
			ll_filter.setVisibility(View.GONE);
			filter.setFocusable(false);
			filter.setFocusableInTouchMode(false);
			filter.clearFocus();
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(filter.getWindowToken(), 0);
			updateListView(MODE_GROUPED);
		} else {
			Intent intent = new Intent(this, Activity_Main.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			Util.setTransition(context, TransitionStyle.SHALLOWER);
		}
	}
}