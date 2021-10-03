# Schedule service

* DATABASE_URL=localhost:3306
* DB_NAME:feight_jobs
* DB_USER:root
* DB_PWD:123456
* CONNECTION_POOL_SIZE:10
* PAYMENT_QUERY_INTERVAL: 600000 # 600 second
* queryAfter: 30 # 30 minutes
* ORDER_HOURS_GRACE_PERIOD: 48 # number of hours order pending will be cancelled
* COMMERCE_SERVICE_ENDPOINT:http://localhost:7777