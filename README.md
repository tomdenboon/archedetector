# ArcheDetector

## Setup
While you can use external dependencies, the easiest method of getting up and running is to use the included `docker-compose.yml` which you can use with Docker to create a container that holds all the services needed to run the main program. To use this, do the following from the main project directory:

```
docker-compose up
```

You should now have a PostgreSQL database running on `localhost:54321`, with a database named `arch`, and the following credentials:
```
username: arch_dev
password: arch_dev_pass
```

You can access the database by navigating to [the local PgAdmin instance running in docker](http://localhost:5050), or by connecting to the database directly using a management tool of your choice.

### External Properties
Some sensitive properties of the application are omitted from the VCS, such as API credentials. Please see `config/TEMPLATE.properties` for a list of the properties which must be defined externally.

To define these properties, simply create a `config/application.properties` file, and copy the contents of the template to it, and set their real values.
