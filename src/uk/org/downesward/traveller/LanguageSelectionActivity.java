package uk.org.downesward.traveller;

import java.io.IOException;
import java.io.InputStream;

import uk.org.downesward.traveller.language.Language;
import uk.org.downesward.traveller.language.Languages;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class LanguageSelectionActivity extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		Resources res = getResources();
		super.onCreate(savedInstanceState);
		
		// A contact was picked.  Here we will just display it
        // to the user. 
		AssetManager am = getAssets();
		try {
			Languages langs = new Languages();
			Language oLang;
			InputStream lang;

			String[] files = am.list("languages");
			for (int i = 0; i < files.length; i++) {
				lang = am.open("languages/" + files[i]);
				oLang = new Language(lang);
				oLang.setLanguage(files[i].substring(0, files[i].length() - 4));
				langs.put(oLang.getLanguage(), oLang);
			}
			String[] langList = new String[langs.size()];
			int i = 0;
			for (Language langItem : langs.values()) {
				langList[i++] = langItem.getLanguage();
			}

			ArrayAdapter<String> list = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, langList);
			list.sort(String.CASE_INSENSITIVE_ORDER);
			this.setListAdapter(list);

		} catch (IOException io) {
			Toast.makeText(
					this,
					String.format(
							res.getString(R.string.language_io_exception),
							io.getMessage()), Toast.LENGTH_LONG).show();
		}
		catch (Exception e)
		{
			Toast.makeText(
					this,
					String.format(
							res.getString(R.string.language_io_exception),
							e.getMessage()), Toast.LENGTH_LONG).show();			
		}		

    }
    
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Bundle b = new Bundle();
		String item = (String) getListAdapter().getItem(position);
		b.putString("LANGUAGE", item);
		Intent intent = this.getIntent();
		intent.putExtras(b);

		this.setResult(RESULT_OK, intent);
		finish();
	}    
}