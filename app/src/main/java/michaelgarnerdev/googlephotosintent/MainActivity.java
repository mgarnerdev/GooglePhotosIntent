package michaelgarnerdev.googlephotosintent;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	private static final String GOOGLE_PHOTOS_PACKAGE_NAME = "com.google.android.apps.photos";
	private static final int REQUEST_PHOTO_FROM_GOOGLE_PHOTOS = 100;
	private ImageView ivDisplay;
	private Button btnOpenGooglePhotos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ivDisplay = (ImageView) findViewById(R.id.iv_display);
		btnOpenGooglePhotos = (Button) findViewById(R.id.btn_open_google_photos);
		btnOpenGooglePhotos.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_PHOTO_FROM_GOOGLE_PHOTOS) {
				Uri selectedimg = data.getData();
				try {
					ivDisplay.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimg));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			showErrorMsgDialog("Something went wrong!");
			btnOpenGooglePhotos.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_open_google_photos:
				launchGooglePhotosPicker(this);
				btnOpenGooglePhotos.setVisibility(View.GONE);
				break;
		}
	}

	public boolean isGooglePhotosInstalled() {
		PackageManager packageManager = getPackageManager();
		try {
			return packageManager.getPackageInfo(GOOGLE_PHOTOS_PACKAGE_NAME, PackageManager.GET_ACTIVITIES) != null;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}

	public void launchGooglePhotosPicker(Activity callingActivity) {
		if (callingActivity != null && isGooglePhotosInstalled()) {
			Intent intent = getPackageManager().getLaunchIntentForPackage(GOOGLE_PHOTOS_PACKAGE_NAME);
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			try {
				//callingActivity.startActivity(intent);
				startActivityForResult(intent, REQUEST_PHOTO_FROM_GOOGLE_PHOTOS);
			} catch (ActivityNotFoundException e) {
				showErrorMsgDialog("You don't have Google Photos installed! Download it from the play store today.");
				e.printStackTrace();
			}
		}
	}

	public void showErrorMsgDialog(String message) {
		if (!isFinishing()) {
			new MaterialDialog.Builder(this)
					.content(message)
					.positiveText(android.R.string.ok)
					.show();
		}
	}
}
