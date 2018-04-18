import java.util.EnumSet;
import java.util.HashMap;
import org.apache.commons.validator.routines.EmailValidator;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;


public class EmailFixer extends EmailValidator {
  private ValidationLevel _validationLevel;
  private HashMap<String, Boolean> domainsWithMailServersMap;

  public EmailFixer(boolean allowLocal, boolean allowTld){
    super(allowLocal, allowTld);
    _validationLevel = ValidationLevel.Syntax;
    domainsWithMailServersMap = new HashMap<>();
  }

  public ValidationLevel getValidationLevel(){
    return _validationLevel;
  }

  public void setValidationLevel(ValidationLevel validationLevel) {
    _validationLevel = validationLevel;
  }

  public EmailValidationLevel validateEmail(String email){
    EnumSet<ValidationLevel> continuingLevels = EnumSet.of(ValidationLevel.MXRecords);
    if(email.isEmpty()){
      return EmailValidationLevel.Syntax;
    }
    else if(!isValid(email)){
      return EmailValidationLevel.Syntax;
    }
    else if(continuingLevels.contains(_validationLevel)){
      String domain = getEmailServer(email);
      return validateMailServer(domain);
    }
    else{
      return EmailValidationLevel.Passed;
    }
  }

  public String getEmailServer(String email){
    return email.substring(email.lastIndexOf("@") + 1).trim().toLowerCase();
  }

  public String getEmailUser(String email){
    return email.substring(0, email.lastIndexOf("@") - 1);
  }


  public EmailValidationLevel validateMailServer(String domain){
    if(domainsWithMailServersMap.containsKey(domain)) {
      boolean domainHasMXRecord = domainsWithMailServersMap.get(domain);
      return domainHasMXRecord ? EmailValidationLevel.Passed : EmailValidationLevel.MXRecords;
    }
      if(!hasMXRecords(domain)){
        domainsWithMailServersMap.put(domain, false);
        return EmailValidationLevel.MXRecords;
      }
      else{
      domainsWithMailServersMap.put(domain, true);
        return EmailValidationLevel.Passed;
      }
  }

  public boolean hasMXRecords(String domain){
    if (domain.isEmpty()) {
      return false;
    }

    try {
      Record[] records = new Lookup(domain, Type.MX).run();
      if (records != null && records.length > 0) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }



  enum ValidationLevel{
    Syntax,
    MXRecords
  }

  enum EmailValidationLevel{
    Passed,
    Syntax,
    MXRecords
  }

  //  public EmailValidationLevel validateEmailDNSServer(String email){
//    EnumSet<ValidationLevel> continuingLevels = EnumSet.of(ValidationLevel.MXRecords);
//    String server = getEmailServer(email);
//    if(!isValidDNSServer(server)){
//      return EmailValidationLevel.DNS;
//    }
//    else if (continuingLevels.contains(_validationLevel)){
//      return validateMailServer(server);
//    }
//    else{
//      return EmailValidationLevel.Passed;
//    }
//  }
//
//  public boolean isValidDNSServer(String server){
//    if(!server.isEmpty()){
//        try{
//           InetAddress.getByName(server);
//          return true;
//        }
//        catch (UnknownHostException e){
//          //e.printStackTrace();
//        }
//    }
//    return false;
//  }
}


