#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <string>
#include <fstream>
#include <vector>
#include <tuple>

#include "dir_utils.hpp"

using namespace cv;
using namespace std;

#include <iostream>

void save(const string &file_name,PCA pca_)
{
    FileStorage fs(file_name,FileStorage::WRITE);
    fs << "mean" << pca_.mean;
    fs << "e_vectors" << pca_.eigenvectors;
    fs << "e_values" << pca_.eigenvalues;
    fs.release();
}

PCA load(const string &file_name)
{
    PCA pca_;
    FileStorage fs(file_name,FileStorage::READ);
    fs["mean"] >> pca_.mean ;
    fs["e_vectors"] >> pca_.eigenvectors ;
    fs["e_values"] >> pca_.eigenvalues ;
    fs.release();
    return pca_;
}


int IPCAtrain(const char* trainFolderPath, int numComponents)
{
    string path (trainFolderPath);

    // Run a loop to iterate (or just map) over classes (people)
    return map_dirs(path, [numComponents] (string dir_name, string path) {
        //Run a loop to iterate over images (or map) of same person and generate the data matrix for the class
        vector<Mat> data;
        int err = map_files(path + dir_name, [&data] (string img_name, string path) {
            //i.e. a matrix in which each column is a vectorized version of the face matrix under consideration
            // Subtract the mean vector from each vector of the data matrix (ie normalize it i guess)
            Mat im = imread(path + img_name, CV_LOAD_IMAGE_COLOR);
            if (im.data == NULL) return -1;

            Scalar mean;
            Scalar stddev;
            meanStdDev(im, mean, stddev);
            // cout << mean.val[0] << ", " << stddev.val[0] << endl;
            im = (im.reshape(1, 1) - mean.val[0]) / stddev.val[0];
            data.push_back(im);
            return 0;
        });

        Mat data_mat;
        vconcat(data, data_mat);
        // cout << "data rows/cols" << data_mat.rows << "," << data_mat.cols << endl;

        PCA pca = PCA(data_mat, Mat(), CV_PCA_DATA_AS_ROW, numComponents);
        // cout << "done!   # of principal components: " << pca.eigenvectors.rows << endl;
        // cout << data_mat.total() << endl;

        // Compute the covariance matrix and generate the eigen vectors using the Gram trick
        //....ooorrrr just use PCA
        // Store the eigen vectors and the mean vector in a file, which will be accessed by the IPCAtest function
        save(path + dir_name + ".pca", pca);

        return err;
    });
}

int IPCAtest(const char* trainFolderPath, const char* imgName)
{
    string path (trainFolderPath);
    string test_name (imgName);
    /* imgName is the path and filename of the test image */
    Mat im = imread(test_name, CV_LOAD_IMAGE_COLOR).reshape(1,1);

    float min_error = -1;
    string min_error_class;
    // Read the eigen vectors and means for each class from file
    // Project the input test image onto each eigen space and reconstruct
    // Compute the reconstruction error between the input test image and the reconstructed image
    // You can use euclidean distance (or any other appropriate distance measure)

    int err = map_files(path, [&im, &min_error, &min_error_class] (string pca_name, string path) {
        if (pca_name.size() < 4 || pca_name.substr(pca_name.size() - 4, 4).compare(".pca")) return 0;
        // cout << "pca path:" << path << pca_name << endl;
        PCA pca = load(path + pca_name);

        // cout << "im rows/cols" << im.rows << "," << im.cols << endl;
        // cout << "pca mean rows/cols" << pca.mean.rows << "," << pca.mean.cols << endl;
        // cout << "pca mean rows/cols" << pca.eigenvectors.rows << "," << pca.eigenvectors.cols << endl;
        Mat im_projected = pca.backProject(pca.project(im));
        // cout << im.type() << "im rows/cols" << im.rows << "," << im.cols << endl;
        // cout << im_projected.type() << "im_projected rows/cols" << im_projected.rows << "," << im_projected.cols << endl;

        im.convertTo(im, CV_32F);
        Mat diff_mat;
        absdiff(im, im_projected, diff_mat);

        float diff = sum(diff_mat)[0];
        if (min_error < 0 || diff < min_error) {
            min_error = diff;
            min_error_class = pca_name;
        }

        return 0;
    });
    // return the class label corresponding to the eigen space which showed minimum reconstruction error

    cout << "winning class: " << min_error_class << endl;

    return err;
}
