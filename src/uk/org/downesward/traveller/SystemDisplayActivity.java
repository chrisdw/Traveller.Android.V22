package uk.org.downesward.traveller;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeStateManager;

import uk.org.downesward.utiliites.XMLUtilities;

import com.lamerman.FileDialog;
import com.lamerman.SelectionMode;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

public class SystemDisplayActivity extends Activity {
	private static final int REQUEST_OPEN = 1;
	private Document doc;
	private TreeStateManager<Long> manager = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Resources res = getResources();
		
		// Get the XML from the bundle
		Bundle b = getIntent().getExtras();
		String xml = b.getString("SYSTEM");
		if (xml.length() == 0) {
			getSystemFile();		
		}
		else {
			// turn it into a DOM
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser;
			try {
				parser = factory.newDocumentBuilder();
				doc = parser.parse(new InputSource(new StringReader(xml)));
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Resources res = getResources();
	
		if (requestCode == REQUEST_OPEN) {
			if (resultCode == RESULT_OK) {
				String path = data.getStringExtra(FileDialog.RESULT_PATH);
				try {
					doc = XMLUtilities.readXmlFile(path);
					
					manager = new InMemoryTreeStateManager<Long>();
					final TreeBuilder<Long> treeBuilder = new TreeBuilder<Long>(manager);

				} catch (SAXException e) {
					Toast.makeText(
							this,
							String.format(res
									.getString(R.string.language_io_exception),
									e.getMessage()), Toast.LENGTH_LONG).show();
				} catch (IOException e) {
					Toast.makeText(
							this,
							String.format(res
									.getString(R.string.language_io_exception),
									e.getMessage()), Toast.LENGTH_LONG).show();
				} catch (ParserConfigurationException e) {
					Toast.makeText(
							this,
							String.format(res
									.getString(R.string.language_io_exception),
									e.getMessage()), Toast.LENGTH_LONG).show();
				}
			}
		}
	}
	
	private void getSystemFile() {
		Intent intent = new Intent(this.getBaseContext(), FileDialog.class);
		intent.putExtra(FileDialog.START_PATH,
				Environment.getExternalStorageDirectory());
		intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);
		this.startActivityForResult(intent, REQUEST_OPEN);
	}
}
