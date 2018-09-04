package net.vantagesystems.VantageLicenseScanner;

//lib functionality has been incorporated into JDK 8.0, keeping for compatibility reasons.
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Person implements IPerson
{
   private String firstName, midName, famName;
   private String address, city, postCode; //postCode can be numeric or contain symbols such as dashes
   private String licenseNumber;
   private DateTime birthDate, licenseExpDate;
   private int age;
   private long SEPNumber;
   private PersonInformationParseResult ParseResult;

   public Person()
   {
       initProperties();
   }

   private void initProperties()
   {
        firstName =  midName = famName = "";
        address = city = postCode =  licenseNumber = "";
        age = 0;
        birthDate = new DateTime();
        licenseExpDate = new DateTime();
        SEPNumber = 0;
   }

   //Designed by specification to only consider U.S. law, so legal age >= 21
    @Override
    public Boolean isLegalAge()
    {
        if(age >= 21)
        {
            return true;
        }
        else
            return false;
    }

    @Override
    public Boolean isBanned()
    {
        if(SEPNumber > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public Boolean isLicenseExpired()
    {
        boolean result = false;

        DateTime currentDate = new DateTime();
        if(!currentDate.isBefore(licenseExpDate))
        {
            result = true;
        }

        return result;
    }

    //region GettersSetters

    public void SetFirstName(String fnParam)
    { firstName = fnParam; }
    public String GetFirstName()
    { return firstName; }

    public void SetMidName(String mnParam){midName = mnParam;}
    public String GetMidName(){ return midName; }

    public void SetFamName(String lnParam){famName = lnParam;}
    public String GetFamName(){return famName;}

    public void SetAddress(String param){address = param;}

    public void SetLicenseNumber(String lnParam){licenseNumber = lnParam;}
    public String GetLicenseNumber(){return licenseNumber;}

    public DateTime GetBirthdate(){return birthDate;}

    public void SetBirthdate(DateTime dtParam){birthDate = dtParam;}
    public void SetBirthdate(String bdParam)
    {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");
        birthDate = DateTime.parse(bdParam, dtf);
    }
    public void SetExpirationdate(String expParam)
    {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");
        licenseExpDate = DateTime.parse(expParam, dtf);
    }

    public DateTime GetExpiredate(){return licenseExpDate;}

    public Integer GetAge(){return age;}
    public void SetAge(int ageParam){age = ageParam;}
    public void SetAge(String strAgeParam)
    {
        try
        {
            age = Integer.parseInt(strAgeParam);
        }
        catch(Exception ex){age = 0;}
    }

    public String GetAddress(){return address;}
    public String GetCity(){return city;}
    public String GetPostCode(){return postCode;}
    public void SetPostCode(String pcParam){postCode = pcParam;}
    public void SetCity(String cityParam){city = cityParam;}

    public void SetParseResult(PersonInformationParseResult pipr)
    {
        ParseResult = pipr;
    }

    public PersonInformationParseResult GetParseResult()
    {
        return ParseResult;
    }

    public void SetSEP(long param){SEPNumber = param;}
    public long GetSEP(){return SEPNumber;}

    //endregion

}
