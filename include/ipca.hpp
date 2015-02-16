// takes a directory that contains directories containing classes of images
// and number of training images per class
int IPCAtrain(const char* trainFolderPath, int numTrain);

// returns whichever class the image at this path most likely belongs to
int IPCAtest(char *imgName);
