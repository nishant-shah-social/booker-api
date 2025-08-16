* GET -> Concerned booking is not returned when the filter -> check-in date is exactly equal to booking check-in date.
* PATCH -> If I try to update only checkin date then it automatically makes checkout value as NaN. Same happens when
  only checkout
  date is updated, checkin becomes NaN
* PATCH -> Non existent bookingId, returns status code 405 method not supported rather than 404 not found
* PATCH -> With incorrect data type (e.g. firstname as integer and totalPrice as string, is success, I was expecting 400
  bad params or a proper error message
* Delete -> Non existent bookingId, returns tatus code 405 method not supported rather than 404 not found
* PATCH -> Trying to patch with a non-existent field returns 200
* PATCH -> API Documentation says PUT instead of PATCH
* Lot of API calls took more than 2 seconds for responding. So, I have kept 5 seconds as the threshold in the tests so
  that we dont see lot of failures.