# model-viewer
A 3D model viewer with pseudo-colouring using the JOGL lib in Java

The program uses CoolWarmFloat257.csv to map a range to colours

jet_surface.dat is a sample of the model format that has been derived from the Tecplot ASCII Data format (http://paulbourke.net/dataformats/tp/ascii_format.pdf.gz) each point contains x,y,z variables and one or more extra variables, all the variables can be used for the colouring.

Usage:<br/>
Run with the model filename as first argument to the program
* Left click drag translates x and y
* Left and Right click drag translates z
* Right click drag rotates around x and y axes
  * while shift is held lock rotate around z axis
  * while ctrl is held lock rotate around y axis
  * while alt is held lock rotate around x axis

![Screenshot](https://user-images.githubusercontent.com/4456870/28753125-c75ecedc-7561-11e7-87a7-d678a053b9ba.png)

Previously submitted to Monash University
