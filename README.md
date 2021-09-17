# TopBloc Code Challenge

## Description:
This is my working solution for the TopBloc interview challenge. Being given a Java backend using the Spark framework and a React frontend. I was tasked with reading in excel file data, parsing it, and handling HTTP requests on the backend. On the frontend I was tasked with getting data from the backend, changing state, and updating the UI to adequately display the data. 

## Backend:
For the backend I used the Apache POI library to read and parse the data into a custom object that I made so the data was easily accessed and organized. I then stored all of these objects in a hashmap using the datas' ID value as the key and the custom object became the hashmap value. I then implemented the GSON library to convert my hashmap into a JSON obj so that I could send it to the frontend. 

## Frontend:
On the frontend I parsed in the JSON obj after recieving from an HTTP GET request and updated the webpages' state so that it would reflect the new information via the click of a button. Also, there needed to be a way to record user input and send it to the backend. To achieve this I need to do an HTTP POST request that would send the data the user entered as a JSON obj with updated values. This information was then parsed on the backend into an Array and the proper calculations where then performed and the information was once again the data was sent to the frontend to be displayed for the user.

## Extra Developments
I did implement some error checking to ensure bad user input values wouldn't cause everything to break, but I didn't go as far as to check every concieveable case of what might happen or implement checks for every edge case. I noticed a bug with the Apache POI library where it would detect data in cells where there was no information and this would read in null values and break the program. To accomodate for this I implemented an if statement that ensures the library avoids those rows in the excel document. I did this because I figured that I wasn't allowed to mess with the excel documents in any way as it could make the challenge significantly easier to solve, so this band aid of a solution can be removed once the excel docs are modified to delete the bad rows. I suspected it was a bug because it only occured on one sheet in the Distributors.xlsx file. 
