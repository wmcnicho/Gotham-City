###Team 31
####Team Information

  + Team Name: Team 31
  + Team E-Mail: usc-csci201-fall2013-team31-l@mymaillists.usc.edu
  + Team Mentor: Sarah Whang <sarahwha@usc.edu>

####Resources

  + [Code Repository](https://github.com/usc-csci201-fall2013/team31)
  + [Documentation](https://github.com/usc-csci201-fall2013/team31/docs)

####Team Members
| No. | Name (First (Nickname) Last) |       USC Email          |                GitHub Username                |      Role      |
| :-: | :--------------------------- | :--------------------    | :-------------------------------------------- | :------------- |
|  1  | Evan Coutre                  | evcoutre@usc.edu         | @[evcoutre](https://github.com/evcoutre  )    |  Team Leader   |
|  2  | William Hunter McNichols     | wmcnicho@usc.edu         | @[wmcnicho](https://github.com/wmcnicho)      |                |
|  3  | Meruyert Aitbayeva           | aitbayev@usc.edu         | @[aibtbayev](https://github.com/aitbayev)     |                |
|  4  | Brice Roland                 | broland@usc.edu          | @[broland](https://github.com/broland)        |                |
|  5  | Nikhil Bedi                  | nbedi@usc.edu	        | @[nikhilbedi](https://github.com/nikhilbedi)  |                |

####Team Meetings
|       Meeting       |           Time                  |      Location      |
| :------------------ | :-----------------------        | :----------------- |
| Lab                 | Wed. 10:00am                    | SAL 109            |
| Weekly Meeting 1    | Tues. 05:00pm to 10:00pm        | GFS 112            |
| Weekly Meeting 2    | Thur. 05:00pm to 10:30pm        | SAL 125            |
| General meetings    | Tues./Thur. 05:00pm to 10:30pm  | WPH B34            |



### Team Project V1

#### Welcome to Gotham City

Immediately upon entering Gotham City (and running our program using the SimCityRun file to run it on Eclipse) you will hear
the Batman soundtrack, which will immediately immerse yourself into the darkness, chaos, and unpredictability that is Gotham City.

#####For V1
As you can see in our GUI we have 4 different types of buildings: Market (top right), Bank (top middle), Restaurant (top left and sides),
Homes (bottom row).
When you click on a building, the screen zooms in and gets replaced with the GUI and animation of that building. There is a small black box 
on the top left of each zoomed in building view that can be clicked to return to the city view.

######Home:
#######HomeResident Role:
########Landlord Role:

There is only one Home in our city and only one new person can live there.  If more than one person in our city is created, 
the others are spawned in a temporary location
######Bank:
######Market:
#######CashierRole:
#######MarketWorkerRole:
######Restaurants:
//all restaurants are implemented. They do not include the producer-consumer revolving stand. This is something we decided early
on was not vital to our V1 and could be easily implemented in V2.
Including all team member's restaurants into the city was a difficult task that we thought if done successfully would set us apart 
from a lot of other teams.
only one of the restaurants can successfully order from the market.  The others have enough inventoy so it is not necessary.
######PersonAgent:
######GUI:
ScreenFactory:

WorldClock timer:

RoleFactory:

#####For V2

Although we have robots in now, all of the jobs are set up for roles so we can easily transition to sending people to work
to populate the business and have them running in V2.

we weren't able to find out the perfect "hour length" for our program to run realistically.  By that we mean, the program may
run in a loop (e.g. the person keeps going to the restaurant and to the home repeatedly because every four hours he gets hungry
and each hour could be 5 seconds but in 20 seconds he will need to go to the restaurant to eat again) due to short hour lengths.
We kept it short for faster testing (and also for faster grading) since watching a program simulate for 5-15 minutes (representativ
of a full day) can take a toll on productivity.
