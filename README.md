pptviewer
=========

NOTE : I have discontinued support for this project. It is provided as it basis.
A ppt viewer library, which can be easily used in any existing android application.

Hello folks,
I was working on a project for wireless presentation and i need to embedd a PPT viewer in it.
I searched for some existing library but found none of them to be developer friendly.
So here is a custom library made by me for seamless integration of a PPT viewer in any existing application.

All you need to do is:

1. Download the library of mine (of course from GitHub).

2. Somehow add it to your existing app, that can be done in any ways.<br/>
      a. Copy and Paste it in a folder named as "libs" in your project root directory.<br/>
      b. Add it to your build path by right clicking on your project and "Configure build ......."<br/>

3. You must be having an Activity and an XML file for it.

4. Now open the layout XML file and add the following line of code in it.<br/>
      < com.itsrts.pptviewer.PPTViewer<br/>
      android:id="@+id/pptviewer"<br/>
      android:width="match_parent"<br/>
      android:height="match_parent"<br/>
      /&gt;

5. Goto the main activity file which will be inflating the above XML and after the "setContentView", add:<br/>
      PPTViewer pptViewer = (PPTViewer) findViewById(R.id.pptviewer);<br/>
      pptViewer.setNext_img(R.drawable.next)<br/>
                  .setPrev_img(R.drawable.prev)<br/>
	            .setSettings_img(R.drawable.settings)<br/>
			.setZoomin_img(R.drawable.zoomin)<br/>
			.setZoomout_img(R.drawable.zoomout);<br/>
      pptViewer.loadPPT(activity, path);<br/>
      // path is a String having the location of the ppt file to be loaded.<br/>

The library is fully customizable with the option to change the viewer the way you want.
