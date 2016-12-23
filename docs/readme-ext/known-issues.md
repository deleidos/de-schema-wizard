[Back to Main](https://github.com/deleidos/de-schema-wizard/#schema-wizard)

# Security Considerations
It is very important to understand the security implications that come with Schema Wizard.  Please carefully review the following:

## Interpretation Engine
The Interpretation Engine executes arbitrary Python code provided by users of Schema Wizard.  There are constraints to prevent this code from affecting the rest of the application, but it should not yet be considered secure.  This feature has not been tested by security professionals.  For this reason Schema Wizard should not be exposed to anything other than trusted connections.

## Unencrypted network traffic
Schema Wizard is not configured to use SSL by default.  Sensitive material should not be processed in open networks.

# Known Issues

| Defect_ID | Description | Work Around (If Applicable) |
|:-------------:|:-------------:|:-----------:|
| B-07197 | Sample columns are not shown on matching screen. | Maximize the size of your screen.  Decrease font size until they are displayed.  With a large number of samples, leverage the "Modify Existing" feature" | 
| B-07197 | Manually added properties should have a visual cue that they are not available in details pane | N/A |
| B-07197 | Schema property details should be availabe in matching dialog | N/A |
| B-07197 | Data samples should scroll horizontally | *see first row* |
| B-07197 | Data samples should be deletable in matching dialog | Use the "Discard" feature instead |
| B-07197 | Restore matching that requires interpretation | N/A |
| B-07197 | Duplicate rows are created on manual merge | N/A |
| B-07197 | Schema fields are sometimes not saved properly | N/A |
