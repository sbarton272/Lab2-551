/*** 18551 Homework 2 Starter Code ******/

// Please add your own comments to the code before you submit

#include <iostream>
#include <string>
#include <fstream>

#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>

using namespace cv;
using namespace std;

int main( int argc, char** argv )
{
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

void IPCAtrain(char* trainFolderPath, int numTrain)
{
    /* trainFolderPath is the path to the folder containing the training images
       numTrain is the number of training images per class */

    // Run a loop to iterate over classes (people)
    for(;;)
    {
        //Run a loop to iterate over images of same person and generate the data matrix for the class
        //i.e. a matrix in which each column is a vectorized version of the face matrix under consideration

        // Subtract the mean vector from each vector of the data matrix

        // Compute the covariance matrix and generate the eigen vectors using the Gram trick

        // Store the eigen vectors and the mean vector in a file, which will be accessed by the IPCAtest function

    }

}

int IPCAtest(char *imgName)
{
    /* imgName is the path and filename of the test image */

    // Read the eigen vectors and means for each class from file

    // Project the input test image onto each eigen space and reconstruct

    // Compute the reconstruction error between the input test image and the reconstructed image
    // You can use euclidean distance (or any other appropriate distance measure)

    // return the class label corresponding to the eigen space which showed minimum reconstruction error

    return 0;
}
