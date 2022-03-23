Business Need: Be able to scrap unvisited locations async from the requests received from web clients.
  One client can create multiple scrap requests but the scrapping process will be triggered only once because we need to
  prevent populating the DB with duplicate values.
  Be able to retrieve the status for a scrap process the client triggered.

Scenario: Scrap an unvisited location async successfully
  Given 1 unvisited location
  When web client triggers scrap process
  Then 1 proof of work is returned with status "in_progress"
  When the scrap process finishes
  Then 1 proof of work is returned with status "finished"
  And articles could be retrieved from DB

#Scenario: Scrap an unvisited location async with multiple scrap trigger requests during the scrapping process
#  Given 1 unvisited location
#  When web client triggers scrap process
#  Then 1 proof of work is returned with status "in_progress"
#  When web client triggers scrap process
#  Then 1 proof of work is returned with status "in_progress"
#  When the scrap process finishes
#  Then 1 proof of work is returned with status "finished"
#  And articles could be retrieved from DB
#
#Scenario: Scrap unvisited locations async with multiple scrap trigger requests while new scrapping locations are added
#  Given 1 unvisited location
#  When web client triggers scrap process
#  Then 1 proof of work is returned with status "in_progress"
#  When add 1 unvisited location
#  When web client triggers scrap process
#  Then 2 proof of work is returned with status "in_progress"
#  When the scrap process finishes
#  Then 1 proof of work is returned with status "finished"
#  Then 2 proof of work is returned with status "finished"
#  And articles could be retrieved from DB
#  And 2 proof of work are stored in DB