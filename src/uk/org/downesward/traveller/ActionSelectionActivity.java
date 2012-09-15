package uk.org.downesward.traveller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActionSelectionActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actionselection);
		Button button = (Button) this.findViewById(R.id.butLangauge);

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ActionSelectionActivity.this,
						LanguageConfigurationActvity.class);
				startActivity(intent);
			}         
		});

		button = (Button) this.findViewById(R.id.butEncounters);

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ActionSelectionActivity.this,
						EncounterConfigurationActivity.class);
				startActivity(intent);
			}         
		});
		
		button = (Button) this.findViewById(R.id.butSystemViewer);

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ActionSelectionActivity.this,
						SystemDisplayActivity.class);
				startActivity(intent);
			}         
		});		
	}
}
