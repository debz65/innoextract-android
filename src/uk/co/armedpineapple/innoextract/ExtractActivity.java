package uk.co.armedpineapple.innoextract;

import java.io.File;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ExtractActivity extends Activity {

	private final String LOG_TAG = "ExtractActivity";

	EditText extractToEditText;
	Button extractButton;
	Button cancelButton;
	Button browseButton;

	String fileToExtract;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_layout);

		extractToEditText = (EditText) findViewById(R.id.extract_to);
		extractButton = (Button) findViewById(R.id.extract);
		cancelButton = (Button) findViewById(R.id.cancel);
		browseButton = (Button) findViewById(R.id.browse);

		String extDir;

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			extDir = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		} else {
			extDir = getFilesDir().getAbsolutePath();
		}

		extractToEditText.setText(extDir + "/extracted");

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ExtractActivity.this.finish();
			}
		});

		extractButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doExtract();
				ExtractActivity.this.finish();
			}
		});

		browseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showBrowseActivity();
			}

		});

		Intent intent = getIntent();
		Uri uri = intent.getData();

		fileToExtract = uri.getPath();

	}

	private void doExtract() {
		Intent serviceIntent = new Intent(this, ExtractService.class);
		serviceIntent.putExtra(ExtractService.EXTRACT_FILE_PATH, fileToExtract);
		serviceIntent.putExtra(ExtractService.EXTRACT_FILE_NAME, new File(
				fileToExtract).getName());
		serviceIntent.putExtra(ExtractService.EXTRACT_DIR, extractToEditText
				.getText().toString());
		startService(serviceIntent);

	}

	private void showBrowseActivity() {
		final Intent chooserIntent = new Intent(this,
				DirectoryChooserActivity.class);

		// Optional: Allow users to create a new directory with a fixed
		// name.
		chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_NEW_DIR_NAME,
				"extracted");

		// REQUEST_DIRECTORY is a constant integer to identify the
		// request, e.g. 0
		startActivityForResult(chooserIntent, 111);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 111) {
			if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
				extractToEditText
						.setText(data
								.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
			}
		}

	}

}
