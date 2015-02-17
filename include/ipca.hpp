// takes a directory that contains directories containing classes of images
// and number of components ie trained images per class
int IPCAtrain(const char* trainFolderPath, int numComponents);

// returns whichever class the image at this path most likely belongs to
int IPCAtest(const char* trainFolderPath, const char *imgName);
