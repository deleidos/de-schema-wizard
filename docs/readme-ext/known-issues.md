[Back to Main](https://github.com/deleidos/de-schema-wizard/#schema-wizard)

# Security Considerations
It is very important to understand the security implications that come with Schema Wizard.  Please carefully review the following:

## Authentication
Schema Wizard has demo authentication functionality as of verion 3.0.0-beta3.  The username and password is publicly available, so any information available to the user should be considered publicly available.  Full authentication functionality is expected in future releases.

## Interpretation Engine
The Interpretation Engine executes arbitrary Python code provided by users of Schema Wizard.  There are constraints to prevent this code from affecting the rest of the application, but it should not yet be considered secure.  This feature has not been tested by security professionals.  For this reason Schema Wizard should not be exposed to anything other than trusted connections.

## Unencrypted network traffic
Schema Wizard is not configured (by default) to use SSL.  Sensitive material should not be processed in open networks.

# Known Issues

| Defect_ID | Description | Work Around (If Applicable) |
|:-------------:|:-------------:|:-----------:|
| B-06908 | Context sensitive help is outdated. | More documentation coming in 4th beta... |
| D-02808 | Small files break during schema analysis. | Manually increase the size of your file. |
| D-02674 | Some graphs have labels that overlap each other. | N/A |
| D-02713 | Long names throw off field header/data alignment | N/A |