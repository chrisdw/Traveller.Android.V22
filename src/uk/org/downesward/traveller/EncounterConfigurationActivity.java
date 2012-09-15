package uk.org.downesward.traveller;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import uk.org.downesward.traveller.common.UPP;
import uk.org.downesward.traveller.encounters.TableGenerator;
import uk.org.downesward.utiliites.XMLUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class EncounterConfigurationActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.encounterconfiguration);

		String[] hexValues = { "0", "1", "2", "3", "4", "5", "6", "7", "8",
				"9", "A", "B", "C", "D", "E", "F" };

		String[] decValues = { "0", "1", "2", "3", "4", "5", "6", "7", "8",
				"9", "A" };
		
		ArrayAdapter<String> hexList = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, hexValues);
		hexList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ArrayAdapter<String> decList = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, decValues);
		decList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner numPick = (Spinner) this.findViewById(R.id.numAtmos);
		numPick.setAdapter(hexList);		

		numPick = (Spinner) this.findViewById(R.id.numSize);
		numPick.setAdapter(decList);		

		numPick = (Spinner) this.findViewById(R.id.numHydro);
		numPick.setAdapter(decList);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.encountergeneration, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.menuGen1D6) {
			TableGenerator tg = new TableGenerator();
			tg.Generate(1, getUPP());

			displayResults(tg);
		} else if (itemId == R.id.menuGen2D6) {
			TableGenerator tg = new TableGenerator();
			tg.Generate(2, getUPP());

			displayResults(tg);
		} else {
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void displayResults(TableGenerator tg) {
		Resources res = getResources();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder parser = factory.newDocumentBuilder();
			Document doc = parser.newDocument();
			Element root = doc.createElement("encounters");
			tg.WriteToXML(root);
			doc.appendChild(root);
			
			Bundle b = new Bundle();
			b.putString("ENCOUNTERS", XMLUtilities.serializeDoc(doc));

			Intent intent = new Intent(EncounterConfigurationActivity.this,
					EncounterDisplayActivity.class);
			intent.putExtras(b);
			startActivity(intent);
		} catch (ParserConfigurationException e) {
			Toast.makeText(
					this,
					String.format(
							res.getString(R.string.language_io_exception),
							e.getMessage()), Toast.LENGTH_LONG).show();
		} catch (TransformerException e) {
			Toast.makeText(
					this,
					String.format(
							res.getString(R.string.language_io_exception),
							e.getMessage()), Toast.LENGTH_LONG).show();
		}
	}

	private UPP getUPP() {
		UPP upp = new UPP();
		Spinner numPick = (Spinner) this.findViewById(R.id.numAtmos);
		upp.atmosphere.setValue(Integer.parseInt(numPick.getSelectedItem().toString(), 16));
		numPick = (Spinner) this.findViewById(R.id.numSize);
		upp.size.setValue(Integer.parseInt(numPick.getSelectedItem().toString(), 16));
		numPick = (Spinner) this.findViewById(R.id.numHydro);
		upp.hydro.setValue(Integer.parseInt(numPick.getSelectedItem().toString(), 16));
		return upp;
	}
}
