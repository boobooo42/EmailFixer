import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileLoader{

  private static BufferedReader loadFile(String filePath) throws FileNotFoundException {
    return new BufferedReader(new FileReader(filePath));
  }

  public static void openFileToStream(){

  }

  public static List<String> readLines(int linesToRead, String filePath) {
    ArrayList<String> fileList = new ArrayList<>();
    try(BufferedReader bufferedReader = loadFile(filePath)){
      for (int i=0; i < linesToRead; i++){
        String line = bufferedReader.readLine();
        if(line != null)
          fileList.add(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
      //throw new FileNotFoundException();
    }

    return fileList;
  }



  public static List<String> readAllLines(String filePath) {
    List<String> fileList = new ArrayList<>();
    try(BufferedReader bufferedReader = loadFile(filePath)){
        String line = bufferedReader.readLine();
        while(line != null){
          fileList.add(line);
          line = bufferedReader.readLine();
        }
    } catch (IOException e) {
      e.printStackTrace();
      //throw new FileNotFoundException();
    }

    return fileList;
  }

}
