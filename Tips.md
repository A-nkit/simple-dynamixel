# Introduction #

To talk to the dynamixel we use the [USB2Dynamixel](http://support.robotis.com/en/product/auxdevice/interface/usb2dxl_manual.htm). This adapter uses the [FT232R](http://www.ftdichip.com/Products/ICs/FT232R.htm) chip.
The FTDI drivers on OSX have a default latency of 16ms. To change this behavior you have to change the driver like described in this [document](https://www.google.com/url?q=http://www.ftdichip.com/Documents/TechnicalNotes/TN_105%2520Adding%2520Support%2520for%2520New%2520FTDI%2520Devices%2520to%2520Mac%2520Driver.pdf&sa=U&ei=3y1UUO6KIMKk0AWm7IHYAg&ved=0CAYQFjAA&client=internal-uds-cse&usg=AFQjCNGiKAA-8cGM0jMNkrHasvnIyifDqw).




# Step by Step #

  * Copy the FTDI driver to a local folder, you can find the driver at
```
/System/Library/Extensions/FTDIUSBSerialDriver.kext
```
  * Download this [file](http://simple-dynamixel.googlecode.com/svn/trunk/ext/Info.plist.zip) and unzip it
  * Copy the file into the FTDI Driver. Go to the finder and show the package content(right mouse click)
  * copy the file to
```
/Contents
```
  * Open the terminal and type:
```
$ touch /yourPath/FTDIUSBSerialDriver.kext
```
  * Copy the driver to this system folder
```
$ sudo cp -r /yourPath/FTDIUSBSerialDriver.kext /System/Library/Extensions/
```
  * Now restart your computer