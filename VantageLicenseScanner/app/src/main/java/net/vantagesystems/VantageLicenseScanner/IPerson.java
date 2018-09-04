package net.vantagesystems.VantageLicenseScanner;

public interface IPerson
{
    Boolean isLegalAge();
    Boolean isBanned();
    Boolean isLicenseExpired();
}
