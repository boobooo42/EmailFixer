import com.sun.tools.javac.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase. *;


public class TestEmailFixer {
  private String _filePath;
  private String _delimiter;
  private String _emailFieldName;
  private FieldMapper _fieldMapper;
  private EmailFixer _emailFixer;
  private List<String> _fileLines;

  @Before
  public void setFilePath(){
    _filePath = "resources/ir_contact_info.txt";
    _delimiter = "\t";
    _emailFieldName = "contact.email";
    _fileLines = FileLoader.readAllLines(_filePath);
    _fieldMapper = new FieldMapper(_fileLines,_delimiter);
    _emailFixer = new EmailFixer(false,false);
    //_emailFixer.setNumberOfRetrys(3);
    _emailFixer.setValidationLevel(EmailFixer.ValidationLevel.MXRecords);
  }


  @Test
  public void testLoad1000Lines(){
    List<String> lineList = new ArrayList<>();
    lineList = FileLoader.readLines(1000,_filePath);
    assertNotNull(lineList);
    assertEquals(1000,lineList.size());
  }

  @Test
  public void testReadWholeFileToList(){
    List<String> lineList;
    lineList = FileLoader.readAllLines(_filePath);
    assertEquals(9898, lineList.size());
  }


  @Test
  public void testGetIndexOfEmail(){
    int indexOfEmail = _fieldMapper.indexOfField(_emailFieldName);
    assertEquals(4, indexOfEmail);
  }

  @Test
  public void testGetIndexOfFirstName(){
    int indexOfEmail = _fieldMapper.indexOfField("firstname");
    assertEquals(3, indexOfEmail);
  }


  @Test
  public void testGetEmailFor1000Lines(){
    List<String> fieldList = null;
    fieldList = _fieldMapper.getFieldByFieldName(_emailFieldName,0 , 1000);
    assertEquals("BFischer@bankfirstfed.com", fieldList.get(324));
  }

  @Test
  public void testFileLinesInitialization(){
    assertEquals(9898, _fieldMapper.getFileLines().length);
  }

  @Test
  public void testGetFileFieldCount(){
    assertEquals(13, _fieldMapper.getFileFieldCount());
  }

  @Test
  public void testInitializeEmailFixer(){
    assertTrue(_emailFixer.isValid("boobooo42@gmail.com"));
    assertFalse(_emailFixer.isValid("bob.1@g.cm.j"));
  }

  @Test
  public void testForValidEmails(){
    List<String> emailList = null;
    emailList = _fieldMapper.getFieldByFieldName(_emailFieldName, 0 , 100);
    int validEmails = 0;
    for(String email : emailList){
      EmailFixer.EmailValidationLevel validationLevelFailed = _emailFixer.validateEmail(email);
      if(validationLevelFailed == EmailFixer.EmailValidationLevel.Passed)
        validEmails++;
    }
    assertEquals(98, validEmails);
  }


  @Test
  public void testCheckLineForBadEmail(){
    boolean isBadEmail = _emailFixer.isValid("boobooo42@gm.cm.");
    assertFalse(isBadEmail);
  }

  @Test
  public void testCheckLineForGoodEmail(){
    boolean isGoodEmail = _emailFixer.isValid("boobooo42@gmail.com");
    assertTrue(isGoodEmail);
  }

  @Test
  public void testValidEmailServer(){
    boolean isValidEmailServer = _emailFixer.hasMXRecords("idfbins.com");
    assertTrue(isValidEmailServer);
  }

  @Test
  public void testInvalidEmailServer(){
    boolean isValidEmailServer = _emailFixer.hasMXRecords("gmail.cm");
    assertTrue(isValidEmailServer);
  }

  @Test
  public void testgetServerFromEmail(){
    String server = _emailFixer.getEmailServer("boobooo42@gmail.com");
    assertEquals("gmail.com",server);
  }

  @Test
  public void testMXRecords(){
    boolean failedMXRecord = _emailFixer.hasMXRecords("www.idfbins.com");
    assertFalse(failedMXRecord);
  }



  @Test
  public void testEmail(){
    EmailFixer.EmailValidationLevel emailValidationLevelFailed = _emailFixer.validateEmail("karensue@ritterskamp.com");
    assertEquals(EmailFixer.EmailValidationLevel.MXRecords, emailValidationLevelFailed);
  }

  @Test
  public void testCreateTextFile(){
    String filePath = "resources/white_beep.txt";
    List<String> testList = Arrays.asList("Hey yo", "What up");
    FileWriter.writeToFile(testList, filePath);
    List<String> fileLines = FileLoader.readAllLines(filePath);
    assertEquals(testList.size(), fileLines.size());
    File file = new File(filePath);
    assertTrue(file.delete());

  }

  @Test
  public void testWriteTwoLinesToExistingFile(){
    String filePath = "resources/existing_file.txt";
    List<String> originalFileLines = FileLoader.readAllLines(filePath);
    List<String> testList = Arrays.asList("Bob", "Alice");
    FileWriter.writeToFile(testList, filePath);
    List<String> fileLines = FileLoader.readAllLines(filePath);
    assertEquals(originalFileLines.size() + 2, fileLines.size());
  }

  @Test
  public void testCreateDelimitedLine(){
    List<String> testLines = Arrays.asList("a","a","a","a");
    String fileLine = FileWriter.createDelimitedLine(testLines,_delimiter,13);
    assertEquals(17, fileLine.length());
  }

  @Test
  public void testWriteDelimitedLineToFile(){
    String[][] fileLines = _fieldMapper.getFileLines();
    List<String> fileList = new ArrayList<>();
    for(int i = 0; i < fileLines.length; i++){
      String fileLine = FileWriter.createDelimitedLine(Arrays.asList(fileLines[i]),"||",_fieldMapper.getFileFieldCount());
      fileList.add(fileLine);
    }
    FileWriter.writeNewTextFile(fileList, "resources/test.txt");
  }


  @Test
  public void testForValidEmailsWithArray(){
    String[][] fileLines = _fieldMapper.getFileLines();
    int emailIndex = _fieldMapper.indexOfField(_emailFieldName);
    int badEmails = 0;
    int isBlank = 0;
    for(int i = 0; i < fileLines.length; i++){
      String email = fileLines[i][emailIndex];
      if(email.trim().isEmpty()){
        isBlank++;
      }
      else{
        EmailFixer.EmailValidationLevel emailValidationLevel = _emailFixer.validateEmail(email);
        if(emailValidationLevel != EmailFixer.EmailValidationLevel.Passed){
          badEmails++;
          System.out.println(i + "\temail:\t" + email + "\treason:\t" + emailValidationLevel.toString());
        }
      }
    }
    System.out.println("Bad email Count:\t" + badEmails);
    System.out.println("Empty email Count:\t" + isBlank);
    System.out.println("Emails size:\t" + fileLines.length);
    //assertEquals(50, badEmails);
    //assertEquals(95, badEmails); //DNS
    //assertEquals(111, badEmails);//MXRecords + DNS
    assertEquals(85, badEmails);//MXRecords w/ no DNS
  }

//  @Test
//  public void testIndexesExactlyTheSame(){
//    List<String> fileEmails = new ArrayList<>();
//    for(int i = 0; i< _fieldMapper.getFileLines().length; i++){
//      fileEmails.add(_fieldMapper.getFileLines()[i][_fieldMapper.indexOfField(_emailFieldName)]);
//    }
//    boolean indexesSame = true;
//    List<Pair<Integer, EmailFixer.EmailValidationLevelFailed>> emailStatuses = _emailFixer.checkEmailList(fileEmails);
//    for(int i = 0; i< fileEmails.size(); i++){
//      if(emailStatuses.get(i).fst != i){
//        indexesSame = false;
//        break;
//      }
//    }
//    assertTrue(indexesSame);
//  }

//  @Test
//  public void testListForBadEmailServers(){
//    List<Pair<Integer, String>> badServerList = null;
//    badServerList = _emailFixer.checkFileForInvalidServers(_fieldMapper.getFileLines(), _fieldMapper.indexOfField(_emailFieldName));
//    assertNotNull(badServerList);
//  }


  //  @Test
//  public void testDelimitLineFromFile(){
//    String firstLine = FileLoader.readLine(_filePath);
//    assertNotNull(firstLine);
//    String[] splitString = _fieldMapper.createFieldMap(_delimiter,firstLine);
//    System.out.print(splitString[1]);
//  }

//  @Test
//  public void testGetFieldMapFromLine(){
//    String firstLine = FileLoader.readLine(_filePath);
//    String[] fieldMap = _fieldMapper.setFieldMap(firstLine,_delimiter);
//    assertEquals(fieldMap[1],"ssn");
//  }


//  @Test
//  public void testLine50(){
//    assertNotNull(_fileLoader.readLines(100));
//    //_fileLoader.getFileLines();
//    //assertNotNull();
//  }

//  @Test
//  public void testLinesListHas100Lines(){
//    assertEquals(100, fileLoader.getFileLines().size());
//  }
//  @Test
//  public void testAddLineToList(){
//    BufferedReader br = fileLoader.getBufferedReader();
//    assertNotNull(br);
//    String firstLine = fileLoader.readNextLine();
//    assertNotNull(firstLine);
//
//    assertNotNull(fileLineList.i);
//  }

  //  @Test
//  public void testGetFilePath(){
//    assertNotNull(fileLoader.getFilePath());
//  }

//  @Test
//  public void testNewFileLoaderWithFile(){
//    try {
//      assertNotNull(fileLoader.getFilePath());
//      assertNotNull(fileLoader.loadFile());
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//    }
//  }

//  @Test
//  public void testReadOneLineFromFile(){
//    String firstLine = FileLoader.readLine(_filePath);
//    assertNotNull(firstLine);
//  }

//  @Test
//  public void testSetDelimiter(){
//    String[] splitString = _fieldMapper.splitLineOnDelimiter("||","hey||bb",2);
//    assertEquals(2, splitString.length);
//  }
//
//  @Test
//  public void testSplitLineOnDelimiter(){
//    String[] splitString = _fieldMapper.splitLineOnDelimiter(_delimiter, _fileLines.get(2), 10);
//    assertEquals(10, splitString.length);
//  }
}
