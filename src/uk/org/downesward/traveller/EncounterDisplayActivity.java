package uk.org.downesward.traveller;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import uk.org.downesward.traveller.encounters.Critter;
import uk.org.downesward.utiliites.XMLUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.lamerman.FileDialog;
import com.lamerman.SelectionMode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EncounterDisplayActivity extends ListActivity implements
		OnItemSelectedListener {

	private static final int REQUEST_SAVE = 0;
	private static final int REQUEST_OPEN = 1;

	private ArrayList<Critter> m_critters = new ArrayList<Critter>();
	private CritterAdapter m_adapter;
	private NodeList regions;
	private Document doc;
	private ArrayAdapter<String> regionAdapater;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Resources res = getResources();

		setContentView(R.layout.encounterdisplay);

		this.m_adapter = new CritterAdapter(this, R.layout.critter,
				m_critters);

		// Get the XML from the bundle
		Bundle b = getIntent().getExtras();
		String xml = b.getString("ENCOUNTERS");
		if (xml.length() == 0) {
			getEncounterFile();		
		}
		else {
			// turn it into a DOM
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser;
			try {
				parser = factory.newDocumentBuilder();
				doc = parser.parse(new InputSource(new StringReader(xml)));
	
				regions = doc.getElementsByTagName("Region");
	
				String[] regionList = extractRegionList();
	
				Spinner spinner = (Spinner) findViewById(R.id.spnRegion);
				regionAdapater = new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, regionList);
				regionAdapater.sort(String.CASE_INSENSITIVE_ORDER);
				spinner.setAdapter(regionAdapater);
				spinner.setOnItemSelectedListener(this);
				setListAdapter(this.m_adapter);
	
				spinner.setSelection(0);
			} catch (ParserConfigurationException e) {
				Toast.makeText(
						this,
						String.format(
								res.getString(R.string.language_io_exception),
								e.getMessage()), Toast.LENGTH_LONG).show();
			} catch (SAXException e) {
				Toast.makeText(
						this,
						String.format(
								res.getString(R.string.language_io_exception),
								e.getMessage()), Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (IOException e) {
				Toast.makeText(
						this,
						String.format(
								res.getString(R.string.language_io_exception),
								e.getMessage()), Toast.LENGTH_LONG).show();
			}
		}
	}

	private String[] extractRegionList() {
		String[] regionList = new String[regions.getLength()];

		for (int i = 0; i < regions.getLength(); i++) {
			Element region = (Element) regions.item(i);
			regionList[i] = region.getElementsByTagName("name").item(0)
					.getTextContent();
		}
		return regionList;
	}

	private class CritterAdapter extends ArrayAdapter<Critter> {
		private ArrayList<Critter> items;

		public CritterAdapter(Context context, int textViewResourceId,
				ArrayList<Critter> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.critter, null);
			}
			Critter o = items.get(position);
			if (o != null) {
				TextView tt = (TextView) v.findViewById(R.id.txtCritterDie);
				TextView bt = (TextView) v.findViewById(R.id.txtCritterAttrib);
				TextView descr = (TextView) v
						.findViewById(R.id.txtCritterDescr);
				TextView weight = (TextView) v
						.findViewById(R.id.txtCritterWeight);
				TextView armour = (TextView) v
						.findViewById(R.id.txtCritterArmour);
				TextView weapons = (TextView) v
						.findViewById(R.id.txtCritterWeapons);
				TextView wounds = (TextView) v
						.findViewById(R.id.txtCritterWounds);
				TextView behaviour = (TextView) v
						.findViewById(R.id.txtCritterBehaviour);
				TextView family = (TextView) v
						.findViewById(R.id.txtCritterFamily);
				if (tt != null) {
					tt.setText(o.die);
				}
				if (bt != null) {
					bt.setText(o.attrib);
				}
				if (descr != null) {
					descr.setText(o.description);
				}
				if (weight != null) {
					weight.setText(o.weight);
				}
				if (armour != null) {
					armour.setText(o.armour);
				}
				if (weapons != null) {
					weapons.setText(o.weapons);
				}
				if (wounds != null) {
					wounds.setText(o.wounds);
				}
				if (behaviour != null) {
					behaviour.setText(o.behaviour);
				}
				if (family != null) {
					family.setText(o.family);
				}
			}
			return v;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.encounterdisplay, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.mnu_encounter_save) {
			Intent intent = new Intent(this.getBaseContext(), FileDialog.class);
			intent.putExtra(FileDialog.START_PATH,
					Environment.getExternalStorageDirectory());
			this.startActivityForResult(intent, REQUEST_SAVE);

		} else if (itemId == R.id.mnu_encounter_open) {
			getEncounterFile();
		} else {
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void getEncounterFile() {
		Intent intent = new Intent(this.getBaseContext(), FileDialog.class);
		intent.putExtra(FileDialog.START_PATH,
				Environment.getExternalStorageDirectory());
		intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);
		this.startActivityForResult(intent, REQUEST_OPEN);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Resources res = getResources();

		if (requestCode == REQUEST_SAVE) {
			if (resultCode == RESULT_OK) {
				try
				{
					Bundle b = data.getExtras();
					if (b != null) {
						String path = data.getStringExtra(FileDialog.RESULT_PATH);
						try {
							XMLUtilities.writeXmlFile(doc, path);
						} catch (TransformerFactoryConfigurationError e) {
							Bundle eb = new Bundle();
							b.putString("Exception", e.getLocalizedMessage());
							this.showDialog(0, eb);
						} catch (TransformerException e) {
							Bundle eb = new Bundle();
							b.putString("Exception", e.getLocalizedMessage());
							this.showDialog(0, eb);
						}
					}
				} catch (Exception e) {
					Bundle b = new Bundle();
					b.putString("Exception", e.getLocalizedMessage());
					this.showDialog(0, b);
				}
			} else {
				this.finish();
			}
		} else if (requestCode == REQUEST_OPEN) {
			if (resultCode == RESULT_OK) {
				String path = data.getStringExtra(FileDialog.RESULT_PATH);
				try {
					doc = XMLUtilities.readXmlFile(path);
					String[] regionList = extractRegionList();
					regionAdapater.clear();
					for (int i = 0; i < regionList.length; i++) {
						regionAdapater.add(regionList[i]);					
					}
					regionAdapater.notifyDataSetChanged();
				} catch (SAXException e) {
					Bundle b = new Bundle();
					b.putString("Exception", e.getLocalizedMessage());
					this.showDialog(0, b);
				} catch (IOException e) {
					Bundle b = new Bundle();
					b.putString("Exception", e.getLocalizedMessage());
					this.showDialog(0, b);
				} catch (ParserConfigurationException e) {
					Bundle b = new Bundle();
					b.putString("Exception", e.getLocalizedMessage());
					this.showDialog(0, b);
				} catch (Exception e) {
					Bundle b = new Bundle();
					b.putString("Exception", e.getLocalizedMessage());
					this.showDialog(0, b);
				}
			}
		}
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		String regionName = parent.getItemAtPosition(pos).toString();
		if (regions != null) {
			for (int i = 0; i < regions.getLength(); i++) {
				Element region = (Element) regions.item(i);
				if (region.getElementsByTagName("name").item(0)
						.getTextContent().equals(regionName)) {
					m_adapter.clear();
					NodeList critters = region.getElementsByTagName("critter");
					for (int j = 0; j < critters.getLength(); j++) {
						Element critter = (Element) critters.item(j);
						Critter oCritter = new Critter(critter);

						m_adapter.add(oCritter);
					}
					break;
				}
			}
			m_adapter.notifyDataSetChanged();
		}
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		m_adapter.clear();
		m_adapter.notifyDataSetChanged();
	}
	
	protected Dialog onCreateDialog(int id, Bundle b) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("An exception has occured" + b.getString("Exception"))
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   EncounterDisplayActivity.this.finish();
		           }
		       });
		AlertDialog alert = builder.create();
		return alert;
	}
}
