package com.chteuchteu.munin.hlpr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.chteuchteu.munin.MuninFoo;
import com.chteuchteu.munin.obj.AlertsWidget;
import com.chteuchteu.munin.obj.GraphWidget;
import com.chteuchteu.munin.obj.Grid;
import com.chteuchteu.munin.obj.GridItem;
import com.chteuchteu.munin.obj.Label;
import com.chteuchteu.munin.obj.MuninMaster;
import com.chteuchteu.munin.obj.MuninMaster.DynazoomAvailability;
import com.chteuchteu.munin.obj.MuninPlugin;
import com.chteuchteu.munin.obj.MuninServer;
import com.chteuchteu.munin.obj.MuninMaster.AuthType;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 6;
	private static final String DATABASE_NAME = "muninForAndroid2.db";
	
	// Table names
	public static final String TABLE_MUNINMASTERS = "muninMasters";
	public static final String TABLE_MUNINSERVERS = "muninServers";
	public static final String TABLE_MUNINPLUGINS = "muninPlugins";
	public static final String TABLE_LABELS = "labels";
	public static final String TABLE_LABELSRELATIONS = "labelsRelations";
	public static final String TABLE_WIDGET_GRAPHWIDGETS = "widgets";
	public static final String TABLE_WIDGET_ALERTSWIDGETS = "alertsWidgets";
	public static final String TABLE_WIDGET_ALERTSWIDGETSRELATIONS = "alertsWidgetsRelations";
	public static final String TABLE_GRIDS = "grids";
	public static final String TABLE_GRIDITEMRELATIONS = "gridItemsRelations";
	
	// Fields
	public static final String KEY_ID = "id";
	
	// MuninMasters
	private static final String KEY_MUNINMASTERS_NAME = "name";
	private static final String KEY_MUNINMASTERS_URL = "url";
	private static final String KEY_MUNINMASTERS_AUTHLOGIN = "authLogin";
	private static final String KEY_MUNINMASTERS_AUTHPASSWORD = "authPassword";
	private static final String KEY_MUNINMASTERS_SSL = "SSL";
	private static final String KEY_MUNINMASTERS_AUTHTYPE = "authType";
	private static final String KEY_MUNINMASTERS_AUTHSTRING = "authString";
	private static final String KEY_MUNINMASTERS_HDGRAPHS = "hdGraphs";

	// MuninServers
	private static final String KEY_MUNINSERVERS_SERVERURL = "serverUrl";
	private static final String KEY_MUNINSERVERS_NAME = "name";
	private static final String KEY_MUNINSERVERS_GRAPHURL = "graphURL";
	private static final String KEY_MUNINSERVERS_HDGRAPHURL = "hdGraphURL";
	private static final String KEY_MUNINSERVERS_POSITION = "position";
	private static final String KEY_MUNINSERVERS_MASTER = "master";
	
	// MuninPlugins
	private static final String KEY_MUNINPLUGINS_NAME = "name";
	private static final String KEY_MUNINPLUGINS_FANCYNAME = "fancyName";
	private static final String KEY_MUNINPLUGINS_SERVER = "server";
	private static final String KEY_MUNINPLUGINS_CATEGORY = "category";
	private static final String KEY_MUNINPLUGINS_PLUGINPAGEURL = "pluginPageUrl";
	
	// Labels
	private static final String KEY_LABELS_NAME = "name";
	
	// LabelsRelations
	private static final String KEY_LABELSRELATIONS_PLUGIN = "plugin";
	private static final String KEY_LABELSRELATIONS_LABEL = "label";
	
	// Widget_GraphWidgets
	private static final String KEY_WIDGET_GRAPHWIDGETS_PLUGIN = "plugin";
	private static final String KEY_WIDGET_GRAPHWIDGETS_PERIOD = "period";
	private static final String KEY_WIDGET_GRAPHWIDGETS_WIFIONLY = "wifiOnly";
	private static final String KEY_WIDGET_GRAPHWIDGETS_HIDESERVERNAME = "hideServerName";
	private static final String KEY_WIDGET_GRAPHWIDGETS_WIDGETID = "widgetId";

	// Widget_AlertsWidgets
	private static final String KEY_WIDGET_ALERTSWIDGETS_WIDGETID = "widgetId";
	private static final String KEY_WIDGET_ALERTSWIDGETS_WIFIONLY = "wifiOnly";

	// Widget_AlertsWidgetsRelations
	private static final String KEY_WIDGET_ALERTSWIDGETSRELATIONS_WIDGET = "widget";
	private static final String KEY_WIDGET_ALERTSWIDGETSRELATIONS_SERVER = "server";

	// Grids
	private static final String KEY_GRIDS_NAME = "name";
	
	// GridItemRelations
	private static final String KEY_GRIDITEMRELATIONS_GRID = "grid";
	private static final String KEY_GRIDITEMRELATIONS_X = "x";
	private static final String KEY_GRIDITEMRELATIONS_Y = "y";
	private static final String KEY_GRIDITEMRELATIONS_PLUGIN = "plugin";
	
	
	private static final String CREATE_TABLE_MUNINMASTERS = "CREATE TABLE " + TABLE_MUNINMASTERS + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_MUNINMASTERS_NAME + " TEXT,"
			+ KEY_MUNINMASTERS_URL + " TEXT,"
			+ KEY_MUNINMASTERS_AUTHLOGIN + " TEXT,"
			+ KEY_MUNINMASTERS_AUTHPASSWORD + " TEXT,"
			+ KEY_MUNINMASTERS_AUTHTYPE + " INTEGER,"
			+ KEY_MUNINMASTERS_AUTHSTRING + " TEXT,"
			+ KEY_MUNINMASTERS_SSL + " INTEGER,"
			+ KEY_MUNINMASTERS_HDGRAPHS + " TEXT)";
	
	private static final String CREATE_TABLE_MUNINSERVERS = "CREATE TABLE " + TABLE_MUNINSERVERS + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_MUNINSERVERS_SERVERURL + " TEXT,"
			+ KEY_MUNINSERVERS_NAME + " TEXT,"
			+ KEY_MUNINSERVERS_GRAPHURL + " TEXT,"
			+ KEY_MUNINSERVERS_HDGRAPHURL + " TEXT,"
			+ KEY_MUNINSERVERS_POSITION + " INTEGER,"
			+ KEY_MUNINSERVERS_MASTER + " INTEGER)";
	
	private static final String CREATE_TABLE_MUNINPLUGINS = "CREATE TABLE " + TABLE_MUNINPLUGINS + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_MUNINPLUGINS_NAME + " TEXT,"
			+ KEY_MUNINPLUGINS_FANCYNAME + " TEXT,"
			+ KEY_MUNINPLUGINS_SERVER + " INTEGER,"
			+ KEY_MUNINPLUGINS_CATEGORY + " TEXT,"
			+ KEY_MUNINPLUGINS_PLUGINPAGEURL + " TEXT)";
	
	private static final String CREATE_TABLE_LABELS = "CREATE TABLE " + TABLE_LABELS + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_LABELS_NAME + " TEXT)";
	
	private static final String CREATE_TABLE_LABELSRELATIONS = "CREATE TABLE " + TABLE_LABELSRELATIONS + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_LABELSRELATIONS_LABEL + " INTEGER,"
			+ KEY_LABELSRELATIONS_PLUGIN + " INTEGER)";
	
	private static final String CREATE_TABLE_GRAPHWIDGETS = "CREATE TABLE " + TABLE_WIDGET_GRAPHWIDGETS + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_WIDGET_GRAPHWIDGETS_WIDGETID + " INTEGER,"
			+ KEY_WIDGET_GRAPHWIDGETS_PLUGIN + " INTEGER,"
			+ KEY_WIDGET_GRAPHWIDGETS_PERIOD + " TEXT,"
			+ KEY_WIDGET_GRAPHWIDGETS_WIFIONLY + " INTEGER,"
			+ KEY_WIDGET_GRAPHWIDGETS_HIDESERVERNAME + " INTEGER)";

	private static final String CREATE_TABLE_ALERTSWIDGETS = "CREATE TABLE " + TABLE_WIDGET_ALERTSWIDGETS + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_WIDGET_ALERTSWIDGETS_WIDGETID + " INTEGER,"
			+ KEY_WIDGET_ALERTSWIDGETS_WIFIONLY + " INTEGER)";

	private static final String CREATE_TABLE_ALERTSWIDGETSRELATIONS = "CREATE TABLE " + TABLE_WIDGET_ALERTSWIDGETSRELATIONS + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_WIDGET_ALERTSWIDGETSRELATIONS_WIDGET + " INTEGER,"
			+ KEY_WIDGET_ALERTSWIDGETSRELATIONS_SERVER + " INTEGER)";
	
	private static final String CREATE_TABLE_GRIDS = "CREATE TABLE " + TABLE_GRIDS + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_GRIDS_NAME + " TEXT)";
	
	private static final String CREATE_TABLE_GRIDITEMRELATIONS = "CREATE TABLE " + TABLE_GRIDITEMRELATIONS + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_GRIDITEMRELATIONS_GRID + " INTEGER,"
			+ KEY_GRIDITEMRELATIONS_PLUGIN + " INTEGER,"
			+ KEY_GRIDITEMRELATIONS_X + " INTEGER,"
			+ KEY_GRIDITEMRELATIONS_Y + ")";
	
	public DatabaseHelper(Context c) {
		super(c, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_MUNINMASTERS);
		db.execSQL(CREATE_TABLE_MUNINSERVERS);
		db.execSQL(CREATE_TABLE_MUNINPLUGINS);
		db.execSQL(CREATE_TABLE_LABELS);
		db.execSQL(CREATE_TABLE_LABELSRELATIONS);
		db.execSQL(CREATE_TABLE_GRAPHWIDGETS);
		db.execSQL(CREATE_TABLE_ALERTSWIDGETS);
		db.execSQL(CREATE_TABLE_ALERTSWIDGETSRELATIONS);
		db.execSQL(CREATE_TABLE_GRIDS);
		db.execSQL(CREATE_TABLE_GRIDITEMRELATIONS);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		MuninFoo.log("onUpgrade from " + oldVersion + " to " + newVersion);
		if (oldVersion < 2) // From 1 to 2
			db.execSQL("ALTER TABLE " + TABLE_MUNINSERVERS + " ADD COLUMN " + KEY_MUNINSERVERS_NAME + " TEXT");
		if (oldVersion < 3) { // From 2 to 3
			db.execSQL(CREATE_TABLE_GRIDS);
			db.execSQL(CREATE_TABLE_GRIDITEMRELATIONS);
		}
		if (oldVersion < 4) { // From 3 to 4
			db.execSQL("ALTER TABLE " + TABLE_MUNINPLUGINS + " ADD COLUMN " + KEY_MUNINPLUGINS_PLUGINPAGEURL + " TEXT");
			db.execSQL(CREATE_TABLE_MUNINMASTERS);
			db.execSQL("ALTER TABLE " + TABLE_MUNINSERVERS + " ADD COLUMN " + KEY_MUNINSERVERS_MASTER + " INTEGER");
			db.execSQL("ALTER TABLE " + TABLE_WIDGET_GRAPHWIDGETS + " ADD COLUMN " + KEY_WIDGET_GRAPHWIDGETS_HIDESERVERNAME + " INTEGER");
		}
		if (oldVersion < 5) { // From 4 to 5
			// For unknown reasons, this gets executed several times. (throwing SQLiteException)
			try {
				db.execSQL("ALTER TABLE " + TABLE_MUNINMASTERS + " ADD COLUMN " + KEY_MUNINMASTERS_HDGRAPHS + " TEXT");
				db.execSQL("ALTER TABLE " + TABLE_MUNINMASTERS + " ADD COLUMN " + KEY_MUNINMASTERS_AUTHLOGIN + " TEXT");
				db.execSQL("ALTER TABLE " + TABLE_MUNINMASTERS + " ADD COLUMN " + KEY_MUNINMASTERS_AUTHPASSWORD + " TEXT");
				db.execSQL("ALTER TABLE " + TABLE_MUNINMASTERS + " ADD COLUMN " + KEY_MUNINMASTERS_AUTHSTRING + " TEXT");
				db.execSQL("ALTER TABLE " + TABLE_MUNINMASTERS + " ADD COLUMN " + KEY_MUNINMASTERS_AUTHTYPE + " TEXT");
				db.execSQL("ALTER TABLE " + TABLE_MUNINMASTERS + " ADD COLUMN " + KEY_MUNINMASTERS_SSL + " INTEGER");
				db.execSQL(CREATE_TABLE_ALERTSWIDGETS);
				db.execSQL(CREATE_TABLE_ALERTSWIDGETSRELATIONS);
			} catch (SQLiteException ex) { ex.printStackTrace(); }
		}
		if (oldVersion < 6) // From 5 to 6
			db.execSQL("ALTER TABLE " + TABLE_MUNINSERVERS + " ADD COLUMN " + KEY_MUNINSERVERS_HDGRAPHURL + " TEXT");
	}
	
	public static void close(Cursor c, SQLiteDatabase db) {
		if (c != null)	c.close();
		if (db != null)	db.close();
	}
	
	public long insertMuninMaster(MuninMaster m) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_MUNINMASTERS_NAME, m.getName());
		values.put(KEY_MUNINMASTERS_URL, m.getUrl());
		values.put(KEY_MUNINMASTERS_AUTHLOGIN, m.getAuthLogin());
		values.put(KEY_MUNINMASTERS_AUTHPASSWORD, m.getAuthPassword());
		values.put(KEY_MUNINMASTERS_SSL, m.getSSL());
		values.put(KEY_MUNINMASTERS_AUTHTYPE, m.getAuthType().getVal());
		values.put(KEY_MUNINMASTERS_AUTHSTRING, m.getAuthString());
		values.put(KEY_MUNINMASTERS_HDGRAPHS, m.isDynazoomAvailable().getVal());
		
		long id = db.insert(TABLE_MUNINMASTERS, null, values);
		m.setId(id);
		
		close(null, db);
		return id;
	}
	
	public long insertMuninServer(MuninServer s) {
		if (s == null)
			return -1;
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_MUNINSERVERS_SERVERURL, s.getServerUrl());
		values.put(KEY_MUNINSERVERS_NAME, s.getName());
		values.put(KEY_MUNINSERVERS_GRAPHURL, s.getGraphURL());
		values.put(KEY_MUNINSERVERS_HDGRAPHURL, s.getHdGraphURL());
		values.put(KEY_MUNINSERVERS_POSITION, s.getPosition());
		values.put(KEY_MUNINSERVERS_MASTER, s.master != null ? s.master.getId() : -1);
		
		long id = db.insert(TABLE_MUNINSERVERS, null, values);
		s.setId(id);
		s.isPersistant = true;
		
		close(null, db);
		return id;
	}
	
	public long insertMuninPlugin(MuninPlugin p) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_MUNINPLUGINS_NAME, p.getName());
		values.put(KEY_MUNINPLUGINS_FANCYNAME, p.getFancyName());
		values.put(KEY_MUNINPLUGINS_SERVER, p.getInstalledOn().getId());
		values.put(KEY_MUNINPLUGINS_CATEGORY, p.getCategory());
		values.put(KEY_MUNINPLUGINS_PLUGINPAGEURL, p.getPluginPageUrl());
		
		long id = db.insert(TABLE_MUNINPLUGINS, null, values);
		p.setId(id);
		close(null, db);
		return id;
	}
	
	public void deleteMuninPlugin(MuninPlugin p, boolean onCascade) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_MUNINPLUGINS, KEY_ID + " = ?", new String[] { String.valueOf(p.getId()) });
		close(null, db);
		
		if (onCascade) {
			deleteGridItemRelations(p);
			deleteLabelsRelations(p);
			deleteGraphWidgets(p);
		}
	}
	
	
	
	public long insertLabel(Label l) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_LABELS_NAME, l.getName());
		
		long id = db.insert(TABLE_LABELS, null, values);
		l.setId(id);
		close(null, db);
		return id;
	}
	
	public int updateLabel(Label label) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_LABELS_NAME, label.getName());
		
		int nbRows = db.update(TABLE_LABELS, values, KEY_ID + " = ?", new String[]{String.valueOf(label.getId())});
		close(null, db);
		return nbRows;
	}
	
	public long insertLabelRelation(MuninPlugin p, Label l) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_LABELSRELATIONS_LABEL, l.getId());
		values.put(KEY_LABELSRELATIONS_PLUGIN, p.getId());
		
		long id = db.insert(TABLE_LABELSRELATIONS, null, values);
		close(null, db);
		return id;
	}
	
	public long insertGraphWidget(GraphWidget w) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_WIDGET_GRAPHWIDGETS_PLUGIN, w.getPlugin().getId());
		values.put(KEY_WIDGET_GRAPHWIDGETS_PERIOD, w.getPeriod());
		values.put(KEY_WIDGET_GRAPHWIDGETS_WIFIONLY, w.isWifiOnly());
		values.put(KEY_WIDGET_GRAPHWIDGETS_WIDGETID, w.getWidgetId());
		values.put(KEY_WIDGET_GRAPHWIDGETS_HIDESERVERNAME, w.getHideServerName());
		
		long id = db.insert(TABLE_WIDGET_GRAPHWIDGETS, null, values);
		w.setId(id);
		close(null, db);
		return id;
	}

	public long insertAlertsWidget(AlertsWidget w) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_WIDGET_ALERTSWIDGETS_WIDGETID, w.getWidgetId());
		values.put(KEY_WIDGET_ALERTSWIDGETS_WIFIONLY, w.isWifiOnly());

		long id = db.insert(TABLE_WIDGET_ALERTSWIDGETS, null, values);
		w.setId(id);

		// Insert relations
		for (MuninServer server : w.getServers()) {
			MuninFoo.log("Inserting AlertsWidget relation between widget "
					+ w.getWidgetId() + " and server " + server.getName());

			ContentValues values2 = new ContentValues();
			values2.put(KEY_WIDGET_ALERTSWIDGETSRELATIONS_SERVER, server.getId());
			values2.put(KEY_WIDGET_ALERTSWIDGETSRELATIONS_WIDGET, w.getId());

			db.insert(TABLE_WIDGET_ALERTSWIDGETSRELATIONS, null, values2);
		}

		close(null, db);
		return id;
	}
	
	public long insertGrid(String gridName) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_GRIDS_NAME, gridName);
		
		long id = db.insert(TABLE_GRIDS, null, values);
		close(null, db);
		return id;
	}
	
	public long insertGridItemRelation(GridItem i) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		try {
			values.put(KEY_GRIDITEMRELATIONS_GRID, i.getGrid().getId());
			values.put(KEY_GRIDITEMRELATIONS_PLUGIN, i.getPlugin().getId());
			values.put(KEY_GRIDITEMRELATIONS_X, i.getX());
			values.put(KEY_GRIDITEMRELATIONS_Y, i.getY());
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			return -1;
		}
		
		long id = db.insert(TABLE_GRIDITEMRELATIONS, null, values);
		i.setId(id);
		close(null, db);
		return id;
	}

	public void insertGridItemRelations(List<GridItem> items) {
		if (items.isEmpty())
			return;

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		for (GridItem item : items) {
			try {
				values.clear();
				values.put(KEY_GRIDITEMRELATIONS_GRID, item.getGrid().getId());
				values.put(KEY_GRIDITEMRELATIONS_PLUGIN, item.getPlugin().getId());
				values.put(KEY_GRIDITEMRELATIONS_X, item.getX());
				values.put(KEY_GRIDITEMRELATIONS_Y, item.getY());
			} catch (NullPointerException ex) {
				ex.printStackTrace();
			}

			long id = db.insert(TABLE_GRIDITEMRELATIONS, null, values);
			item.setId(id);
		}

		close(null, db);
	}
	
	public void saveGridItemsRelations(Grid g) {
		deleteGridItemRelations(g);
		for (GridItem i : g.getItems())
			insertGridItemRelation(i);
	}
	
	public void saveMuninMaster(MuninMaster m) {
		if (!m.isPersistant)
			insertMuninMaster(m);
		else
			updateMuninMaster(m);
	}
	
	public int updateMuninMaster(MuninMaster m) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_MUNINMASTERS_NAME, m.getName());
		values.put(KEY_MUNINMASTERS_URL, m.getUrl());
		values.put(KEY_MUNINMASTERS_AUTHLOGIN, m.getAuthLogin());
		values.put(KEY_MUNINMASTERS_AUTHPASSWORD, m.getAuthPassword());
		values.put(KEY_MUNINMASTERS_SSL, m.getSSL());
		values.put(KEY_MUNINMASTERS_AUTHTYPE, m.getAuthType().getVal());
		values.put(KEY_MUNINMASTERS_AUTHSTRING, m.getAuthString());
		values.put(KEY_MUNINMASTERS_HDGRAPHS, m.isDynazoomAvailable().getVal());
		
		int nbRows = db.update(TABLE_MUNINMASTERS, values, KEY_ID + " = ?", new String[] { String.valueOf(m.getId()) });
		close(null, db);
		return nbRows;
	}
	
	public int updateMuninServer(MuninServer s) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_MUNINSERVERS_SERVERURL, s.getServerUrl());
		values.put(KEY_MUNINSERVERS_NAME, s.getName());
		values.put(KEY_MUNINSERVERS_GRAPHURL, s.getGraphURL());
		values.put(KEY_MUNINSERVERS_HDGRAPHURL, s.getHdGraphURL());
		values.put(KEY_MUNINSERVERS_POSITION ,s.getPosition());
		values.put(KEY_MUNINSERVERS_MASTER, s.master.getId());
		
		int nbRows = db.update(TABLE_MUNINSERVERS, values, KEY_ID + " = ?", new String[] { String.valueOf(s.getId()) });
		close(null, db);
		return nbRows;
	}
	
	public int updateMuninPlugin(MuninPlugin p) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_MUNINPLUGINS_NAME, p.getName());
		values.put(KEY_MUNINPLUGINS_FANCYNAME, p.getFancyName());
		values.put(KEY_MUNINPLUGINS_SERVER, p.getInstalledOn().getId());
		values.put(KEY_MUNINPLUGINS_CATEGORY, p.getCategory());
		values.put(KEY_MUNINPLUGINS_PLUGINPAGEURL, p.getPluginPageUrl());
		
		int nbRows = db.update(TABLE_MUNINPLUGINS, values, KEY_ID + " = ?", new String[] { String.valueOf(p.getId()) });
		close(null, db);
		return nbRows;
	}
	
	public int updateGridName(String oldName, String newName) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_GRIDS_NAME, newName);
		
		int nbRows = db.update(TABLE_GRIDS, values, KEY_GRIDS_NAME + " = ?", new String[] { oldName });
		close(null, db);
		return nbRows;
	}
	
	public boolean gridExists(String gridName) {
		return GenericQueries.getNbLines(this, TABLE_GRIDS, KEY_GRIDS_NAME + " = '" + gridName + "'") > 0;
	}
	
	public List<MuninMaster> getMasters() {
		List<MuninMaster> l = new ArrayList<>();
		try {
			String selectQuery = "SELECT * FROM " + TABLE_MUNINMASTERS;
			
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor c = db.rawQuery(selectQuery, null);
			
			if (c != null && c.moveToFirst()) {
				do {
					MuninMaster m = new MuninMaster();
					m.setId(c.getInt(c.getColumnIndex(KEY_ID)));
					m.setName(c.getString(c.getColumnIndex(KEY_MUNINMASTERS_NAME)));
					m.setUrl(c.getString(c.getColumnIndex(KEY_MUNINMASTERS_URL)));
					m.setAuthIds(c.getString(c.getColumnIndex(KEY_MUNINMASTERS_AUTHLOGIN)),
							c.getString(c.getColumnIndex(KEY_MUNINMASTERS_AUTHPASSWORD)),
							AuthType.get(c.getInt(c.getColumnIndex(KEY_MUNINMASTERS_AUTHTYPE))));
					m.setAuthString(c.getString(c.getColumnIndex(KEY_MUNINMASTERS_AUTHSTRING)));
					m.setSSL(c.getInt(c.getColumnIndex(KEY_MUNINMASTERS_SSL)) == 1);
					m.setDynazoomAvailable(DynazoomAvailability.get(c.getString(c.getColumnIndex(KEY_MUNINMASTERS_HDGRAPHS))));
					m.isPersistant = true;
					l.add(m);
				} while (c.moveToNext());
			}
			
			close(c, db);
		} catch (Exception ex) {
			Crashlytics.logException(ex);
		}
		return l;
	}
	
	public MuninMaster getMaster(long id, List<MuninMaster> currentMasters) {
		if (id == -1)	return null;
		
		// Check if we already got it
		if (currentMasters != null) {
			for (MuninMaster m : currentMasters) {
				if (m.getId() == id)
					return m;
			}
		}
		
		// We don't already have it -> get it from BDD
		String selectQuery = "SELECT * FROM " + TABLE_MUNINMASTERS 
				+ " WHERE " + KEY_ID + " = " + id;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		if (c != null && c.moveToFirst()) {
			MuninMaster m = new MuninMaster();
			m.setId(c.getInt(c.getColumnIndex(KEY_ID)));
			m.setName(c.getString(c.getColumnIndex(KEY_MUNINMASTERS_NAME)));
			m.setUrl(c.getString(c.getColumnIndex(KEY_MUNINMASTERS_URL)));
			m.setAuthIds(c.getString(c.getColumnIndex(KEY_MUNINMASTERS_AUTHLOGIN)),
					c.getString(c.getColumnIndex(KEY_MUNINMASTERS_AUTHPASSWORD)),
					AuthType.get(c.getInt(c.getColumnIndex(KEY_MUNINMASTERS_AUTHTYPE))));
			m.setAuthString(c.getString(c.getColumnIndex(KEY_MUNINMASTERS_AUTHSTRING)));
			m.setSSL(c.getInt(c.getColumnIndex(KEY_MUNINMASTERS_SSL)) == 1);
			m.setDynazoomAvailable(DynazoomAvailability.get(c.getString(c.getColumnIndex(KEY_MUNINMASTERS_HDGRAPHS))));
			m.isPersistant = true;
			close(c, db);
			return m;
		}
			
		return null;
	}

	public MuninMaster getMasterFromServer(long serverId) {
		String masterIdQuery = "SELECT " + KEY_MUNINSERVERS_MASTER + " FROM " + TABLE_MUNINSERVERS
				+ " WHERE " + KEY_ID + " = " + serverId;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(masterIdQuery, null);

		if (c != null && c.moveToFirst()) {
			long masterId = c.getInt(c.getColumnIndex(KEY_MUNINSERVERS_MASTER));
			close(c, db);
			return getMaster(masterId, null);
		}

		return null;
	}

	/**
	 * Gets the servers list from db.
	 * @param currentMasters Avoid fetching a server from db if already done
	 * @return List<MuninServer>
	 */
	public List<MuninServer> getServers(List<MuninMaster> currentMasters) {
		List<MuninServer> l = new ArrayList<>();
		try {
			String selectQuery = "SELECT * FROM " + TABLE_MUNINSERVERS
					+ " ORDER BY " + KEY_MUNINSERVERS_MASTER + ", " + KEY_MUNINSERVERS_POSITION + ", " + KEY_ID;
			
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor c = db.rawQuery(selectQuery, null);
			
			if (c != null && c.moveToFirst()) {
				do {
					MuninServer s = new MuninServer();
					s.setId(c.getInt(c.getColumnIndex(KEY_ID)));
					s.setServerUrl(c.getString(c.getColumnIndex(KEY_MUNINSERVERS_SERVERURL)));
					s.setName(c.getString(c.getColumnIndex(KEY_MUNINSERVERS_NAME)));
					s.setGraphURL(c.getString(c.getColumnIndex(KEY_MUNINSERVERS_GRAPHURL)));
					s.setHdGraphURL(c.getString(c.getColumnIndex(KEY_MUNINSERVERS_HDGRAPHURL)));
					s.setParent(getMaster(c.getInt(c.getColumnIndex(KEY_MUNINSERVERS_MASTER)), currentMasters));
					s.setPosition(c.getInt(c.getColumnIndex(KEY_MUNINSERVERS_POSITION)));
					s.setPluginsList(getPlugins(s));
					s.isPersistant = true;
					l.add(s);
				} while (c.moveToNext());
			}
			
			close(c, db);
		} catch (Exception ex) {
			Crashlytics.logException(ex);
		}
		return l;
	}
	
	public List<MuninPlugin> getPlugins(MuninServer s) {
		List<MuninPlugin> l = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + TABLE_MUNINPLUGINS 
				+ " WHERE " + KEY_MUNINPLUGINS_SERVER + " = " + s.getId();
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		if (c != null && c.moveToFirst()) {
			do {
				MuninPlugin p = new MuninPlugin();
				p.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				p.setName(c.getString(c.getColumnIndex(KEY_MUNINPLUGINS_NAME)));
				p.setFancyName(c.getString(c.getColumnIndex(KEY_MUNINPLUGINS_FANCYNAME)));
				p.setCategory(c.getString(c.getColumnIndex(KEY_MUNINPLUGINS_CATEGORY)));
				p.setPluginPageUrl(c.getString(c.getColumnIndex(KEY_MUNINPLUGINS_PLUGINPAGEURL)));
				p.setInstalledOn(s);
				l.add(p);
			} while (c.moveToNext());
		}
		
		close(c, db);
		return l;
	}
	
	public MuninPlugin getPlugin(long id) {
		String selectQuery = "SELECT * FROM " + TABLE_MUNINPLUGINS 
				+ " WHERE " + KEY_ID + " = " + id;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		if (c != null && c.moveToFirst()) {
			MuninPlugin p = new MuninPlugin();
			p.setId(c.getInt(c.getColumnIndex(KEY_ID)));
			p.setName(c.getString(c.getColumnIndex(KEY_MUNINPLUGINS_NAME)));
			p.setFancyName(c.getString(c.getColumnIndex(KEY_MUNINPLUGINS_FANCYNAME)));
			p.setCategory(c.getString(c.getColumnIndex(KEY_MUNINPLUGINS_CATEGORY)));
			p.setPluginPageUrl(c.getString(c.getColumnIndex(KEY_MUNINPLUGINS_PLUGINPAGEURL)));
			p.setInstalledOn(getServer(c.getInt(c.getColumnIndex(KEY_MUNINPLUGINS_SERVER))));
			close(c, db);
			return p;
		}
		return null;
	}
	
	public MuninServer getServer(int id) {
		String selectQuery = "SELECT * FROM " + TABLE_MUNINSERVERS
				+ " WHERE " + KEY_ID + " = " + id;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		if (c != null && c.moveToFirst()) {
			MuninServer s = new MuninServer();
			s.setId(c.getInt(c.getColumnIndex(KEY_ID)));
			s.setServerUrl(c.getString(c.getColumnIndex(KEY_MUNINSERVERS_SERVERURL)));
			s.setName(c.getString(c.getColumnIndex(KEY_MUNINSERVERS_NAME)));
			s.setGraphURL(c.getString(c.getColumnIndex(KEY_MUNINSERVERS_GRAPHURL)));
			s.setHdGraphURL(c.getString(c.getColumnIndex(KEY_MUNINSERVERS_HDGRAPHURL)));
			s.setPosition(c.getInt(c.getColumnIndex(KEY_MUNINSERVERS_POSITION)));
			s.setPluginsList(getPlugins(s));
			s.isPersistant = true;
			close(c, db);
			return s;
		}
		return null;
	}
	
	public List<GraphWidget> getGraphWidgets() {
		List<GraphWidget> l = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + TABLE_WIDGET_GRAPHWIDGETS;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		if (c != null && c.moveToFirst()) {
			do {
				GraphWidget w = new GraphWidget();
				w.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				w.setPeriod(c.getString(c.getColumnIndex(KEY_WIDGET_GRAPHWIDGETS_PERIOD)));
				w.setWidgetId(c.getInt(c.getColumnIndex(KEY_WIDGET_GRAPHWIDGETS_WIDGETID)));
				w.setPlugin(getPlugin(c.getInt(c.getColumnIndex(KEY_WIDGET_GRAPHWIDGETS_PLUGIN))));
				w.setWifiOnly(c.getInt(c.getColumnIndex(KEY_WIDGET_GRAPHWIDGETS_WIFIONLY)));
				w.setHideServerName(c.getInt(c.getColumnIndex(KEY_WIDGET_GRAPHWIDGETS_HIDESERVERNAME)));
				l.add(w);
			} while (c.moveToNext());
		}
		
		close(c, db);
		return l;
	}

	public boolean hasAlertsWidget(int widgetId) {
		return GenericQueries.getNbLines(this, TABLE_WIDGET_ALERTSWIDGETS, KEY_WIDGET_ALERTSWIDGETS_WIDGETID + " = " + widgetId) > 0;
	}

	/**
	 * Get an alertWidget from its id.
	 * @param widgetId Widget id provided by Android at its creation
	 * @param servers List of servers. Can be null.
	 * @return AlertsWidget
	 */
	public AlertsWidget getAlertsWidget(int widgetId, List<MuninServer> servers) {
		String selectQuery = "SELECT * FROM " + TABLE_WIDGET_ALERTSWIDGETS
				+ " WHERE " + KEY_WIDGET_ALERTSWIDGETS_WIDGETID + " = " + widgetId;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null && c.moveToFirst()) {
			AlertsWidget w = new AlertsWidget();
			w.setId(c.getInt(c.getColumnIndex(KEY_ID)));
			w.setWidgetId(c.getInt(c.getColumnIndex(KEY_WIDGET_ALERTSWIDGETS_WIDGETID)));
			w.setWifiOnly(c.getInt(c.getColumnIndex(KEY_WIDGET_ALERTSWIDGETS_WIFIONLY)));

			// Get servers
			if (servers == null)
				servers = getServers(null);

			w.setServers(getAlertsWidgetRelations(w.getId(), servers));

			close(c, db);
			return w;
		}

		close(c, db);
		return null;
	}

	public List<MuninServer> getAlertsWidgetRelations(long widgetId, List<MuninServer> servers) {
		List<MuninServer> widgetServers = new ArrayList<>();

		String selectQuery = "SELECT * FROM " + TABLE_WIDGET_ALERTSWIDGETSRELATIONS
				+ " WHERE " + KEY_WIDGET_ALERTSWIDGETSRELATIONS_WIDGET + " = " + widgetId;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null && c.moveToFirst()) {
			do {
				int serverId = c.getInt(c.getColumnIndex(KEY_WIDGET_ALERTSWIDGETSRELATIONS_SERVER));
				// Find server
				MuninServer s = null;

				for (MuninServer server : servers) {
					if (server.getId() == serverId) {
						s = server;
						break;
					}
				}

				if (s != null)
					widgetServers.add(s);
			} while (c.moveToNext());
		}

		close(c, db);
		return widgetServers;
	}
	
	public GraphWidget getGraphWidget(int widgetId) {
		String selectQuery = "SELECT * FROM " + TABLE_WIDGET_GRAPHWIDGETS
				+ " WHERE " + KEY_WIDGET_GRAPHWIDGETS_WIDGETID + " = " + widgetId;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		if (c != null && c.moveToFirst()) {
			GraphWidget w = new GraphWidget();
			w.setId(c.getInt(c.getColumnIndex(KEY_ID)));
			w.setPeriod(c.getString(c.getColumnIndex(KEY_WIDGET_GRAPHWIDGETS_PERIOD)));
			w.setWidgetId(c.getInt(c.getColumnIndex(KEY_WIDGET_GRAPHWIDGETS_WIDGETID)));
			
			// Get plugin, and master (server is fetched with getPlugin)
			MuninPlugin plugin = getPlugin(c.getInt(c.getColumnIndex(KEY_WIDGET_GRAPHWIDGETS_PLUGIN)));
			MuninMaster master = getMasterFromServer(plugin.getInstalledOn().getId());
			plugin.getInstalledOn().setParent(master);
			
			w.setPlugin(plugin);
			w.setWifiOnly(c.getInt(c.getColumnIndex(KEY_WIDGET_GRAPHWIDGETS_WIFIONLY)));
			w.setHideServerName(c.getInt(c.getColumnIndex(KEY_WIDGET_GRAPHWIDGETS_HIDESERVERNAME)));
			close(c, db);
			return w;
		}
		return null;
	}
	
	public List<Label> getLabels(List<MuninMaster> masters) {
		List<Label> list = new ArrayList<>();
		try {
			String selectQuery = "SELECT * FROM " + TABLE_LABELS;
			
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor c = db.rawQuery(selectQuery, null);
			
			if (c != null && c.moveToFirst()) {
				do {
					Label l = new Label();
					l.setId(c.getInt(c.getColumnIndex(KEY_ID)));
					l.setName(c.getString(c.getColumnIndex(KEY_LABELS_NAME)));
					l.setPlugins(getPlugins(l, masters));
					list.add(l);
				} while (c.moveToNext());
			}
			close(c, db);
		} catch (Exception ex) {
			Crashlytics.logException(ex);
		}
		return list;
	}
	
	/**
	 * Get all plugins linked to a label
	 * @param label Label
	 * @param masters masters
	 * @return List<MuninPlugin>
	 */
	public List<MuninPlugin> getPlugins(Label label, List<MuninMaster> masters) {
		List<MuninPlugin> list = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + TABLE_LABELSRELATIONS
				+ " WHERE " + KEY_LABELSRELATIONS_LABEL + " = " + label.getId();
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		if (c != null && c.moveToFirst()) {
			do {
				// Find plugin
				long pluginId = c.getInt(c.getColumnIndex(KEY_LABELSRELATIONS_PLUGIN));
				MuninPlugin plugin = MuninPlugin.findFromMastersList(pluginId, masters);
				if (plugin == null)
					plugin = getPlugin(pluginId);

				list.add(plugin);
			} while (c.moveToNext());
		}
		close(c, db);
		return list;
	}
	
	public List<Grid> getGrids(MuninFoo f) {
		List<Grid> l = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + TABLE_GRIDS;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		if (c != null && c.moveToFirst()) {
			do {
				Grid g = new Grid(c.getString(c.getColumnIndex(KEY_GRIDS_NAME)));
				g.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				// Get all GridItems
				g.setItems(getGridItems(f, g));
				l.add(g);
			} while (c.moveToNext());
		}
		close(c, db);
		return l;
	}

	public boolean hasGrids() {
		return GenericQueries.getNbLines(this, TABLE_GRIDS, "") > 0;
	}
	
	public List<String> getGridsNames() {
		List<String> names = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + TABLE_GRIDS;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		if (c != null && c.moveToFirst()) {
			do {
				names.add(c.getString(c.getColumnIndex(KEY_GRIDS_NAME)));
			} while (c.moveToNext());
		}
		close(c, db);
		return names;
	}

	/**
	 * Get a grid from its id
	 * @param muninFoo MuninFoo instance
	 * @param gridId Grid id
	 * @return Grid
	 */
	public Grid getGrid(MuninFoo muninFoo, long gridId) {
		String selectQuery = "SELECT * FROM " + TABLE_GRIDS
				+ " WHERE " + KEY_ID + " = " + gridId;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null && c.moveToFirst()) {
			Grid g = new Grid(c.getString(c.getColumnIndex(KEY_GRIDS_NAME)));
			g.setId(c.getInt(c.getColumnIndex(KEY_ID)));
			// Get all GridItems
			g.setItems(getGridItems(muninFoo, g));

			close(c, db);
			return g;
		}
		return null;
	}
	
	/**
	 * Get all grid items from a Grid
	 * @param grid Grid
	 * @return List<GridItem>
	 */
	public List<GridItem> getGridItems(MuninFoo muninFoo, Grid grid) {
		List<GridItem> l = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + TABLE_GRIDITEMRELATIONS
				+ " WHERE " + KEY_GRIDITEMRELATIONS_GRID + " = " + grid.getId();
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		if (c != null && c.moveToFirst()) {
			do {
				int pluginId = c.getInt(c.getColumnIndex(KEY_GRIDITEMRELATIONS_PLUGIN));
				MuninPlugin plugin = muninFoo.getPlugin(pluginId);
				GridItem i = new GridItem(grid, plugin);
				i.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				i.setX(c.getInt(c.getColumnIndex(KEY_GRIDITEMRELATIONS_X)));
				i.setY(c.getInt(c.getColumnIndex(KEY_GRIDITEMRELATIONS_Y)));
				l.add(i);
			} while (c.moveToNext());
		}
		close(c, db);
		return l;
	}

	public void deleteMaster(MuninMaster m, boolean deleteChildren) { deleteMaster(m, deleteChildren, null); }
	public void deleteMaster(MuninMaster m, boolean deleteChildren, Util.ProgressNotifier progressNotifier) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_MUNINMASTERS, KEY_ID + " = ?", new String[] { String.valueOf(m.getId()) });
		close(null, db);
		
		if (deleteChildren) {
			for (MuninServer s : m.getChildren()) {
				deleteServer(s);
				if (progressNotifier != null)
					progressNotifier.notify(m.getChildren().indexOf(s)+1, m.getChildren().size());
			}
		}
	}
	
	public void deleteServer(MuninServer s) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_MUNINSERVERS, KEY_ID + " = ?", new String[] { String.valueOf(s.getId()) });
		close(null, db);
		deletePlugins(s);
	}
	
	public void deletePlugin(MuninPlugin p) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_MUNINPLUGINS, KEY_ID + " = ?", new String[] { String.valueOf(p.getId()) });
	}
	
	public void deletePlugins(MuninServer s) {
		List<MuninPlugin> l = getPlugins(s);
		for (MuninPlugin p : l) {
			deleteGraphWidgets(p);
			deleteLabelsRelations(p);
			deleteGridItemRelations(p);
		}
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_MUNINPLUGINS, KEY_MUNINPLUGINS_SERVER + " = ?", new String[] { String.valueOf(s.getId()) });
		close(null, db);
	}
	
	public void deleteGraphWidgets(MuninPlugin p) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_WIDGET_GRAPHWIDGETS, KEY_WIDGET_GRAPHWIDGETS_PLUGIN + " = ?", new String[] { String.valueOf(p.getId()) });
		close(null, db);
	}
	
	public void deleteGraphWidget(int appWidgetId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_WIDGET_GRAPHWIDGETS, KEY_WIDGET_GRAPHWIDGETS_WIDGETID + " = ?", new String[] { String.valueOf(appWidgetId) });
		close(null, db);
	}

	public void deleteAlertsWidget(int appWidgetId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_WIDGET_ALERTSWIDGETS, KEY_WIDGET_ALERTSWIDGETS_WIDGETID + " = ?", new String[] { String.valueOf(appWidgetId) });
		close(null, db);
	}
	
	public void deleteLabelsRelations(MuninPlugin p) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_LABELSRELATIONS, KEY_LABELSRELATIONS_PLUGIN + " = ?", new String[] { String.valueOf(p.getId()) });
		close(null, db);
	}
	
	public void deleteGrid(Grid g) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_GRIDS, KEY_ID + " = ?", new String[] { String.valueOf(g.getId()) });
		close(null, db);
		deleteGridItemRelations(g);
	}
	
	public void deleteGridItemRelations(Grid g) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_GRIDITEMRELATIONS, KEY_GRIDITEMRELATIONS_GRID + " = ?", new String[] { String.valueOf(g.getId()) });
		close(null, db);
	}
	
	public void deleteGridItemRelation(GridItem i) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_GRIDITEMRELATIONS, KEY_ID + " = ?", new String[] { String.valueOf(i.getId()) });
		close(null, db);
	}
	
	public void deleteGridItemRelations(MuninPlugin p) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_GRIDITEMRELATIONS, KEY_GRIDITEMRELATIONS_PLUGIN + " = ?", new String[] { String.valueOf(p.getId()) });
		close(null, db);
	}
	
	// DROP
	public void deleteLabelsRelations() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LABELSRELATIONS);
		db.execSQL(CREATE_TABLE_LABELSRELATIONS);
		close(null, db);
	}
	public void deleteLabels() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LABELS);
		db.execSQL(CREATE_TABLE_LABELS);
		close(null, db);
	}
	public void deleteGraphWidgets() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WIDGET_GRAPHWIDGETS);
		db.execSQL(CREATE_TABLE_GRAPHWIDGETS);
		close(null, db);
	}
	public void deleteMuninPlugins() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MUNINPLUGINS);
		db.execSQL(CREATE_TABLE_MUNINPLUGINS);
		close(null, db);
	}
	public void deleteMuninServers() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MUNINSERVERS);
		db.execSQL(CREATE_TABLE_MUNINSERVERS);
		close(null, db);
	}
	public void deleteMuninMasters() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MUNINMASTERS);
		db.execSQL(CREATE_TABLE_MUNINMASTERS);
		close(null, db);
	}
	public void deleteGrids() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GRIDS);
		db.execSQL(CREATE_TABLE_GRIDS);
		close(null, db);
	}
	public void deleteGridItemRelations() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GRIDITEMRELATIONS);
		db.execSQL(CREATE_TABLE_GRIDITEMRELATIONS);
		close(null, db);
	}
	
	
	public static class GenericQueries {
		/**
		 * Returns the number of lines returned by a SELECT COUNT(*) FROM _table WHERE _cond
		 * Ex : where = 'ID = 5'
		 * @param table Table name
		 * @param where Where condition (ID = 5)
		 * @return int nbLines
		 */
		public static int getNbLines(SQLiteOpenHelper sqloh, String table, String where) {
			String query = "SELECT COUNT(*) FROM " + table + (where.equals("") ? "" : (" WHERE " + where));
			SQLiteDatabase db = sqloh.getReadableDatabase();
			Cursor cursor = db.rawQuery(query, null);
			try {
				cursor.moveToNext();
				int val = cursor.getInt(0);
				cursor.close();
				return val;
			} catch (Exception e) { e.printStackTrace(); }
			return 0;
		}
	}
}