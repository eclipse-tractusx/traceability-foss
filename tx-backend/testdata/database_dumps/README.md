# Restoring a Database Dump

## Overview

This README provides step-by-step instructions on how to restore a database dump with pgAdmin. Database dumps are invaluable for data recovery in case of system issues or data corruption. This guide outlines the process for restoring a PostgreSQL database from a dump file.


## Restoring the Database

Follow these steps to restore a database from a dump file using pgAdmin:

1. Open the pgAdmin link from the ArgoCD application and login.

2. Click on the "trace" database.

3. Right-click on the database, and select "Restore."

4. Click on the folder icon next to the "Filename" field.

5. Click on the three dots (...) on the right, and select "Upload."

6. Drag and drop the database dump file from the "database dump" folder for the corresponding environment into the upload area and confirm the action.

7. "Process Failed" error message can be ignored

### Which Database dump for which environment

In this project, there are two database dumps available: `database_dump_a` and `database_dump_b`. The files `database_dump_a` and `database_dump_b` are associated with different environments. Below is a list indicating which file corresponds to which environment:

- `database_dump_a`
    - `dev`
    - `e2e-a`
    - `int-a`

- `database_dump_b`
    - `test`
    - `e2e-b`
    - `int-b`


#### How to create a database dump

To create a database dump, follow these steps:

1. Open pgAdmin and connect to the database server.

2. Right-click on the database you want to dump.

3. Select "Backup."

4. Choose the appropriate options, such as the filename for the dump file.

5. Click "Backup" to generate the dump file.


#### Registering a Server in pgAdmin

If the database server isn't pre-registered in pgAdmin, follow these steps:

1. Right-click on "Servers" in the left panel.
2. Choose "Register" > "Server..." from the context menu.
3. In the "General" tab, name the server (e.g., int-b-db, dev, test).
4. Switch to the "Connection" tab.

##### Connection Details:
1. Open the live manifest from the tx-backend pod in the respective Argo environment.
2. Copy the middle part of the URL under SPRING_DATASOURCE_URL for the Host name/address (e.g., tx-backend-postgresql-int-b).
3. Extract the 4-numbered Port from the URL (e.g., 5432).
4. Find Maintenance database and Username under SPRING_DATASOURCE_USERNAME in the live manifest.
5. Retrieve the Password from SPRING_DATASOURCE_PASSWORD in the live manifest.
6. Click "Save" to register the server.

You've successfully registered the server in pgAdmin.

