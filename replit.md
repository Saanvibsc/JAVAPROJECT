# Running this project on Replit

This is a Java command-line application. It does not use a browser UI or web server.

## Start

Run the console application in the Shell with:

```bash
mvn exec:java
```

The **Start application** workflow runs the same command as a console process.

## Database and email

MySQL or MariaDB must be available for registration, login, loan management, and administrator features. Database settings can be provided through `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, and `DB_PASSWORD`; see `.env.example`. Without a database, the application prints a connection warning and still opens its console menu, but database-backed actions will not work.

SMTP settings are optional and can be supplied through the `SMTP_*` environment variables described in `.env.example`.
