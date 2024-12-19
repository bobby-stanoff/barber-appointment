package vn.something.barberfinal.DataModel;

import android.content.Context;
import android.content.SharedPreferences;

public class ShopPreferences {
    private static final String PREF_NAME = "ShopPrefs";
    private static final String KEY_SHOP_ID = "shop_id";
    private static final String KEY_SHOP_PAGE_TOKEN = "shopPageToken";
    private SharedPreferences prefs;

    public ShopPreferences(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveShopId(String shopId) {
        prefs.edit().putString(KEY_SHOP_ID, shopId).apply();
    }

    public String getShopId() {
        return prefs.getString(KEY_SHOP_ID, null);
    }
}