#include <string>
#include <functional>

using namespace std;

typedef function<int (string, string)> fd_fn;

// maps two functions over all the files and directories at a given path
// file_fn/dir_fn(name, directory its in)
int map_dir(string dir_path, fd_fn file_fn, fd_fn dir_fn);

int map_files(string dir_path, fd_fn file_fn);

int map_dirs(string dir_path, fd_fn dir_fn);
