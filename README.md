
#ARFE

### Scrapping Data

Article Feeding (ArFe) retrieves data from scientific articles and store it on DB.
This will be further used by another API to summarize and post short paragraphs on LinkedIn.

Things to be done:
- [ ] Technical level
  - [ ] Swagger
  - [ ] Stress testing
  - [ ] Load testing
  - [ ] Security
    - [ ] Static security analysis
    - [ ] Dynamic analysis
  - [ ] Monitoring 
  - [x] Logging
  - [ ] CI/CD Azure pipelines
  - [ ] Code coverage
  - [ ] Architectural testing
  - [x] Liquibase
  - [ ] Integration Tests [testcontainers]
  - [ ] Webflux/Reactive
  - [ ] Automatic gradle updates of dependencies
  - [x] Exception handling
  - [ ] Caching
  - [ ] Security
  - [ ] QueryDsl
  
- [ ] Business level
  - [ ] Monitoring of the scrapped articles (how many a day, how many in total)
  - [ ] Web-scrapping report/statistics
  - [ ] Configuration modification through REST
  - [ ] REST trigger for specific location scrapping
  - [ ] Scheduler in place for daily/weekly/monthly scrapping


### Scraping Video Links
