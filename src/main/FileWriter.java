import com.sun.deploy.util.StringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileWriter {
  public static void writeNewTextFile(List<String> lines, String filePath){
    Path file = Paths.get(filePath);
    try{
      Files.write(file, lines, Charset.forName("UTF-8"));
    }
    catch (IOException e){
      e.printStackTrace();
    }
  }


  public static void writeToFile(List<String> lines, String filePath){
    try(java.io.FileWriter fileWriter = new java.io.FileWriter(filePath,true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
    ){
      for(String line : lines){
        bufferedWriter.append(line);
        bufferedWriter.newLine();
      }
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  public static String createDelimitedLine(List<String> fields, String delimiter, int fileWidth){
    StringBuilder delimitedLine = new StringBuilder();
    for(int i = 0; i < fields.size(); i++){
      if(fields.get(i) != null){
        delimitedLine.append(fields.get(i));
      }
      delimitedLine.append(delimiter);
    }

    for(int i = fields.size(); i < fileWidth; i++){
      delimitedLine.append(delimiter);
    }
    return delimitedLine.toString();
  }


}
