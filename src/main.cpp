/*** 18551 Homework 2 Starter Code ******/

// Please add your own comments to the code before you submit

#include <iostream>
#include <string>
#include <fstream>

#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>

#include "dir_utils.hpp"
#include "ipca.hpp"

using namespace cv;
using namespace std;

int main( int argc, char** argv )
{
    IPCAtrain(".");
    if( argc != 2)
    {
     cout <<" Usage: display_image ImageToLoadAndDisplay" << endl;
     return -1;
    }

    Mat image;
    image = imread(argv[1], CV_LOAD_IMAGE_COLOR);   // Read the file

    if(! image.data )                              // Check for invalid input
    {
        cout <<  "Could not open or find the image" << std::endl ;
        return -1;
    }

    namedWindow( "Display window", WINDOW_AUTOSIZE );// Create a window for display.
    imshow( "Display window", image );                   // Show our image inside it.

    waitKey(0);                                          // Wait for a keystroke in the window
    return 0;


    // Call IPCAtrain to generate a text file containing eigenvectors and means of each clas

    // Call IPCAtest using a test function and see if it returns the correct class label

    // We will be testing on our own set of test images after you submit the code

}
