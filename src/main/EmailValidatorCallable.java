import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;
import org.apache.commons.validator.routines.EmailValidator;
import java.util.concurrent.Callable;

public class EmailValidatorCallable extends EmailValidator implements Callable {
  private String _email;
  private String _domain;
  private ValidationLevel _validationLevel;

  public EmailValidatorCallable(String email, ValidationLevel validationLevel){
    super(false, false);
    _email = email.trim();
    _domain = _email.substring(email.lastIndexOf("@") + 1).trim().toLowerCase();
    _validationLevel = validationLevel;
  }

  public boolean hasMXRecords(){
    if (_domain.isEmpty()) {
      return false;
    }
    try {
      Record[] records = new Lookup(_domain, Type.MX).run();
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
    Empty,
    NoDomain,
    Syntax,
    MXRecords
  }

  public EmailValidationLevel call() {
    if(_email.isEmpty())
      return EmailValidationLevel.Empty;
    if(_domain.isEmpty())
      return EmailValidationLevel.NoDomain;

    switch(_validationLevel){
      case Syntax:
        if(!isValid(_email)){
          return EmailValidationLevel.Syntax;
        }
      case MXRecords:
        if(!hasMXRecords()){
          return EmailValidationLevel.MXRecords;
        }
      default:
        return EmailValidationLevel.Passed;
    }

  }
}
