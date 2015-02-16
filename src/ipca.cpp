#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>

#include "dir_utils.hpp"

using namespace cv;
using namespace std;

int IPCAtrain(const char* trainFolderPath)
{
    /* trainFolderPath is the path to the folder containing the training images */
    string p (trainFolderPath);
    // map_dir(p, print_file_name, print_all_files);
    // map_dirs(p, print_all_files);
    // map_files(p, print_file_name);

    // Run a loop to iterate over classes (people)
    for(int c = 0; c < 0; c++)
    {
        //Run a loop to iterate over images of same person and generate the data matrix for the class
        for(int im = 0; im < 0; im++)
        {

        }
        //i.e. a matrix in which each column is a vectorized version of the face matrix under consideration

        // Subtract the mean vector from each vector of the data matrix

        // Compute the covariance matrix and generate the eigen vectors using the Gram trick

        // Store the eigen vectors and the mean vector in a file, which will be accessed by the IPCAtest function

    }

    return 42;
}

int IPCAtest(char *imgName)
{
    /* imgName is the path and filename of the test image */

    // Read the eigen vectors and means for each class from file

    // Project the input test image onto each eigen space and reconstruct

    // Compute the reconstruction error between the input test image and the reconstructed image
    // You can use euclidean distance (or any other appropriate distance measure)

    // return the class label corresponding to the eigen space which showed minimum reconstruction error

    return 42;
}
