package rts.pptviewer;

import com.itsrts.pptviewer.PPTViewer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;

public class MainActivity extends Activity {

	PPTViewer pptViewer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pptViewer = (PPTViewer) findViewById(R.id.pptviewer);
		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/Download/ssadagopan.ppt";
		pptViewer.setNext_img(R.drawable.next).setPrev_img(R.drawable.prev)
				.setSettings_img(R.drawable.settings)
				.setZoomin_img(R.drawable.zoomin)
				.setZoomout_img(R.drawable.zoomout);
		pptViewer.loadPPT(this, path);

	}
}
