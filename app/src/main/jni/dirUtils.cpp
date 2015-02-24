#include <jni.h>
#include <dirent.h>
#include <string>

#include "dirUtils.hpp"

using namespace std;

// maps two functions over all the files and directories at a given path
// file_fn/dir_fn(name, directory its in)
int map_dir(string dir_path, fd_fn file_fn, fd_fn dir_fn)
{
    DIR *dir = opendir(dir_path.c_str());
    if (dir == NULL)
    {
        return -1;
    }
    struct dirent *ent;
    while ((ent = readdir(dir)) != NULL)
    {
        string name (ent->d_name);
        unsigned char type =  ent->d_type;
        if (type == DT_REG) {
            if(file_fn(name, dir_path+"/") == -1) {
                return -1;
            }
        }

        if (type == DT_DIR && name[0] != '.') {
            if(dir_fn(name, dir_path+"/") == -1) {
                return -1;
            }
        }
    }
    return closedir(dir);
}

int do_nothing(string name, string path)
{
    return 0;
}

int map_files(string dir_path, fd_fn file_fn)
{
    return map_dir(dir_path, file_fn, do_nothing);
}

int map_dirs(string dir_path, fd_fn dir_fn)
{
    return map_dir(dir_path, do_nothing, dir_fn);
}