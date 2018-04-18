import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FieldMapper {
  private String _delimiter;
  private HashMap<String, Integer> _fieldMap;
  private String[][] _fileLines;
  private int _fieldCount;


  FieldMapper(List<String> fileLines, String delimiter){
    _delimiter = delimiter;
    _fieldMap = mapFieldToPosition(fileLines.get(0), delimiter);
    _fieldCount = _fieldMap.size();

    List<String[]> tempFileList = new ArrayList<String[]>();
    for(String line : fileLines){
      String[] lineAsArray = line.split(delimiter, _fieldCount);
      tempFileList.add(lineAsArray);
    }
    _fileLines = tempFileList.toArray(new String[0][_fieldCount]);
  }

  private HashMap<String,Integer> mapFieldToPosition(String headOfFile, String delimiter){
    HashMap<String,Integer> fieldMap = new HashMap<>();
    String[] fieldList = headOfFile.split(delimiter);
    for(int i = 0; i < fieldList.length; i++){
      fieldMap.put(fieldList[i], i);
    }
    return fieldMap;
  }

  public String getDelimiter(){
    return _delimiter;
  }

  public int getFileFieldCount(){
    return _fieldCount;
  }

  public String[][] getFileLines(){
    return _fileLines;
  }

  public int indexOfField(String fieldName) {
    return _fieldMap.get(fieldName);
  }

  public List<String> getFieldByFieldName(String fieldName) {
    return getFieldByFieldName(fieldName,0,_fileLines.length - 1);
  }

  public List<String> getFieldByFieldName(String fieldName, int startIndex, int endIndex) {
    List<String> fieldList = new ArrayList<String>();
    for(int i = startIndex; i <= endIndex; i++){
      fieldList.add(_fileLines[i][indexOfField(fieldName)]);
    }
    return fieldList;
  }
}
