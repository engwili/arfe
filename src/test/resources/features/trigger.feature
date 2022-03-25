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