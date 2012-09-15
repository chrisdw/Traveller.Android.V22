package uk.org.downesward.traveller;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class WordDisplayActivity extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		Bundle b = getIntent().getExtras();
		String[] words = b.getStringArray("WORDLIST");
		
		ArrayAdapter<String> list = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, words);
		list.sort(String.CASE_INSENSITIVE_ORDER);
		this.setListAdapter(list);

	}
}
