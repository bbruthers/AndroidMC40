package net.vantagesystems.VantageLicenseScanner;


public class PersonParser
{
    public static Person ParsePersonInformation(String data)
    {
        Person p = new Person();

        String strRetain = data.replaceAll("\u0000","");
        strRetain = strRetain.replaceAll("\\{","");
        strRetain = strRetain.replaceAll("\\}", "");
        strRetain = strRetain.replace('"', ' ');

        String strResultArray[] = strRetain.split(",");

        for(int i = 0; i < strResultArray.length - 1; i++)
        {
            switch(i)
            {
                case 0: //address
                    p.SetAddress(strResultArray[0] = strResultArray[0]); //home address
                    break;
                case 1:
                    p.SetCity(strResultArray[1] = strResultArray[1]); //city
                    break;
                case 2:
                    p.SetPostCode(FormatPostCode(strResultArray[2])); //post code
                    break;
                case 3: //firstName
                    if(strResultArray[3].equals(""))
                        p.SetParseResult(PersonInformationParseResult.ParseFailure);
                    else
                        p.SetFirstName(strResultArray[3]);
                        p.SetParseResult(PersonInformationParseResult.ParseSuccess);
                    break;
                case 4: //Last Name

                    if(strResultArray[4].equals(""))
                        p.SetParseResult(PersonInformationParseResult.ParseFailure);
                    else
                        p.SetFamName(strResultArray[4]);
                        p.SetParseResult(PersonInformationParseResult.ParseSuccess);
                    break;
                case 5: //Get Birthdate
                    FormatBirthdate(p, strResultArray[5]);
                    break;
                case 6: //Get LicenseNum
                    p.SetLicenseNumber(strResultArray[6]);
                    break;
                case 7:
                    p.SetAge(ParseAge(strResultArray[7]));
                    break;
                case 8: //SEPNumber
                    p.SetSEP(ParseSEP(strResultArray[8]));
                    break;
                case 9: //License EXP Date
                    p.SetExpirationdate(FormatExpDate(strResultArray[9]));
                    break;
            }
        }

        return p;
    }

    private static String FormatPostCode(String param)
    {
        String tempPostCode = param.substring(0, 12);

        try
        {
            param = param.replace(tempPostCode, "");
            param = param.trim();
        }
        catch(Exception ex)
        {
            DebugLogger.LogException(ex);
        }

        return param;
    }

    private static void FormatBirthdate(Person perParam, String bdString)
    {
        bdString = bdString.trim();
        String tempbd = bdString.substring(0, 11);
        bdString = bdString.replace(tempbd, "");
        StringBuffer sb = new StringBuffer(bdString);

        try
        {
            sb.insert(sb.length() - 4, "/");
            sb.insert(sb.length() - 7, "/");

            bdString = sb.toString();
            perParam.SetBirthdate(bdString);
            perParam.SetParseResult(PersonInformationParseResult.ParseSuccess);
        }
        catch (Exception ex)
        {
            perParam.SetParseResult(PersonInformationParseResult.ParseFailure);
        }
    }

    private static String FormatExpDate(String exParam)
    {
        exParam = exParam.trim();
        String tempExp = exParam.substring(0, 16);
        exParam = exParam.replace(tempExp, "");

        StringBuffer exSB = new StringBuffer(exParam);

        try
        {
            exSB.insert(exSB.length() - 4, "/");
            exSB.insert(exSB.length() - 7, "/");
            exParam = exSB.toString();
            exParam = exParam.trim();
        }
        catch(Exception ex)
        {
            DebugLogger.LogException(ex);
        }

        return exParam;
    }

    private static int ParseAge(String strAge)
    {
        strAge = strAge.trim();
        String tempSubString = strAge.substring(0, 12);
        strAge = strAge.replace(tempSubString, "");

        int ageResult = 0;

        try
        {
            ageResult = Integer.parseInt(strAge);
        }
        catch(Exception ParseException)
        {
            DebugLogger.LogException(ParseException);
        }

        return ageResult;
    }

    private static long ParseSEP(String strSEP)
    {
        long SEP = 0;

        try
        {
            SEP = Long.parseLong(strSEP);
        }
        catch(Exception ex)
        {
            DebugLogger.LogException(ex);
        }

        return SEP;
    }
}
