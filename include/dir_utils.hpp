#include <string>

using namespace std;

typedef int (*fd_fn)(string, string);

// maps two functions over all the files and directories at a given path
// file_fn/dir_fn(name, directory its in)
int map_dir(string dir_path, fd_fn file_fn, fd_fn dir_fn);

int map_files(string dir_path, fd_fn file_fn);

int map_dirs(string dir_path, fd_fn dir_fn);
