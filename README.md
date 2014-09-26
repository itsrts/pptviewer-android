pptviewer
=========

A ppt viewer library, which can be easily used in any existing android application.

Hello folks,
I was working on a project for wireless presentation and i need to embedd a PPT viewer in it.
I searched for some existing library but found none of them to be developer friendly.
So here is a custom library made by me for seamless integration of a PPT viewer in any existing application.

All you need to do is:

1. Download the library of mine (of course from GitHub).

2. Somehow add it to your existing app, that can be done in any ways.
      a. Copy and Paste it in a folder named as "libs" in your project root directory.
      b. Add it to your build path by right clicking on your project and "Configure build ......."

3. You must be having an Activity and an XML file for it.

4. Now open the layout XML file and add the following line of code in it.
      the xml code for including the pptviewer

5. Goto the main activity file which will be inflating the above XML and after the "setContentView", add:
      the code to get the reference of the pptviewer in the activity
      and load a PPT file by providing the path to the reference.
