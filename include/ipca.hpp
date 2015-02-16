// takes a directory that contains directories containing classes of images
int IPCAtrain(const char* trainFolderPath);

// returns whichever class the image at this path most likely belongs to
int IPCAtest(char *imgName);
