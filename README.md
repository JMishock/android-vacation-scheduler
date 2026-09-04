# D308 Vacation Scheduler

## Purpose

The D308 Vacation Scheduler is an Android mobile application designed to help users create and manage vacations and their associated excursions. The application stores vacation and excursion information in a local Room database and provides features for creating, editing, deleting, viewing, sharing, and scheduling alerts for vacation and excursion information.

## Application Instructions

### Home Screen
1. Launch the D308 Vacation Scheduler application.
2. Select **View Vacations** to open the Vacation List.

### Vacation List
1. The Vacation List displays all saved vacations.
2. Select an existing vacation to open its Vacation Details.
3. Select **Add Vacation** to create a new vacation.

### Add or Edit a Vacation
1. Enter a vacation title.
2. Enter the hotel or accommodation.
3. Select the vacation start date.
4. Select the vacation end date.
5. The end date must not occur before the start date.
6. Select **Save Vacation** to save a new vacation or update an existing vacation.

### Delete a Vacation
1. Open an existing vacation from the Vacation List.
2. Select **Delete Vacation**.
3. A vacation cannot be deleted while excursions are associated with it. Delete the associated excursions first if the vacation needs to be removed.

### Vacation Alerts
1. Open an existing vacation.
2. Select **Set Vacation Alerts**.
3. The application schedules alerts for the vacation start and end dates.

### Share a Vacation
1. Open an existing vacation.
2. Select **Share Vacation**.
3. Choose an available Android sharing option.
4. The shared information includes the vacation title, hotel or accommodation, start date, and end date.

### View Excursions
1. Open an existing vacation.
2. Select **View Excursions**.
3. The Excursion List displays excursions associated with that vacation, including each excursion's title and date.

### Add or Edit an Excursion
1. From the Excursion List, select **Add Excursion** to create an excursion, or select an existing excursion to edit it.
2. Enter the excursion title.
3. Select the excursion date.
4. The excursion date must fall within the associated vacation's start and end dates.
5. Select **Save Excursion** to save or update the excursion.

### Delete an Excursion
1. Open an existing excursion.
2. Select **Delete Excursion** to remove it.

### Excursion Alerts
1. Open a saved excursion.
2. Select **Set Excursion Alert**.
3. The application schedules an alert for the excursion date.

## Android Version

- Minimum SDK: API 24
- Target SDK: API 37
- Compile SDK: API 37

The application was developed to target Android API 37.

## Git Repository

WGU GitLab Repository:

https://gitlab.com/wgu-gitlab-environment/student-repos/jmishoc/d308-mobile-application-development-android