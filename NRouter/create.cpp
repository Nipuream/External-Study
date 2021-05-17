#include <iostream>
#include <stdlib.h>
#include <stdio.h>
#include <sys/stat.h>
#include <unistd.h>
#include<iostream>
#include<fstream>
#include<string>
#include <vector>
#include <memory.h>
#include <dirent.h>
#include <string.h>

using namespace std;
char *ltrim(char *str);
int readFileList(vector<string> &filelist, const char *basePath);
void writerEntry(fstream& file, string& path);

int main(int argc, char* argv[]){

   for(int i = 0; i < argc; i++){
       cout <<  argv[i] << endl;
   }


  char* path = get_current_dir_name();

 DIR* routerDir = opendir("router/");
 if(routerDir == NULL){
     cout <<"router file not exits." << endl;
     return -1;
 }

  DIR* dir = opendir("doc");
  if(dir == NULL){
      system("mkdir doc");
  }

   if((access("doc/业务场景.md", F_OK)) != -1){
         remove("业务场景.md");
   }

  const char* filePath = "router/";
  vector<string> files;

  fstream file("doc/业务场景.md", ios::out);
  file << "# " << "业务场景定义" << endl;
  file << endl;

  file << "------" << endl;

  //获取该路径下所有文件
  readFileList(files, filePath);
  for(int i = 0; i < files.size(); i++){
     
    //   cout << files[i] <<endl;
      size_t firstPos = files[i].find("/",0);
    //   size_t lastPos = files[i].find("/", firstPos+1);
    //   cout << "firstPos = " << firstPos << ", lastPos = " << lastPos << endl;

      string sence = files[i].substr(firstPos + 1);
    //   cout << "sence : " << sence << endl;

   size_t testpos = files[i].find_last_of("/");
    string test = files[i].substr(testpos + 1);
    // cout << "testpos :" << testpos << "test : " << test << endl;

      if(sence == "data/data"){
          continue;
      }

      file << "## " << test << endl;
      file << "> " << "路由表位置：" << files[i] <<   endl;

     string::size_type idx = sence.find("callback");
     if(idx != string::npos){
           file << "> " << "接口注入路径： 由宿主app提供实例" << endl;  
     } else {
           file << "> " << "接口注入路径：" <<  sence <<  endl;
     }

      file << endl << endl;
      writerEntry(file, files[i]);
  }

file.flush();
file.close();

    return 0;
}

void writerEntry(fstream& file, string& path){

      fstream tmp(path.c_str(), ios::in);
      string line;
      string::size_type idx;
      char tmpBuf[200] = "";
      while(getline(tmp, line)){

        //   idx = line.find("业务场景");
        //   if(idx != string::npos){
        //          file << endl;
        //   } 

          idx = line.find("################ CREATE BY YANGHUI11, DON'T MODIFY #################");
          if(idx != string::npos){
              continue;
          }

          file << line << endl;

          idx = line.find("返回类型");
          if(idx != string::npos){
              file << endl;
          }

          idx = line.find("======================================================================================");
          if(idx != string::npos){
              file << endl;
          }

      }

      tmp.close();
}

//获取特定格式的文件名
int readFileList(vector<string> &filelist, const char *basePath)
{
    DIR *dir;
    struct dirent *ptr;
    char base[1000];

    if ((dir=opendir(basePath)) == NULL)
    {
        perror("Open dir error...");
        exit(1);
    }

    while ((ptr=readdir(dir)) != NULL)
    {
        if(strcmp(ptr->d_name,".")==0 || strcmp(ptr->d_name,"..")==0)    ///current dir OR parrent dir
            continue;
        else if(ptr->d_type == 8)    //file
        {
            /*
            //printf("d_name:%s/%s\n",basePath,ptr->d_name);
            string temp = ptr->d_name;
            //cout  << temp << endl;
            string sub = temp.substr(temp.length() - 4, temp.length()-1);
            //cout  << sub << endl;
            if(sub == format)
            {
                string path = basePath;
                path += "/";
                path += ptr->d_name;
                filelist.push_back(path);
            }
            */
              memset(base,'\0',sizeof(base));
              strcpy(base,basePath);
              strcat(base,"/");
              strcat(base,ptr->d_name);
              filelist.push_back(base);
        }
        else if(ptr->d_type == 10)    ///link file
        {
            //printf("d_name:%s/%s\n",basePath,ptr->d_name);
        }
        else if(ptr->d_type == 4)    ///dir
        {
            memset(base,'\0',sizeof(base));
            strcpy(base,basePath);
            // strcat(base,"/");
            strcat(base,ptr->d_name);
            readFileList(filelist, base);
        }
    }
    closedir(dir);
    return 1;
}

char *ltrim(char *str)
{
	if (str == NULL || *str == '\0')
	{
		return str;
	}
 
	int len = 0;
	char *p = str;
	while (*p != '\0' && isspace(*p))
	{
		++p;
		++len;
	}
 
	memmove(str, p, strlen(str) - len + 1);
 
	return str;
}
